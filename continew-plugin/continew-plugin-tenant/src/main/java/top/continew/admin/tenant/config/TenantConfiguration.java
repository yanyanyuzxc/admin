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

package top.continew.admin.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.continew.admin.tenant.service.TenantService;
import top.continew.starter.extension.tenant.annotation.ConditionalOnEnabledTenant;
import top.continew.starter.extension.tenant.autoconfigure.TenantProperties;
import top.continew.starter.extension.tenant.config.TenantProvider;

/**
 * 租户配置
 *
 * @author Charles7c
 * @since 2025/7/12 13:30
 */
@Configuration
public class TenantConfiguration {

    @Autowired(required = false)
    private TenantProperties tenantProperties;

    /**
     * API 文档分组配置
     */
    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder().group("tenant").displayName("租户管理").pathsToMatch("/tenant/**").build();
    }

    /**
     * 租户扩展配置属性
     */
    @Bean
    public TenantExtensionProperties tenantExtensionProperties() {
        return new TenantExtensionProperties();
    }

    /**
     * 租户行级隔离处理器（默认），等待 ContiNew Starter 2.13.2 发布后替换
     */
    @Bean
    @ConditionalOnEnabledTenant
    public TenantLineHandler tenantLineHandler() {
        return new DefaultTenantLineHandler(tenantProperties);
    }

    /**
     * 租户提供者
     */
    @Bean
    @ConditionalOnEnabledTenant
    public TenantProvider tenantProvider(TenantExtensionProperties tenantExtensionProperties,
                                         TenantService tenantService) {
        return new DefaultTenantProvider(tenantExtensionProperties, tenantService);
    }
}
