<script lang="ts" setup>
import RoleTree from './tree/index.vue'
import Permission from './components/Permission.vue'
import RoleUser from './components/RoleUser.vue'
import { ref } from 'vue'
const activeName = ref('功能权限')
const roleId = ref('1')
// 根据选中角色查询
const handleSelectRole = (keys: any) => {
    // console.log(keys)
    console.log(keys)
    roleId.value = keys ? keys.id : 1

}
</script>

<template>
    <GiPageLayout>
        <template #left>
            <RoleTree @node-click="handleSelectRole" />
        </template>
        <template #header>
            <el-tabs v-model="activeName">
                <el-tab-pane label="功能权限" name="功能权限"></el-tab-pane>
                <el-tab-pane label="角色用户" name="角色用户"></el-tab-pane>
            </el-tabs>
        </template>
        <component :is="Permission" v-if="activeName === '功能权限'" :role-id="roleId"></component>
        <component :is="RoleUser" v-if="activeName === '角色用户'" :role-id="roleId"></component>
    </GiPageLayout>
</template>

<style lang="scss" scoped>
::v-deep .el-tabs__item {
    margin-left: 10px !important;
}
</style>