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

package top.continew.admin.tenant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.base.controller.BaseController;
import top.continew.admin.tenant.model.query.DatasourceQuery;
import top.continew.admin.tenant.model.req.DatasourceReq;
import top.continew.admin.tenant.model.resp.DatasourceDetailResp;
import top.continew.admin.tenant.model.resp.DatasourceResp;
import top.continew.admin.tenant.service.DatasourceService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

/**
 * 数据源管理 API
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/12/12 19:13
 */
@Tag(name = "数据源管理 API")
@RestController
@CrudRequestMapping(value = "/tenant/datasource", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE})
public class DatasourceController extends BaseController<DatasourceService, DatasourceResp, DatasourceDetailResp, DatasourceQuery, DatasourceReq> {

    @Operation(summary = "测试连接", description = "测试数据源连接可用性")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @SaCheckPermission("tenant:datasource:testConnection")
    @PostMapping("/{id}/test/connection")
    public void testConnection(@PathVariable Long id) {
        baseService.testConnection(id);
    }
}