<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import { type DashboardNoticeResp, listDashboardNotice } from '@/apis'
import { on } from 'events'

const dataList = ref<DashboardNoticeResp[]>([])
const loading = ref(false)
// 查询列表数据
const getDataList = async () => {
    try {
        loading.value = true
        const res = await listDashboardNotice()
        // console.log(res)
        dataList.value = res.data
    } finally {
        loading.value = false
    }
}

const router = useRouter()
// 详情
const onDetail = (id: number) => {
    router.push({ path: '/user/notice', query: { id } })
}
// 打开消息中心
const open = () => {
    router.push({ path: '/user/message', query: { tab: 'notice' } })
}
onMounted(() => {
    getDataList()
})
</script>

<template>
    <el-card>
        <template #header>
            <div class="title">
                <span>公告</span>
                <el-link @click="open">更多</el-link>
            </div>
        </template>
        <template #default>
            <!-- <el-button type="danger" size="small" width>置顶</el-button>
            <hr> -->
            <el-space direction="vertical" style="width:100%;" alignment="start">
                <div class="item" v-for="item in dataList" :key="item.id">
                    <el-link class="item-content" @click="onDetail(item.id)">

                        {{ item.title }}

                    </el-link>
                </div>
            </el-space>
        </template>
    </el-card>
</template>
<style lang="scss" scoped>
.title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: var(--color-text-1);

}

.el-card {
    background: var(--color-bg-1);
    overflow: auto;
    max-height: 100px;
}

:deep(.el-card__body) {
    display: flex;
    flex-direction: column;
}

::v-deep .el-card__header {
    height: 30px;
    padding: 15px;
    border-bottom: 0px;
}

.el-button {
    margin-right: 10px;
    width: 20%;
}
</style>