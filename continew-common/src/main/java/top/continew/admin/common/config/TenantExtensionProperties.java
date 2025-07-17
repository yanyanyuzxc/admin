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

package top.continew.admin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.continew.starter.core.constant.PropertiesConstants;

import java.util.List;

/**
 * 租户扩展配置属性
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/11/29 12:05
 */
@Data
@Component
@ConfigurationProperties(prefix = PropertiesConstants.TENANT)
public class TenantExtensionProperties {

    /**
     * 请求头中租户编码键名
     */
    private String tenantCodeHeader;

    /**
     * 忽略菜单 ID（租户不能使用的菜单）
     */
    private List<Long> ignoreMenus;
}
