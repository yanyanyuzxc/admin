<script lang="ts" setup>
import { useDevice } from '@/hooks'
import SiteConfig from './site/index.vue'
import SecurityConfig from './security/index.vue'
import LoginConfig from './login/index.vue'
import MailConfig from './mail/index.vue'
import SmsConfig from './sms/index.vue'
import StorageConfig from './storage/index.vue'
import ClientConfig from './client/index.vue'
import has from '@/utils/has'
const { isDesktop } = useDevice()

const data = [
    { name: '网站配置', key: 'site', icon: 'apps', permissions: ['system:siteConfig:get'], value: SiteConfig },
    { name: '安全配置', key: 'security', icon: 'safe', permissions: ['system:securityConfig:get'], value: SecurityConfig },
    { name: '登录配置', key: 'login', icon: 'lock', permissions: ['system:loginConfig:get'], value: LoginConfig },
    { name: '邮件配置', key: 'mail', icon: 'email', permissions: ['system:mailConfig:get'], value: MailConfig },
    { name: '短信配置', key: 'sms', icon: 'message', permissions: ['system:smsConfig:list'], value: SmsConfig },
    { name: '存储配置', key: 'storage', icon: 'storage', permissions: ['system:storage:list'], value: StorageConfig },
    { name: '客户端配置', key: 'client', icon: 'mobile', permissions: ['system:client:list'], value: ClientConfig },
]

const menuList = computed(() => {
    return data.filter((item) => {
        return has.hasPermOr(item.permissions)
    })
})
const activeKey = ref(menuList.value[0].key)
const router = useRouter()
const route = useRoute()
const change = (key: string | number) => {
    activeKey.value = key as string
    router.replace({ path: route.path, query: { tab: key } })
}
</script>

<template>
    <GiPageLayout :margin="true" :default-collapsed="false">
        <template v-if="isDesktop" #left>
            <el-tabs class="custom-tabs" v-model="activeKey" type="rounded" tab-position="left" @tab-change="change">
                <el-tab-pane v-for="(item) in menuList" :key="item.key" :name="item.key">
                    <template #label>
                        <div style="display: flex; align-items: center;font-size:1.1em">
                            <GiSvgIcon :name="item.icon" size="18" style="margin-right: 4px" />
                            {{ item.name }}
                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
        </template>
        <template #header>
            <el-tabs v-if="!isDesktop" v-model="activeKey" type="rounded" tab-position="top" size="large"
                @change="change">
                <el-tab-pane v-for="(item) in menuList" :key="item.key" :title="item.name">
                    <template #label>
                        <div style="display: flex; align-items: center;width:100%;">
                            <GiSvgIcon :name="item.icon" :size="18" style="margin-right: 4px" />
                            {{ item.name }}
                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
        </template>
        <transition name="fade-slide" mode="out-in" appear>
            <component :is="menuList.find((item) => item.key === activeKey)?.value"></component>
        </transition>
    </GiPageLayout>
</template>

<style lang="scss" scoped>
:deep(.custom-tabs.el-tabs--left .el-tabs__item.is-left,
    .custom-tabs.el-tabs--right .el-tabs__item.is-left) {
    justify-content: flex-start;
    margin-top: 10px;
}

:deep(.custom-tabs.el-tabs--left .el-tabs__nav-wrap.is-left:after) {
    display: none;
}

:deep(.custom-tabs.el-tabs__content) {
    display: none;
}

:deep(.custom-tabs .el-tabs__item.is-active) {
    width: 200px;
    background: rgb(238, 238, 240);
    color: $color-theme;
    border-radius: 5px;
}

.el-tabs {
    margin: 10px;
}
</style>