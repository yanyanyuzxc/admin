<script lang="ts" setup>
import { getUserNotice } from '@/apis/system/user-message'
import { useTabStore } from '@/store'
import { useResetReactive } from '@/hooks'
import AiEditor from './components/index.vue'
defineOptions({ name: 'UserNotice' })

const route = useRoute()
const router = useRouter()
const tabsStore = useTabStore()

const { id } = route.query
const containerRef = ref<HTMLElement | null>()
const [form, resetForm] = useResetReactive({
    title: '',
    createUserString: '',
    publishTime: '',
    content: '',
})

// 回退
const onBack = () => {
    tabsStore.closeCurrent(route.path)
    router.push({ path: '/user/message', query: { tab: 'notice' } })
}
// 打开
const onOpen = async (id: number) => {
    resetForm()
    const { data } = await getUserNotice(id)
    Object.assign(form, data)
}

onMounted(() => {
    onOpen(id as unknown as number)
})
</script>

<template>
    <div class="view">
        <el-page-header @back="onBack" class="page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> Title </span>
            </template>
        </el-page-header>


        <el-card>
            <div class="detail_content">
                <h1 class="title">{{ form?.title }}</h1>
                <div class="info">
                    <el-space>
                        <span>
                            <GiSvgIcon name="user"></GiSvgIcon>
                            <span class="label">发布人：</span>
                            <span>{{ form?.createUserString }}</span>
                        </span>
                        <el-divider direction="vertical" />
                        <span>
                            <GiSvgIcon name="history"></GiSvgIcon>
                            <span class="label">发布时间：</span>
                            <span>{{ form?.publishTime }}</span>
                        </span>
                        <el-divider v-if="form?.updateTime" direction="vertical" />
                        <span v-if="form?.updateTime">

                            <GiSvgIcon name="schedule" />
                            <span>更新时间：</span>
                            <span>{{ form?.updateTime }}</span>
                        </span>
                    </el-space>
                </div>
                <div class="footer">
                    <span>{{ form?.content }}</span>
                </div>
            </div>

        </el-card>
    </div>
</template>

<style lang="scss" scoped>
.view {

    display: flex;
    flex-direction: column;
    height: 100%;
}

.page-header {
    height: 50px;
    background: white;
    padding: 10px;
}

.footer {
    flex: 1;
    width: 60%;
    padding: 20px;
    margin-top: 20px;
    border: 1px dashed #ccc;
}

.el-card {
    margin-top: 30px;
}

.detail_content {
    display: flex;
    flex-direction: column;
    align-items: center;


    flex: 1;
    min-height: 500px;
}
</style>