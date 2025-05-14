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

package top.continew.admin.system.model.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import top.continew.admin.common.model.entity.BaseDO;
import top.continew.admin.system.enums.FileTypeEnum;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.util.StrUtils;

import java.io.Serial;
import java.util.Map;

/**
 * 文件实体
 *
 * @author Charles7c
 * @since 2023/12/23 10:38
 */
@Data
@NoArgsConstructor
@TableName("sys_file")
public class FileDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 大小（字节）
     */
    private Long size;

    /**
     * URL
     */
    private String url;

    /**
     * 上级目录
     */
    private String parentPath;

    /**
     * 绝对路径
     */
    private String absPath;

    /**
     * 扩展名
     */
    private String extension;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 类型
     */
    private FileTypeEnum type;

    /**
     * SHA256值
     */
    private String sha256;

    /**
     * 元数据
     */
    private String metadata;

    /**
     * 缩略图大小（字节)
     */
    private Long thumbnailSize;

    /**
     * 缩略图 URL
     */
    private String thumbnailUrl;

    /**
     * 缩略图元数据
     */
    private String thumbnailMetadata;

    /**
     * 存储 ID
     */
    private Long storageId;

    /**
     * 基于 {@link FileInfo} 构建文件信息对象
     *
     * @param fileInfo {@link FileInfo} 文件信息
     */
    public FileDO(FileInfo fileInfo) {
        this.name = FileNameUtil.getPrefix(EscapeUtil.unescape(fileInfo.getOriginalFilename()));
        this.size = fileInfo.getSize();
        this.url = fileInfo.getUrl();
        this.absPath = StrUtil.isEmpty(fileInfo.getPath())
            ? StringConstants.SLASH
            : StrUtil.prependIfMissing(fileInfo.getPath(), StringConstants.SLASH);
        String[] pathAttr = this.absPath.split(StringConstants.SLASH);
        this.parentPath = pathAttr.length > 1 ? pathAttr[pathAttr.length - 1] : StringConstants.SLASH;
        this.extension = fileInfo.getExt();
        this.contentType = fileInfo.getContentType();
        this.type = FileTypeEnum.getByExtension(this.extension);
        this.sha256 = fileInfo.getHashInfo().getSha256();
        this.metadata = JSONUtil.toJsonStr(fileInfo.getMetadata());
        this.thumbnailSize = fileInfo.getThSize();
        this.thumbnailUrl = fileInfo.getThUrl();
        this.thumbnailMetadata = JSONUtil.toJsonStr(fileInfo.getThMetadata());
        this.setCreateTime(DateUtil.toLocalDateTime(fileInfo.getCreateTime()));
    }

    /**
     * 转换为 {@link FileInfo} 文件信息对象
     *
     * @param storage 存储配置信息
     * @return {@link FileInfo} 文件信息对象
     */
    public FileInfo toFileInfo(StorageDO storage) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPlatform(storage.getCode());
        fileInfo.setFilename(StrUtil.contains(this.url, StringConstants.SLASH)
            ? StrUtil.subAfter(this.url, StringConstants.SLASH, true)
            : this.url);
        fileInfo.setOriginalFilename(StrUtils
            .blankToDefault(this.extension, this.name, ex -> this.name + StringConstants.DOT + ex));
        fileInfo.setBasePath(StringConstants.EMPTY);
        fileInfo.setSize(this.size);
        fileInfo.setUrl(this.url);
        fileInfo.setPath(StringConstants.SLASH.equals(this.absPath)
            ? StringConstants.EMPTY
            : StrUtil.removePrefix(this.absPath, StringConstants.SLASH));
        fileInfo.setExt(this.extension);
        if (StrUtil.isNotBlank(this.metadata)) {
            fileInfo.setMetadata(JSONUtil.toBean(this.metadata, Map.class));
        }
        // 缩略图信息
        fileInfo.setThFilename(StrUtil.contains(this.thumbnailUrl, StringConstants.SLASH)
            ? StrUtil.subAfter(this.thumbnailUrl, StringConstants.SLASH, true)
            : this.thumbnailUrl);
        fileInfo.setThSize(this.thumbnailSize);
        fileInfo.setThUrl(this.thumbnailUrl);
        if (StrUtil.isNotBlank(this.thumbnailMetadata)) {
            fileInfo.setThMetadata(JSONUtil.toBean(this.thumbnailMetadata, Map.class));
        }
        return fileInfo;
    }
}
