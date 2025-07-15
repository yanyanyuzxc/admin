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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import top.continew.admin.tenant.model.req.DatasourceReq;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.core.enums.BaseEnum;
import top.continew.starter.core.exception.BusinessException;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Map;

/**
 * 数据源数据库类型枚举
 *
 * @author Charles7c
 * @author 小熊
 * @since 2024/11/26 17:20
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public enum DatasourceDatabaseTypeEnum implements BaseEnum<Integer> {

    /**
     * MySQL
     */
    MYSQL(1, "MySQL", "com.mysql.cj.jdbc.Driver") {
        @Override
        public DataSource buildDataSource(DatasourceReq datasource) {
            return DataSourceBuilder.create()
                .url(this.getJdbcUrl(datasource, null))
                .driverClassName(this.getDriverClassName())
                .username(datasource.getUsername())
                .password(datasource.getPassword())
                .build();
        }

        @Override
        public void testConnection(DatasourceReq datasource) {
            DataSource dataSource = this.buildDataSource(datasource);
            try (Connection ignored = dataSource.getConnection()) {
                log.info("数据源 [{}] 测试连接成功", datasource.getName());
            } catch (Exception e) {
                throw new BusinessException("数据源 [%s] 测试连接失败".formatted(datasource.getName()));
            }
        }

        @Override
        public String getJdbcUrl(DatasourceReq datasource, String databaseName) {
            StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://%s:%s".formatted(datasource.getHost(), datasource
                .getPort()));
            if (StrUtil.isNotBlank(databaseName)) {
                urlBuilder.append(StringConstants.SLASH).append(databaseName);
                urlBuilder.append(StringConstants.QUESTION_MARK);
                urlBuilder.append(URLUtil.buildQuery(this.getDefaultParameters(), StandardCharsets.UTF_8));
            }
            return urlBuilder.toString();
        }

        @Override
        public Map<String, String> getDefaultParameters() {
            Map<String, String> parameter = MapUtil.newHashMap(8);
            parameter.put("serverTimezone", "Asia/Shanghai");
            parameter.put("useSSL", "true");
            parameter.put("useUnicode", "true");
            parameter.put("characterEncoding", "utf8");
            parameter.put("rewriteBatchedStatements", "true");
            parameter.put("autoReconnect", "true");
            parameter.put("allowPublicKeyRetrieval", "true");
            parameter.put("nullCatalogMeansCurrent", "true");
            return parameter;
        }
    };

    private final Integer value;
    private final String description;
    private final String driverClassName;

    /**
     * 构建数据源
     *
     * @param datasource 数据源配置
     * @return 数据源
     */
    public abstract DataSource buildDataSource(DatasourceReq datasource);

    /**
     * 测试连接
     *
     * @param datasource 数据源配置
     */
    public abstract void testConnection(DatasourceReq datasource);

    /**
     * 获取 JDBC URL
     *
     * @param datasource   数据源配置
     * @param databaseName 数据库名称
     * @return JDBC URL
     */
    public abstract String getJdbcUrl(DatasourceReq datasource, String databaseName);

    /**
     * 获取默认数据库连接参数
     *
     * @return 默认数据库连接参数
     */
    public abstract Map<String, String> getDefaultParameters();
}
