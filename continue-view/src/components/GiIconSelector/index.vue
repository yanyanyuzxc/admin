<script lang="ts" setup>
import { useUrlSearchParams } from '@vueuse/core'
import { ref } from 'vue'
import { useClipboard } from '@vueuse/core'
import { ElMessage } from 'element-plus'
const iconList: string[] = []
// 自定义图标模块
const SvgIconModules = import.meta.glob('@/assets/icons/*.svg')

interface Props {
    modelValue?: string
    enableCopy?: boolean
}
const props = withDefaults(defineProps<Props>(), {
    modelValue: '',
    enableCopy: false,
})
for (const path in SvgIconModules) {
    const name = path.replace('/src/assets/icons/', '').replace('.svg', '')
    iconList.push(name)
}
const pageSize = 42
const current = ref(1)
const total = ref(iconList.length) // 图标总数
const pageChange = (page: number) => {
    current.value = page
    currentPageIconList.value = iconList.slice((current.value - 1) * pageSize, current.value * pageSize)
}
// 当前页的图标列表
const currentPageIconList = ref(iconList.slice(0, pageSize))
const searchValue = ref('')
// 搜索列表
const searchList = ref<string[]>([])
const search = (icon: string) => {
    searchList.value = iconList.filter(item => item.includes(icon))
    currentPageIconList.value = searchList.value.slice(0, pageSize)
}
const isGridView = ref(false) // 图标列表显示方式
const handleSelectedIcon = async (icon: string) => {
    emit('selected', icon)
    emit('update:modelValue', icon)
    if (props.enableCopy) {
        const { isSupported, copied, copy } = useClipboard()
        if (isSupported) {
            try {
                await copy(`<${icon}>`)
                if (copied) {
                    ElMessage.success('复制成功')
                }
            } catch (e) {
                ElMessage.error('复制失败')
            }

        }
    }
}
const emit = defineEmits<{
    (e: 'selected', icon: string): void,
    (e: 'update:modelValue', icon: string): void,
}>()
</script>

<template>

    <el-popover width="400px">
        <template #reference>
            <el-input placeholder="请选择菜单图标" :model-value="modelValue">
                <template #prefix>

                    <GiSvgIcon :name="modelValue" size="20" v-if="modelValue"></GiSvgIcon>
                    <GiSvgIcon name="search" size="20" v-else></GiSvgIcon>

                </template>
            </el-input>
        </template>
        <template #default>
            <div class="serach">
                <el-input prefix-icon="Search" placeholder="搜索图标名称" v-model="searchValue" @input="search">
                </el-input>
                <el-button @click="isGridView = !isGridView">
                    <el-tooltip content="详细内容" v-if="true">
                        <GiSvgIcon name="apps" size="15"></GiSvgIcon>
                    </el-tooltip>
                    <el-tooltip v-else content="精简内容">
                        <GiSvgIcon name="unordered-list" size="15"></GiSvgIcon>
                    </el-tooltip>
                </el-button>
            </div>
            <div class="content">
                <el-row :gutter="4" class="icon-list">
                    <el-col v-for="(icon, index) in currentPageIconList" :key="index" :span="isGridView ? 4 : 8"
                        style="margin-bottom: 10px" class="icon-item" @click="handleSelectedIcon(icon)">
                        <GiSvgIcon :name="icon" size="20"></GiSvgIcon>
                        <span style="font-size:0.8em" v-if="!isGridView">{{ icon }}</span>
                    </el-col>
                </el-row>
                <el-row>
                    <el-pagination layout="prev, pager, next" :page-size="pageSize" :total="total"
                        @current-change="pageChange"></el-pagination>
                </el-row>
            </div>
        </template>
    </el-popover>
</template>

<style lang="scss" scoped>
.serach {
    display: flex;

    .el-button {
        margin-left: 10px;
    }
}

.content {
    margin-top: 10px;


    .icon-list {
        padding-left: 20px;
        max-height: 300px;
        overflow: auto;
    }

    .icon-item {
        flex-wrap: nowrap;
        white-space: nowrap;
        overflow: hidden;

    }
}
</style>