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

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.tenant.service.TenantService;
import top.continew.starter.extension.tenant.autoconfigure.TenantProperties;
import top.continew.starter.extension.tenant.config.TenantProvider;
import top.continew.starter.extension.tenant.context.TenantContext;
import top.continew.starter.extension.tenant.enums.TenantIsolationLevel;

/**
 * 默认租户提供者
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 15:35
 */
@Service
@RequiredArgsConstructor
public class DefaultTenantProvider implements TenantProvider {

    private final TenantProperties tenantProperties;
    private final TenantService tenantService;

    @Override
    public TenantContext getByTenantId(String tenantIdAsString, boolean verify) {
        TenantContext context = new TenantContext();
        context.setIsolationLevel(TenantIsolationLevel.LINE);
        // 超级租户
        Long superTenantId = tenantProperties.getSuperTenantId();
        if (StrUtil.isBlank(tenantIdAsString) || superTenantId.toString().equals(tenantIdAsString)) {
            context.setTenantId(superTenantId);
            return context;
        }
        // 获取租户信息
        Long tenantId = Long.valueOf(tenantIdAsString);
        tenantService.checkStatus(tenantId);
        context.setTenantId(tenantId);
        return context;
    }
}
