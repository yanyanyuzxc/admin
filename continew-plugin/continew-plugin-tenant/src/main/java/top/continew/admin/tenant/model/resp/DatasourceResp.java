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

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.tenant.model.enums.DatasourceDatabaseTypeEnum;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;

/**
 * 数据源响应参数
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 19:13
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "数据源响应参数")
public class DatasourceResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "T0001数据源")
    @ExcelProperty(value = "名称", order = 2)
    private String name;

    /**
     * 数据库类型
     */
    @Schema(description = "数据库类型", example = "1")
    @ExcelProperty(value = "数据库类型", converter = ExcelBaseEnumConverter.class, order = 3)
    private DatasourceDatabaseTypeEnum databaseType;

    /**
     * 主机
     */
    @Schema(description = "主机", example = "123.56.195.68")
    @ExcelProperty(value = "主机", order = 4)
    private String host;

    /**
     * 端口
     */
    @Schema(description = "端口", example = "3306")
    @ExcelProperty(value = "端口", order = 5)
    private Integer port;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "root")
    @ExcelProperty(value = "用户名", order = 6)
    private String username;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "T0001数据源描述")
    @ExcelProperty(value = "描述", order = 7)
    private String description;
}