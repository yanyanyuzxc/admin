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
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 租户配置属性
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/11/29 12:05
 */
@Data
@Component
public class TenantProperties extends top.continew.starter.extension.tenant.autoconfigure.TenantProperties {

    /**
     * 忽略菜单 ID（租户不能使用的菜单）
     */
    private List<Long> ignoreMenus;
}
