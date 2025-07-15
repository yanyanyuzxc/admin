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

package top.continew.admin.tenant.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.continew.starter.core.enums.BaseEnum;
import top.continew.starter.extension.tenant.enums.TenantIsolationLevel;

/**
 * 租户隔离级别枚举
 *
 * @author Charles7c
 * @since 2025/7/14 20:31
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public enum TenantIsolationLevelEnum implements BaseEnum<Integer> {

    /**
     * 行级
     */
    LINE(1, "行级", TenantIsolationLevel.LINE),

    /**
     * 数据源级
     */
    DATASOURCE(2, "数据源级", TenantIsolationLevel.DATASOURCE);

    private final Integer value;
    private final String description;
    private final TenantIsolationLevel level;
}
