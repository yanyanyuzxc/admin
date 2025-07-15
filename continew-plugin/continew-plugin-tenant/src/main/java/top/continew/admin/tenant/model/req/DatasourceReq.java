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

package top.continew.admin.tenant.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.tenant.model.enums.DatasourceDatabaseTypeEnum;
import top.continew.starter.extension.crud.validation.CrudValidationGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据源创建或修改请求参数
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 19:13
 */
@Data
@Schema(description = "数据源创建或修改请求参数")
public class DatasourceReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "T0001数据源")
    @NotBlank(message = "名称不能为空")
    @Length(max = 30, message = "名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 数据库类型
     */
    @Schema(description = "数据库类型", example = "1")
    @NotNull(message = "数据库类型无效")
    private DatasourceDatabaseTypeEnum databaseType;

    /**
     * 主机
     */
    @Schema(description = "主机", example = "123.56.195.68")
    @NotBlank(message = "主机不能为空")
    @Length(max = 128, message = "主机长度不能超过 {max} 个字符")
    private String host;

    /**
     * 端口
     */
    @Schema(description = "端口", example = "3306")
    @NotNull(message = "端口不能为空")
    private Integer port;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "root")
    @NotBlank(message = "用户名不能为空")
    @Length(max = 128, message = "用户名长度不能超过 {max} 个字符")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "jXo1Mwsuoz+XhLy6tOhdzbTJ3gIDxciTAnCjcOO8akglghVDO3jR5pqOp95LkSBp1Yd9bltYzWDNjNvL6yD3TQ==")
    @NotBlank(message = "密码不能为空", groups = CrudValidationGroup.Create.class)
    private String password;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "T0001数据源描述")
    @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
    private String description;
}