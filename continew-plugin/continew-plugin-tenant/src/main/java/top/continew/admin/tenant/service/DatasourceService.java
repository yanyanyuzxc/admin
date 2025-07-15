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

package top.continew.admin.tenant.service;

import top.continew.admin.common.base.service.BaseService;
import top.continew.admin.tenant.model.entity.DatasourceDO;
import top.continew.admin.tenant.model.query.DatasourceQuery;
import top.continew.admin.tenant.model.req.DatasourceReq;
import top.continew.admin.tenant.model.resp.DatasourceDetailResp;
import top.continew.admin.tenant.model.resp.DatasourceResp;
import top.continew.starter.data.service.IService;

/**
 * 数据源业务接口
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 19:13
 */
public interface DatasourceService extends BaseService<DatasourceResp, DatasourceDetailResp, DatasourceQuery, DatasourceReq>, IService<DatasourceDO> {

    /**
     * 测试连接
     *
     * @param id ID
     */
    void testConnection(Long id);

    /**
     * 初始化数据库
     *
     * @param databaseName 数据库名称
     * @param id           ID
     */
    void initDb(String databaseName, Long id);
}