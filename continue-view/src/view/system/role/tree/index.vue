<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { type RoleResp, deleteRole, listRole } from '@/apis/system/role'
import { mapTree } from 'xe-utils'
import RightMenu from './RightMenu.vue'
import AddDrawer from '../AddDrawer.vue'
const searchKey = ref('')
interface TreeItem extends RoleResp {
    popupVisible: boolean
}
const dataList = ref<TreeItem[]>([])
//搜索
const search = (key: string) => {

    const loop = (data: TreeItem[]) => {
        const result: TreeItem[] = []
        data.forEach((item) => {
            if (item.name.toLowerCase().includes(key) || item.code.toLowerCase().includes(key)) {
                result.push(item)
            }
        })
        return result
    }
    return loop(dataList.value)
}
const treeData = computed(() => {
    if (!searchKey.value) return dataList.value
    return search(searchKey.value.toLowerCase())
})
const defaultProps = {
    label: 'name',
    children: 'children',

}
//获取查询树的结点
const getTreeData = async () => {
    try {
        const { data } = await listRole({ sort: ['sort,asc'] })
        dataList.value = mapTree(data, (item) => ({
            ...item,
            popupVisible: false,
            icon: () => {
                return null
            },
        }))

    } catch (e) {
        console.log(e)
        ElMessage.error('获取树结点失败')
    }
}
const onAdd = () => {
    AddDrawerRef.value?.onAdd()
}
const visible = ref(false)
const AddDrawerRef = ref()
const onMenuItemClick = (mode: string, node: RoleResp) => {
    if (mode === 'update') {
        AddDrawerRef.value?.onUpdate(node.id)
    } else if (mode === 'delete') {
        ElMessageBox.confirm(
            `是否确定删除角色「${node.name}」？`,
            '提示',
            {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                type: 'warning',
            }
        )
            .then(async () => {
                try {
                    const res = await deleteRole(node.id)
                    if (res.success) {
                        ElMessage.success('删除成功')
                        await getTreeData()
                    }
                    return res.success
                } catch (error) {
                    return false
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: 'Delete canceled',
                })
            })
    }
}
const treeRef = ref<any>(null)
const selectedNode = computed(() => {
    return treeRef.value.getCurrentKey()
})
// const handleOpen = (node: any) => {
//     rightMenu.value.open()
// }
// 选中节点

const select = (keys: Array<any>) => {

    // if (selectedKeys.value && selectedKeys.value[0] === keys[0]) {
    //     // console.log(selectedKeys.value)
    //     return
    // }
    // selectedKeys.value = keys.id

    emit('node-click', keys)
}
onMounted(() => {
    getTreeData()
})
const emit = defineEmits<{
    (e: 'node-click', keys: Array<any>): void
}>()
</script>

<template>
    <div class="container">
        <div class="search">
            <el-input v-model="searchKey" placeholder="搜索名称/编码">
                <template #prefix>
                    <GiSvgIcon name="search"></GiSvgIcon>
                </template>

            </el-input>
            <el-button v-permission="['system:role:create']" type="primary" @click="onAdd">
                <GiSvgIcon name="plus" size="15"></GiSvgIcon>
            </el-button>

        </div>
        <div class="tree">
            <el-tree :data="treeData" @node-click="select" node-key="id" :current-node-key="1" :props="defaultProps"
                highlight-current ref="treeRef">
                <template #default="{ data }">
                    <div class="tree-node-content">
                        {{ data.name }} ({{ data.code }})
                        <el-popover placement="right-start">
                            <template #reference>
                                <GiSvgIcon name="more" size="15" class="more-icon"
                                    v-show="data.id === selectedNode || visible">
                                </GiSvgIcon>
                            </template>
                            <template #default>

                                <RightMenu ref="rightMenu" :data="data" @on-menu-item-click="onMenuItemClick" />
                            </template>
                        </el-popover>
                    </div>
                </template>
            </el-tree>
        </div>
        <AddDrawer ref="AddDrawerRef" @save-success="getTreeData"></AddDrawer>
    </div>
</template>

<style lang="scss" scoped>
::v-deep .el-tree-node__content>.el-tree-node__expand-icon {
    padding: 2px;
}

.tree-node-content:hover .more-icon {
    display: inline-block !important; // !important 确保覆盖v-show的display:none
}

::v-deep .el-tree {
    --el-tree-node-content-height: 35px;
}

.tree-node-content {
    display: flex;
    align-items: center;
    justify-content: space-between; // 让图标靠右对齐
    width: 100%;
    padding: 2px 4px; // 可选：扩大hover范围，提升体验
}



.container {
    padding: 15px;


    .tree {
        margin-top: 20px;


    }

    .search {
        display: flex;
        flex-wrap: nowrap;
        overflow: hidden;

    }

    .el-button {
        width: 10%;
    }
}

::v-deep .el-input {
    margin-right: 10px;
    width: 80%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>