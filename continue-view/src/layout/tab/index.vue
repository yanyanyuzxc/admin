<script lang="ts" setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter, type RouteLocationNormalized } from 'vue-router';
import { toRaw } from 'vue';
import { useTabStore, useAppStore } from '@/store';
//控制标签页的显示
const route = useRoute();
const router = useRouter();
const tabsStore = useTabStore();
const tabList = tabsStore.tabList;
const appStore = useAppStore();
watch(
    () => route.fullPath,
    () => {
        handleRouteChange()
    },
)
tabsStore.init()
const handleRouteChange = () => {
    const item = { ...route } as unknown as RouteLocationNormalized
    tabsStore.addTabItem(toRaw(item))
    tabsStore.addCacheItem(toRaw(item))
}
handleRouteChange()
import {
    Check,
    CircleCheck,
    CirclePlus,
    CirclePlusFilled,
    Plus,
} from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { ElMessage, type TabPaneName, type TabsPaneContext } from 'element-plus'
import type { DropdownInstance } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
components: { draggable }
const dropdownRef = ref<DropdownInstance>()
// const position = ref({
//     top: 0,
//     left: 0,
//     bottom: 0,
//     right: 0,
// })
const position = ref<DOMRect>(new DOMRect(0, 0, 0, 0))
const triggerRef = ref({
    getBoundingClientRect: () => position.value,
})
const loading = ref(false)
const reload = () => {
    if (loading.value) return
    loading.value = true
    tabsStore.reloadPage()
    setTimeout(() => {
        loading.value = false
    }, 600)
}
const handleClick = () => {
    dropdownRef.value?.handleClose()
}
let currentpath = ref('')
const handleContextmenu = (event: MouseEvent, path: string) => {
    currentpath.value = path
    const { clientX, clientY } = event
    position.value = new DOMRect(clientX, clientY, 0, 0)
    event.preventDefault()
    dropdownRef.value?.handleOpen()
}
const handleTabClick = (key: any) => {
    key = key.paneName
    const obj = tabsStore.tabList.find((i) => i.path === key)
    obj ? router.push(obj.fullPath || obj.path) : router.push(String(key))
}
// const dragOptions = computed(() => ({
//     animation: 200, // 动画时长
//     ghostClass: 'ghost', // 拖拽时的幽灵类
//     chosenClass: 'chosen', // 被选中项的类
//     dragClass: 'drag', // 正在拖拽的类
//     filter: '.unmover', // 不可拖拽的类
//     preventOnFilter: true, // 过滤时阻止默认事件
// }))

// // 拖拽开始时的处理
// const onDragStart = (event: any) => {
//     console.log('开始拖拽:', event)
// }

// // 拖拽结束时的处理
// const onDragEnd = (event: any) => {
//     console.log('结束拖拽:', event)
//     // 可以在这里保存标签页顺序到本地存储
//     saveTabOrder()
// }
// const onMove = (event: any) => {
//     // 如果目标位置是不可拖拽的标签，则不允许放置
//     if (event.relatedContext.element.meta?.affix) {
//         return false
//     }
//     return true
// }

// // 保存标签页顺序
// const saveTabOrder = () => {
//     try {
//         localStorage.setItem('tab-order', JSON.stringify(tabsStore.tabList.map(tab => tab.path)))
//         ElMessage.success('标签页顺序已保存')
//     } catch (error) {
//         console.error('保存标签页顺序失败:', error)
//     }
// }
// const loadTabOrder = () => {
//     try {
//         const savedOrder = localStorage.getItem('tab-order')
//         if (savedOrder) {
//             const order = JSON.parse(savedOrder)
//             // 根据保存的顺序重新排列标签页
//             tabsStore.tabList.sort((a, b) => {
//                 return order.indexOf(a.path) - order.indexOf(b.path)
//             })
//         }
//     } catch (error) {
//         console.error('加载标签页顺序失败:', error)
//     }
// }

