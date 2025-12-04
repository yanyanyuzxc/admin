<script setup lang="tsx">
import { useRoute, useRouter } from 'vue-router'
import MyMessage from './components/MyMessage.vue'
import MyNotice from './components/MyNotice.vue'
import { useDevice } from '@/hooks'

import mittBus from '@/utils/mitt'

defineOptions({ name: 'UserMessage' })


const { isDesktop } = useDevice()

const unreadMessageCount = ref(0)
const unreadNoticeCount = ref(0)

const tabItems = computed(() => [
    { key: 'msg', title: '我的消息', count: unreadMessageCount.value },
    { key: 'notice', title: '我的公告', count: unreadNoticeCount.value },
])
const TabPaneTitle = defineComponent({
    props: {
        title: { type: String, required: true },
        count: { type: Number, default: 0 },
    },
    setup(props) {
        return () => (
            <div class="tab-pane-item">
                <div>{props.title}</div>
                <a-badge count={props.count} max-count={99} />
            </div>
        )
    },
})



const menuList = [
    { name: '我的消息', key: 'msg', value: MyMessage },
    { name: '我的公告', key: 'notice', value: MyNotice },
]

const route = useRoute()
const router = useRouter()
const activeKey = ref('msg')
watch(
    () => route.query,
    () => {
        if (route.query.tab) {
            activeKey.value = String(route.query.tab)
        }
    },
    { immediate: true },
)
const change = (key: string | number) => {
    activeKey.value = key as string
    console.log(activeKey.value)
    router.replace({ path: route.path, query: { tab: key } })
}

</script>

<template>
    <GiPageLayout>
        <template #left>

            <el-tabs class="custom-tabs" v-model="activeKey" @tab-change="change" tab-position="left" size="large">
                <el-tab-pane v-for="item in tabItems" :key="item.key" :name="item.key">
                    <template #label>
                        <div class="tab-pane-item">
                            <el-badge :value="item.count" max="99" :show-zero="false">
                                <div>{{ item.title }}</div>
                            </el-badge>


                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
        </template>
        <component :is="menuList.find((item) => item.key === activeKey)?.value"></component>
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