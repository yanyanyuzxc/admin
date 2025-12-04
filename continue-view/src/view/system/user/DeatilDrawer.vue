<template>
    <el-drawer v-model="visible" title="用户详情" :width="width >= 500 ? 500 : '100%'" :footer="false">
        <el-descriptions :column="2" size="large" class="general-description">
            <el-descriptions-item label="ID" :span="2">
                {{ dataDetail?.id }} <el-button type="text" icon="CopyDocument" @click="handleCopy(dataDetail?.id)"
                    size="small">

                </el-button>
            </el-descriptions-item>
            <el-descriptions-item label="用户名">{{ dataDetail?.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ dataDetail?.nickname }}</el-descriptions-item>
            <el-descriptions-item label="性别">
                <span v-if="dataDetail?.gender === 1">男</span>
                <span v-else-if="dataDetail?.gender === 2">女</span>
                <span v-else>未知</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
                <a-tag v-if="dataDetail?.status === 1" color="green">启用</a-tag>
                <a-tag v-else color="red">禁用</a-tag>
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{ dataDetail?.phone || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ dataDetail?.email || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="所属部门">{{ dataDetail?.deptName }}</el-descriptions-item>
            <el-descriptions-item label="角色">
                <GiCellTags :data="dataDetail?.roleNames" />
            </el-descriptions-item>
            <el-descriptions-item label="创建人">{{ dataDetail?.createUserString }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ dataDetail?.createTime }}</el-descriptions-item>
            <el-descriptions-item label="修改人">{{ dataDetail?.updateUserString }}</el-descriptions-item>
            <el-descriptions-item label="修改时间">{{ dataDetail?.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ dataDetail?.description }}</el-descriptions-item>
        </el-descriptions>
    </el-drawer>
</template>

<script setup lang="ts">
import { useWindowSize } from '@vueuse/core'
import { CopyDocument } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { type UserDetailResp, getUser as getDetail } from '@/apis/system/user'
import { ref } from 'vue'
const { width } = useWindowSize()


const handleCopy = async (textToCopy?: string) => {
    // 2. 校验空值，提前返回
    if (textToCopy === undefined || textToCopy === '') {
        ElMessage.warning('没有可复制的内容');
        return;
    }

    try {
        await navigator.clipboard.writeText(textToCopy);//写入浏览器的剪贴板
        ElMessage.success('复制成功！');
    } catch (err) {
        console.error('复制失败:', err);
        ElMessage.error('复制失败，请手动复制');
    }
};
const dataId = ref('')
const dataDetail = ref<UserDetailResp>()
const visible = ref(false)

// 查询详情
const getDataDetail = async () => {
    const { data } = await getDetail(dataId.value)
    dataDetail.value = data
}

// 打开
const open = async (id: string) => {

    dataId.value = id as string
    await getDataDetail()
    visible.value = true
}

defineExpose({ open })
</script>

<style scoped lang="scss"></style>