// 初始化时加载保存的顺序
// loadTabOrder()
</script>
<template>
    <div class="tag">
        <div class="left">
            <!-- <draggable v-model="tabsStore.tabList" v-bind="dragOptions" tag="div" class="draggable-tabs"
                @start="onDragStart" @end="onDragEnd" :move="onMove" item-key="path"> -->
            <!-- <el-tabs type="border-card" class="demo-tabs" @click="handleClick" size="medium"
                @tab-remove="tabsStore.closeCurrent" @tab-click="handleTabClick" v-model="route.path">
                <el-tab-pane class="demo-tabs" v-for="item of tabsStore.tabList" :key="item.path"
                    :closable="Boolean(!item.meta?.affix)" :name="item.path">
                    <template #label>
                        <span @contextmenu="(event) => handleContextmenu(event, item.path)">{{ item.meta?.title
                            }}</span>

                    </template>
<template #default>
                        <div style="height: 0;"></div>
                    </template>
</el-tab-pane>

</el-tabs> -->
            <el-tabs :type="appStore.tabMode" @click="handleClick" size="medium" @tab-remove="tabsStore.closeCurrent"
                @tab-click="handleTabClick" v-model="route.path">
                <el-tab-pane class="demo-tabs" v-for="item of tabsStore.tabList" :key="item.path"
                    :closable="Boolean(!item.meta?.affix)" :name="item.path">
                    <template #label>

                        <span @contextmenu="(event) => handleContextmenu(event, item.path)">{{ item.meta?.title
                            }}</span>

                    </template>
                </el-tab-pane>

            </el-tabs>
            <!-- </draggable> -->
            <el-dropdown ref="dropdownRef" :virtual-ref="triggerRef" :show-arrow="false" :popper-options="{
                modifiers: [{ name: 'offset', options: { offset: [0, 0] } }],
            }" virtual-triggering trigger="contextmenu" placement="bottom-start">
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :icon="Plus" @click="reload">重新加载</el-dropdown-item>
                        <el-dropdown-item :icon="Close" @click="tabsStore.closeCurrent(currentpath)">
                            <template #default>关闭当前</template>
                        </el-dropdown-item>
                        <el-dropdown-item :icon="CirclePlus"
                            @click="tabsStore.closeLeft(currentpath)">关闭左侧</el-dropdown-item>
                        <el-dropdown-item :icon="Check"
                            @click="tabsStore.closeRight(currentpath)">关闭右侧</el-dropdown-item>
                        <el-dropdown-item :icon="CircleCheck"
                            @click="tabsStore.closeOther(currentpath)">关闭其他</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
        <div class="right">
            <GiSvgIcon name="refresh" size="20" class="refresh"></GiSvgIcon>
            <el-dropdown>
                <span class="el-dropdown-link">

                    <GiSvgIcon name="more-vertical" size="20"></GiSvgIcon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item>Action 1</el-dropdown-item>
                        <el-dropdown-item>Action 2</el-dropdown-item>
                        <el-dropdown-item>Action 3</el-dropdown-item>
                        <el-dropdown-item disabled>Action 4</el-dropdown-item>
                        <el-dropdown-item divided>Action 5</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>


        </div>
    </div>
</template>

<style lang="scss" scoped>
::v-deep .el-tabs--card>.el-tabs__header .el-tabs__nav {
    border: 1px solid var(color-border-1);
}

::v-deep .el-tabs__item {
    color: var(--color-text-2);
    border: 1px solid var(color-border-1);
    border-left: none;
}

::v-deep .el-tabs__item:hover {
    background: var(--color-fill-2);
    color: var(--color-text-1);
}

::v-deep .el-tabs {
    border: 1px solid var(--color-border-1);
}

::v-deep .el-tabs__item.is-active,
.el-tabs__item:hover {
    color: rgb(var(--primary-6));
}

.tag {
    width: 100%;
    height: 100%;
    display: flex;
    margin-top: -7px;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .left {


        overflow-y: hidden;
        height: 50px;


        .el-tabs {
            margin-top: 16px;
            height: 34px;
            overflow-y: hidden;
            // background: var(--color-bg-1) !important;
        }

    }

    .right {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-right: 30px;

        .refresh {
            margin-right: 10px;
            height: 40px;
        }
    }


}
</style>