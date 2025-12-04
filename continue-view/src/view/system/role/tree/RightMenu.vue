<script lang="ts" setup>

import type { RoleResp } from '@/apis/system/role'
import { ref } from 'vue';
interface Props {
    data: RoleResp
}

const props = withDefaults(defineProps<Props>(), {})

const emit = defineEmits<{
    (e: 'on-menu-item-click', mode: string, data: RoleResp): void
}>()


// 点击菜单项
const onClick = (mode: string) => {
    emit('on-menu-item-click', mode, props.data)
}

</script>

<template>
    <el-menu class="right-menu">
        <el-menu-item v-permission="['system:role:update']" title="修改" @click="onClick('update')">
            <span>修改</span>
        </el-menu-item>
        <el-menu-item v-permission="['system:role:delete']" class="danger" :disabled="data.isSystem"
            :title="data.isSystem ? '该角色为系统内置角色' : '删除'" @click="onClick('delete')">
            <span>删除</span>
        </el-menu-item>
    </el-menu>
</template>

<style lang="scss" scoped>
.el-menu {
    border-right: 0px !important;
}
</style>