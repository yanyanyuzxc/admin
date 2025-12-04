<script lang="ts" setup>

import type { ModeItem } from '../type'
import { useUserStore } from '@/store'
import VerifyModel from '../components/VerifyModel.vue'
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const modeList = ref<ModeItem[]>([])
modeList.value = [
    {
        title: '安全手机',
        icon: 'phone-color',
        value: userInfo.value.phone,
        subtitle: `${userInfo.value.phone ? '' : '手机号'}可用于登录、身份验证、密码找回、通知接收`,
        type: 'phone',
        jumpMode: 'modal',
        status: !!userInfo.value.phone,
        statusString: userInfo.value.phone ? '已绑定' : '未绑定',
    },
    {
        title: '安全邮箱',
        icon: 'email-color',
        value: userInfo.value.email,
        subtitle: `${userInfo.value.email ? '' : '邮箱'}可用于登录、身份验证、密码找回、通知接收`,
        type: 'email',
        jumpMode: 'modal',
        status: !!userInfo.value.email,
        statusString: userInfo.value.email ? '已绑定' : '未绑定',
    },
    {
        title: '登录密码',
        icon: 'password-color',
        subtitle: userInfo.value.pwdResetTime ? `为了您的账号安全，建议定期修改密码` : '请设置密码，可通过账号+密码登录',
        type: 'password',
        jumpMode: 'modal',
        status: !!userInfo.value.pwdResetTime,
        statusString: userInfo.value.pwdResetTime ? '已设置' : '未设置',
    },
]
const verifyModelRef = ref<InstanceType<typeof VerifyModel>>()
// 修改
const onUpdate = (type: string) => {
    verifyModelRef.value?.open(type)
}
</script>

<template>
    <el-card>
        <template #header>
            <div class="header">
                <span>安全设置</span>
            </div>
        </template>
        <template #default>
            <div class="center">
                <div v-for="item in modeList" :key="item.title" class="item">

                    <div class="label">
                        <GiSvgIcon :name="item.icon" size="25"></GiSvgIcon>
                    </div>

                    <div class="right">
                        <div class="message">
                            <el-space direction="vertical" alignment="normal">
                                <div class="top">
                                    <span>{{ item.title }}</span>
                                    <GiSvgIcon name="check-circle-fill" size="14" v-if="item.status" />
                                    <GiSvgIcon name="exclamation-circle-fill" v-else size="14" />
                                    <span style="font-size: 12px" :class="item.status ? 'success' : 'warning'">{{
                                        item.statusString }}</span>
                                </div>
                                <div class="info-desc">
                                    <span class="value">{{ item.value }}</span>
                                    {{ item.subtitle }}
                                </div>
                            </el-space>
                        </div>
                        <div class="action">
                            <el-button v-if="item.jumpMode === 'modal'" class="btn"
                                :type="item.status ? 'secondary' : 'primary'" @click="onUpdate(item.type)">
                                {{ ['password'].includes(item.type) || item.status ? '修改' : '绑定' }}
                            </el-button>
                        </div>
                    </div>


                </div>
            </div>

        </template>
    </el-card>
    <VerifyModel ref="verifyModelRef" />
</template>

<style lang="scss" scoped>
.el-card {
    background: linear-gradient(to bottom, #e2eef0, white 20%)
}

.item {
    display: flex;
    margin: 20px;
}



.header {
    font-size: 1.3em;
}

.right {
    display: flex;
    width: 100%;
    justify-content: space-between;
    margin-left: 20px;
    align-items: center;
}

.label {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-color: #eae7e7;
    @include flex-center;
    opacity: 0.9;
}
</style>