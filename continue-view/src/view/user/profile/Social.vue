<script lang="ts" setup>
import type { ModeItem } from '../type'
import VerifyModel from '../components/VerifyModel.vue'
import { listUserSocial, socialAuth, unbindSocialAccount } from '@/apis'
import { ElMessage } from 'element-plus'
const socialList = ref<any>([])
const modeList = ref<ModeItem[]>([])
// 初始化数据
const initData = async () => {
    await listUserSocial().then((res) => {
        socialList.value = res.data.map((el) => el.source)
        modeList.value = [
            {
                title: '绑定 Gitee',
                icon: 'gitee',
                subtitle: `${socialList.value.includes('GITEE') ? '' : '绑定后，'}可通过 Gitee 进行登录`,
                jumpMode: 'link',
                type: 'gitee',
                status: socialList.value.includes('GITEE'),
            },
            {
                title: '绑定 GitHub',
                icon: 'github',
                subtitle: `${socialList.value.includes('GITHUB') ? '' : '绑定后，'}可通过 GitHub 进行登录`,
                type: 'github',
                jumpMode: 'link',
                status: socialList.value.includes('GITHUB'),
            },
        ]
    })
}
onMounted(() => {
    initData()
})
//添加🔒
const loading = ref(false)
const onUpdate = (type: string, status: boolean) => {

}
const onBinding = (type: string, status: boolean) => {
    try {
        if (loading.value) return
        loading.value = true
        if (!status) {
            socialAuth(type).then((res: any) => {
                window.open(res.data.authorizeUrl, '_self')
            })

        }
        else {
            unbindSocialAccount(type).then(() => {
                ElMessage.success('解绑成功')
                initData()
            }
            )
        }
    }
    catch (e) {
        console.log(e)
    }
}

</script>

<template>
    <el-card>
        <template #header>
            <div class="header">
                <span> 第三账号</span>
            </div>
        </template>
        <template #default>
            <div class="body">
                <el-space direction="vertical" style="width:100%;" fill>
                    <div class="item" v-for="item in modeList" :key="item.type">
                        <div class="left">
                            <el-space>
                                <div class="label">
                                    <GiSvgIcon :name="item.icon" size="30" />
                                </div>
                                <div class="content">
                                    <el-space direction="vertical" alignment="flex-start">
                                        <div class="top">
                                            <span>{{ item.title }}</span>
                                            <GiSvgIcon name="check-circle-fill" v-if="item.status" :size="14" />
                                            <GiSvgIcon name="exclamation-circle-fill" v-else :size="14" />

                                            <span style="font-size: 12px"
                                                :class="item.status ? 'success' : 'warning'">{{
                                                    item.status ? '已绑定' : '未绑定'
                                                }}</span>
                                        </div>
                                        <div class="bottom">
                                            <span>{{ item.subtitle }}</span>
                                        </div>
                                    </el-space>
                                </div>
                            </el-space>
                        </div>
                        <div class="right">
                            <el-button v-if="item.jumpMode === 'modal'" class="btn"
                                :type="item.status ? 'secondary' : 'primary'" @click="onUpdate(item.type, item.status)">
                                {{ item.status ? '修改' : '绑定' }}
                            </el-button>
                            <el-button v-else-if="item.jumpMode === 'link'" class="btn"
                                :type="item.status ? 'secondary' : 'primary'"
                                @click="onBinding(item.type, item.status)">
                                {{ item.status ? '解绑' : '绑定' }}
                            </el-button>
                        </div>
                    </div>
                </el-space>
            </div>
        </template>
    </el-card>
</template>

<style lang="scss" scoped>
.item {
    @include flex-between;
    width: 100%;
    height: 7vh;
}

.left {
    display: flex;


}

.el-card {
    background: linear-gradient(to bottom, #e2eef0, white 20%);
    min-height: 200px;
}
</style>