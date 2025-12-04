<script lang="ts" setup>
import Search from './search.vue'
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store'
import type { RouteRecordRaw } from 'vue-router'
import { useRouteStore } from '@/store/modules/route.ts'
import { filterTree } from '@/utils/index.ts'
import { useFullscreen } from '@vueuse/core'
import { ElMessageBox, ElMessage, } from 'element-plus'
import Message from './message.vue'
import { useAppStore } from '@/store/modules/app.ts'
import Theme from './theme.vue'
import Drawer from './drawer.vue'
let appStore = useAppStore()
// 基本使用
const { isFullscreen, enter, exit, toggle } = useFullscreen()
let routeStore = useRouteStore()
let drawer = ref()
defineOptions({
    name: 'Tabber'
})
let userStore = useUserStore()
const router = useRouter()
// 退出登录
const logout = () => {
    ElMessageBox.confirm(
        '确认退出登录？',
        '提示',
        {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning',
        }
    )
        .then(async () => {
            try {
                await userStore.logout()
                await router.replace('/login')
                return true
            } catch (error) {
                return false
            }
        },)
        .catch(() => {
            ElMessage({
                type: 'info',
                message: 'Delete canceled',
            })
        })

}
</script>

<template>
    <div class="right">
        <div class="tooltip">
            <el-tooltip placement="top">

                <Search>
                </Search>
                <template #content> multiple lines<br />second line </template>

            </el-tooltip>
        </div>

        <div class="tooltip">
            <el-tooltip placement="top">

                <GiSvgIcon name="settings" @click="drawer?.open"></GiSvgIcon>
                <template #content> multiple lines<br />second line </template>

            </el-tooltip>
        </div>


        <div class="tooltip">

            <el-popover class="box-item" placement="bottom" width="300px">
                <template #reference>
                    <GiSvgIcon name="notification"></GiSvgIcon>
                </template>
                <template #default>
                    <div>
                        <Message></Message>
                    </div>
                </template>
            </el-popover>
        </div>


        <div class="tooltip">
            <el-tooltip placement="top" :content="isFullscreen ? '退出全屏' : '全屏'">
                <GiSvgIcon name="fullscreen" v-if="!isFullscreen" @click="toggle"></GiSvgIcon>
                <GiSvgIcon name="fullscreen-exit" v-else @click="exit"></GiSvgIcon>
                <!-- <template #content> multiple lines<br />second line </template> -->

            </el-tooltip>
        </div>


        <div class="tooltip">
            <Theme></Theme>
        </div>


        <div class="tooltip">
            <el-icon>
                <User />
            </el-icon>
            <el-dropdown>
                <span class="el-dropdown-link">
                    超级管理员
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="router.push('/user/profile')">
                            <span>个人中心</span>
                        </el-dropdown-item>
                        <el-dropdown-item @click="router.push('/user/message')">
                            <span>消息中心</span>
                        </el-dropdown-item>
                        <el-dropdown-item @click="logout">
                            <span>退出登录</span>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>

    </div>
    <Drawer ref="drawer"></Drawer>
</template>

<style lang="scss" scoped>
// :focus-visible {
//     outline: none;
// }

.right {
    display: flex;
    width: 100%;
    height: 100%;
    align-items: center;
    margin: 0 20px;
    justify-content: flex-end;
    white-space: nowrap;
    // overflow: hidden;

    .tooltip {
        margin-left: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;

        // flex-wrap: nowrap;
        .el-dropdown-link {
            font-size: 1em;
            margin-left: 15px;
        }
    }
}
</style>