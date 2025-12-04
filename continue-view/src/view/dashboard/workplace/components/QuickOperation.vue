<template>
    <div class="quick-operation">
        <div class='title'>
            <span>快捷操作</span>
        </div>
        <el-row :gutter="25">
            <el-col v-for="item in links" :key="item.text" :span="8" @click="() => router.push(item.path)">
                <div class="icon">
                    <GiSvgIcon :name="item.icon" size="20"></GiSvgIcon>
                </div>
                <span>{{ item.text }}</span>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
// import has from '@/utils/has'
import { useRouter } from 'vue-router'
const router = useRouter()

const links = [
    { text: '用户管理', icon: 'user', path: '/system/user', permission: 'system:user:list' },
    { text: '角色管理', icon: 'user-group', path: '/system/role', permission: 'system:role:list' },
    { text: '菜单管理', icon: 'menu', path: '/system/menu', permission: 'system:menu:list' },
    { text: '文件管理', icon: 'file', path: '/system/file', permission: 'system:file:list' },
    { text: '代码生成', icon: 'code', path: '/code/generator', permission: 'code:generator:list' },
    { text: '系统日志', icon: 'history', path: '/monitor/log', permission: 'monitor:log:list' },
]
</script>

<style scoped lang="scss">
.quick-operation {
    width: 90%;
    height: 90%;

    padding: 5%;
}

.title {
    font-size: 1.2em;
    color: var(--color-text-3);
}

.el-row ::v-deep {
    padding: 0 !important;
    width: 100%;
    margin: 0px !important;

}

.el-col ::v-deep {
    margin: 0px;
    padding: 0px;
    @include flex-center(column);

    margin-top: 20px;
}

.el-col {
    // color: var(--color-text-3);
}


.icon {
    height: 30px;
    width: 30px;
    background-color: var(--color-bg-4);
    display: flex;
    color: var(--color-text-3);
    justify-content: center;
    align-items: center;
    margin-bottom: 10px;
}

.el-col:hover {
    color: rgb(102, 171, 246);
}

.icon:hover {
    background-color: rgb(199, 222, 247, 0.8);
}

span {
    display: block;
    color: var(--color-text-3);
}
</style>