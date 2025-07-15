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

package top.continew.admin.tenant.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.tenant.model.enums.DatasourceDatabaseTypeEnum;
import top.continew.starter.extension.crud.annotation.DictModel;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;

import java.io.Serial;

/**
 * 数据源实体
 *
 * @author 小熊
 * @since Charles7c
 * @since 2024/12/12 19:13
 */
@Data
@DictModel
@TableName("tenant_datasource")
public class DatasourceDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 数据库类型
     */
    private DatasourceDatabaseTypeEnum databaseType;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @FieldEncrypt
    private String password;

    /**
     * 描述
     */
    private String description;
}