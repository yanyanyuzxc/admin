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

package top.continew.admin.tenant.model.resp;

import cn.crane4j.annotation.AssembleMethod;
import cn.crane4j.annotation.ContainerMethod;
import cn.crane4j.annotation.Mapping;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.tenant.model.enums.TenantIsolationLevelEnum;
import top.continew.admin.tenant.service.DatasourceService;
import top.continew.admin.tenant.service.PackageService;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 租户响应参数
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/11/26 17:20
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "租户响应参数")
public class TenantResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "T0001租户")
    @ExcelProperty(value = "名称", order = 2)
    private String name;

    /**
     * 编码
     */
    @Schema(description = "编码", example = "T0001")
    @ExcelProperty(value = "编码", order = 3)
    private String code;

    /**
     * 域名
     */
    @Schema(description = "域名", example = "https://t0001.continew.top/")
    @ExcelProperty(value = "域名", order = 4)
    private String domain;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间", example = "2023-08-08 08:08:08")
    @ExcelProperty(value = "过期时间", order = 5)
    private LocalDateTime expireTime;

    /**
     * 隔离级别
     */
    @Schema(description = "隔离级别", example = "2")
    @ExcelProperty(value = "隔离级别", converter = ExcelBaseEnumConverter.class, order = 6)
    private TenantIsolationLevelEnum isolationLevel;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "T0001租户描述")
    @ExcelProperty(value = "描述", order = 7)
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 8)
    private DisEnableStatusEnum status;

    /**
     * 套餐 ID
     */
    @Schema(description = "套餐 ID", example = "1")
    @ExcelProperty(value = "套餐 ID", order = 9)
    @AssembleMethod(props = @Mapping(src = "name", ref = "packageName"), targetType = PackageService.class, method = @ContainerMethod(bindMethod = "get", resultType = PackageResp.class))
    private Long packageId;

    /**
     * 数据源 ID
     */
    @Schema(description = "数据源 ID", example = "1")
    @ExcelProperty(value = "数据源 ID", order = 10)
    @AssembleMethod(props = @Mapping(src = "name", ref = "datasourceName"), targetType = DatasourceService.class, method = @ContainerMethod(bindMethod = "get", resultType = DatasourceResp.class))
    private Long datasourceId;

    /**
     * 套餐名称
     */
    @Schema(description = "套餐名称", example = "初级套餐")
    @ExcelProperty(value = "套餐名称", order = 11)
    private String packageName;

    /**
     * 数据源名称
     */
    @Schema(description = "数据源名称", example = "T0001数据源")
    @ExcelProperty(value = "数据源名称", order = 12)
    private String datasourceName;
}