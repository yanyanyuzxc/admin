<script lang="ts" setup>
import { Search } from "@element-plus/icons-vue"
import { ref, onMounted, nextTick, computed } from "vue"
import type { TreeInstance, TreeNodeData } from '@arco-design/web-vue'
import { useDept } from '@/hooks/app'
import { mapTree } from 'xe-utils'
//获取数据
// 查询树列表

const flag = ref(false)
const treeRef = ref()
const { deptList, getDeptList } = useDept({
    onSuccess: () => {
        // nextTick(() => {
        //     deptList.value = mapTree(deptList.value, (item: any) => {
        //         item.label = item.title
        //         return item
        //     });
        // })
    },
})

onMounted(() => {
    getDeptList()
})
const emit = defineEmits<{
    (e: 'node-click', keys: Array<any>): void
}>()

// 选中节点
const selectedKeys = ref()
const select = (keys: Array<any>) => {


    emit('node-click', keys)

}
// 过滤树
const searchKey = ref('')
const search = (keyword: string) => {
    const loop = (data: TreeNodeData[]) => {
        const result = [] as TreeNodeData[]
        data.forEach((item: TreeNodeData) => {
            if (item.title?.toLowerCase().includes(keyword)) {
                result.push({ ...item })
            } else if (item.children) {
                const filterData = loop(item.children)
                if (filterData.length) {
                    result.push({
                        ...item,
                        children: filterData,
                    })
                }
            }
        })
        return result
    }
    return loop(deptList.value)
}

const treeData = computed(() => {
    if (!searchKey.value) return deptList.value
    return search(searchKey.value.toLowerCase())
})
</script>

<template>
    <div class="left">
        <div class="input">
            <el-input placeholder="请输入部门名称" v-model="searchKey">
                <template #prefix>
                    <Search style="width:1em;height:1em;margin-right:10px;color:black" />
                </template>
            </el-input>
        </div>
        <div class="tree">
            <el-tree style="max-width: 600px" :data="treeData" highlight-current ref="treeRef" default-expand-all
                :current-node-key="selectedKeys" @node-click="select" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.left {
    padding: 20px;
}

.tree {
    margin-top: 20px;
}

::v-deep .el-input {
    --el-input-focus-border-color: rgb(var(--primary-6));
}
</style>