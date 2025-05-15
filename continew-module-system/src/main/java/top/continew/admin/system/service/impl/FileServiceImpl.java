/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.ProgressListener;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.enums.FileTypeEnum;
import top.continew.admin.system.mapper.FileMapper;
import top.continew.admin.system.model.entity.FileDO;
import top.continew.admin.system.model.entity.StorageDO;
import top.continew.admin.system.model.query.FileQuery;
import top.continew.admin.system.model.req.FileReq;
import top.continew.admin.system.model.resp.file.FileResp;
import top.continew.admin.system.model.resp.file.FileStatisticsResp;
import top.continew.admin.system.service.FileService;
import top.continew.admin.system.service.StorageService;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.util.StrUtils;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件业务实现
 *
 * @author Charles7c
 * @since 2023/12/23 10:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends BaseServiceImpl<FileMapper, FileDO, FileResp, FileResp, FileQuery, FileReq> implements FileService {

    private final FileStorageService fileStorageService;
    @Resource
    private StorageService storageService;

    @Override
    public void beforeDelete(List<Long> ids) {
        List<FileDO> fileList = baseMapper.lambdaQuery().in(FileDO::getId, ids).list();
        Map<Long, List<FileDO>> fileListGroup = fileList.stream().collect(Collectors.groupingBy(FileDO::getStorageId));
        for (Map.Entry<Long, List<FileDO>> entry : fileListGroup.entrySet()) {
            StorageDO storage = storageService.getById(entry.getKey());
            for (FileDO file : entry.getValue()) {
                if (!FileTypeEnum.DIR.equals(file.getType())) {
                    FileInfo fileInfo = file.toFileInfo(storage);
                    fileStorageService.delete(fileInfo);
                } else {
                    // 不允许删除非空文件夹
                    String separator = StringConstants.SLASH.equals(file.getPath())
                        ? StringConstants.EMPTY
                        : StringConstants.SLASH;
                    boolean exists = baseMapper.lambdaQuery()
                        .eq(FileDO::getPath, file.getPath() + separator + file.getName())
                        .exists();
                    CheckUtils.throwIf(exists, "文件夹 [{}] 不为空，请先删除文件夹下的内容", file.getName());
                }
            }
        }
    }

    @Override
    public FileInfo upload(MultipartFile file, String path, String storageCode) throws IOException {
        // 校验文件格式
        String extName = FileNameUtil.extName(file.getOriginalFilename());
        List<String> allExtensions = FileTypeEnum.getAllExtensions();
        CheckUtils.throwIf(!allExtensions.contains(extName), "不支持的文件类型，仅支持 {} 格式的文件", String
            .join(StringConstants.CHINESE_COMMA, allExtensions));
        // 构建上传预处理对象
        StorageDO storage = storageService.getByCode(storageCode);
        CheckUtils.throwIf(DisEnableStatusEnum.DISABLE.equals(storage.getStatus()), "请先启用存储 [{}]", storage.getCode());
        UploadPretreatment uploadPretreatment = fileStorageService.of(file)
            .setPlatform(storage.getCode())
            .setHashCalculatorSha256(true)
            .putAttr(ClassUtil.getClassName(StorageDO.class, false), storage)
            .setPath(this.pretreatmentPath(path));
        // 图片文件生成缩略图
        if (FileTypeEnum.IMAGE.getExtensions().contains(extName)) {
            uploadPretreatment.setIgnoreThumbnailException(true, true);
            uploadPretreatment.thumbnail(img -> img.size(100, 100));
        }
        uploadPretreatment.setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.info("开始上传");
            }

            @Override
            public void progress(long progressSize, Long allSize) {
                log.info("已上传 [{}]，总大小 [{}]", progressSize, allSize);
            }

            @Override
            public void finish() {
                log.info("上传结束");
            }
        });
        // 创建父级目录
        this.createDir(path, storage);
        // 上传
        return uploadPretreatment.upload();
    }

    @Override
    public Long countByStorageIds(List<Long> storageIds) {
        if (CollUtil.isEmpty(storageIds)) {
            return 0L;
        }
        return baseMapper.lambdaQuery().in(FileDO::getStorageId, storageIds).count();
    }

    @Override
    public FileStatisticsResp statistics() {
        FileStatisticsResp resp = new FileStatisticsResp();
        List<FileStatisticsResp> statisticsList = baseMapper.statistics();
        if (CollUtil.isEmpty(statisticsList)) {
            return resp;
        }
        resp.setData(statisticsList);
        resp.setSize(statisticsList.stream().mapToLong(FileStatisticsResp::getSize).sum());
        resp.setNumber(statisticsList.stream().mapToLong(FileStatisticsResp::getNumber).sum());
        return resp;
    }

    @Override
    public FileResp check(String fileHash) {
        FileDO file = baseMapper.lambdaQuery().eq(FileDO::getSha256, fileHash).one();
        if (file != null) {
            return get(file.getId());
        }
        return null;
    }

    @Override
    public Long createDir(FileReq req) {
        StorageDO storage = storageService.getDefaultStorage();
        FileDO file = new FileDO();
        file.setName(req.getOriginalName());
        file.setOriginalName(req.getOriginalName());
        file.setSize(0L);
        file.setPath(req.getPath());
        file.setType(FileTypeEnum.DIR);
        file.setStorageId(storage.getId());
        baseMapper.insert(file);
        return file.getId();
    }

    @Override
    protected void fill(Object obj) {
        super.fill(obj);
        if (obj instanceof FileResp fileResp) {
            StorageDO storage = storageService.getById(fileResp.getStorageId());
            String prefix = StrUtil.blankToDefault(storage.getDomain(), storage.getEndpoint());
            String path = fileResp.getPath();
            String url = URLUtil.normalize(prefix + path + StringConstants.SLASH + fileResp.getName(), false, true);
            fileResp.setUrl(url);
            String thumbnailUrl = StrUtils.blankToDefault(fileResp.getThumbnailName(), url, thName -> URLUtil
                .normalize(prefix + path + StringConstants.SLASH + thName, false, true));
            fileResp.setThumbnailUrl(thumbnailUrl);
            fileResp.setStorageName("%s (%s)".formatted(storage.getName(), storage.getCode()));
        }
    }

    /**
     * 处理路径
     *
     * <p>
     * 1.如果 path 为空，则使用 {@link FileService#getDefaultFilePath()} 作为默认值 <br />
     * 2.如果 path 为 {@code /}，则设置为空 <br />
     * 3.如果 path 不以 {@code /} 结尾，则添加后缀 {@code /} <br />
     * 4.如果 path 以 {@code /} 开头，则移除前缀 {@code /} <br />
     * 示例：yyyy/MM/dd/
     * </p>
     *
     * @param path 路径
     * @return 处理路径
     */
    private String pretreatmentPath(String path) {
        if (StrUtil.isBlank(path)) {
            return this.getDefaultFilePath();
        }
        if (StringConstants.SLASH.equals(path)) {
            return StringConstants.EMPTY;
        }
        return StrUtil.appendIfMissing(StrUtil.removePrefix(path, StringConstants.SLASH), StringConstants.SLASH);
    }

    /**
     * 创建文件夹（支持多级）
     *
     * <p>
     * user/avatar/ => user（path：/）、avatar（path：/user）
     * </p>
     *
     * @param dirPath 路径
     * @param storage 存储配置
     */
    private void createDir(String dirPath, StorageDO storage) {
        if (StrUtil.isBlank(dirPath) || StringConstants.SLASH.equals(dirPath)) {
            return;
        }
        // user/avatar/ => user：/、avatar：path：/user
        String[] paths = StrUtil.split(dirPath, StringConstants.SLASH, false, true).toArray(String[]::new);
        Map<String, String> pathMap = MapUtil.newHashMap(paths.length, true);
        for (int i = 0; i < paths.length; i++) {
            String key = paths[i];
            String path = (i == 0)
                ? StringConstants.SLASH
                : StringConstants.SLASH + String.join(StringConstants.SLASH, Arrays.copyOfRange(paths, 0, i));
            pathMap.put(key, path);
        }
        // 创建文件夹
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            String key = entry.getKey();
            String path = entry.getValue();
            if (!baseMapper.lambdaQuery()
                .eq(FileDO::getPath, path)
                .eq(FileDO::getName, key)
                .eq(FileDO::getType, FileTypeEnum.DIR)
                .exists()) {
                FileDO file = new FileDO();
                file.setName(key);
                file.setOriginalName(key);
                file.setSize(0L);
                file.setPath(path);
                file.setType(FileTypeEnum.DIR);
                file.setStorageId(storage.getId());
                baseMapper.insert(file);
            }
        }
    }
}