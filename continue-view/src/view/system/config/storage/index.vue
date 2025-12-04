<script lang="ts" setup>
import { groupBy } from 'xe-utils'
import StorageLocal from './StorageLocal.vue'
import StorageOss from './StorageOss.vue'
import { type StorageQuery, type StorageResp, listStorage } from '@/apis'
const queryForm = reactive<StorageQuery>({
    sort: ['createTime,desc'],
})
const loading = ref(false)
// 查询列表
const getDataList = async () => {
    try {
        loading.value = true
        const { data } = await listStorage(queryForm)
        console.log(data)
        dataMap.value = groupBy(data, 'type')
    } finally {
        loading.value = false
    }
}
const change = (key: string) => {
    activeKey.value = key as string
    queryForm.type = key === 'all' ? undefined : key
    getDataList()
}
const dataMap = ref<Record<string, StorageResp[]>>({})
const activeKey = ref('all')
onMounted(() => {
    getDataList()
})
</script>

<template>
    <GiPageLayout :margin="false" :body-style="{ padding: 0 }" class="gi_page_layout">
        <div class="gi_page">
            <el-tabs v-model="activeKey" @tab-change="change">
                <el-tab-pane name="all">
                    <template #label>全部</template>

                    <el-card title="本地存储" :bordered="false" class="gi_card_title">
                        <StorageLocal :data="dataMap['1']" :loading="loading" @save-success="getDataList" />
                    </el-card>
                    <el-card title="对象存储" :bordered="false" class="gi_card_title">
                        <StorageOss :data="dataMap['2']" :loading="loading" @save-success="getDataList" />
                    </el-card>

                </el-tab-pane>
                <el-tab-pane name="1">
                    <template #label>本地存储</template>
                    <StorageLocal :data="dataMap['1']" :loading="loading" @save-success="getDataList" />
                </el-tab-pane>
                <el-tab-pane name="2">
                    <template #label>对象存储</template>
                    <StorageOss :data="dataMap['2']" :loading="loading" @save-success="getDataList" />
                </el-tab-pane>

            </el-tabs>
            <el-input v-model="queryForm.description" placeholder="搜索名称/编码" style="width: 240px;"
                @search="getDataList" />
        </div>
    </GiPageLayout>
</template>

<style lang="scss" scoped>
.gi_page_layout {
    height: 100%;
    width: 100%;
    padding: 10px;
    position: relative
}

.gi_card_title {
    margin-top: 20px;
}


:deep(.el-tabs__nav-wrap:after) {
    display: none;
}

:deep(.el-tabs__item.is-active) {

    background: rgb(238, 238, 240);

}

:deep(.el-tabs--top .el-tabs__item.is-top,
    .custom-tabs.el-tabs--right .el-tabs__item.is-top) {
    justify-content: center;

}

:deep(.el-tabs__item) {
    width: 100px;

    padding: 0px;
    color: $color-theme;

    border-radius: 5px;
}

.el-input {
    position: absolute;
    top: 10px;
    right: 10px;
}
</style>