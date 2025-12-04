<script lang="ts" setup>
import { ref } from 'vue'
import '@/styles/icon/iconfont.js'
import Background from "./components/background/index.vue"
import Account from './components/account/index.vue'
import Phone from './components/phone/index.vue'
import Email from './components/email/index.vue'
import { useDevice } from '@/hooks/index.ts'
import { useRouter } from 'vue-router'
import { socialAuth } from '@/apis'
const { isDesktop } = useDevice()
let router = useRouter()
let value = ref<any>('')
let activeName = ref('Account')
let loginType = ref<number>(1)
const changeLoginType = () => {
    loginType.value = 2
}
const onOauth = async (source: string) => {
    let res = await socialAuth(source)
    window.location.href = res.data.authorizeUrl

}
</script>

<template>
    <Background style="z-index:-1" />
    <div style="height:10%; width:100%;">
        <div class="header">
            <el-row>
                <el-col :span="23">
                    <div class="img">
                        <img src="@/assets/images/logo.png" alt="">
                    </div>
                    <p>哆啦A梦--智慧工程</p>
                </el-col>
                <el-col :span="1">

                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-yueliang"></use>
                    </svg>
                </el-col>
            </el-row>
        </div>
    </div>
    <el-container v-if="isDesktop" class="login__desktop">



        <div class="login" :class="[activeName === 'Phone' ? 'fold' : '', loginType === 2 ? 'fly' : '']">
            <div class="left">
                <img src="@/assets/images/banner.png" alt="" mode="aspectFit">
            </div>
            <div class="right">
                <el-tabs v-model="activeName" class="demo-tabs" v-if="loginType === 1">
                    <el-tab-pane name="Account">
                        <template #label>

                            <p class="title">账号登录</p>
                        </template>
                        <Account />
                    </el-tab-pane>
                    <el-tab-pane name="Phone">
                        <template #label>
                            <p>手机登录</p>
                        </template>
                        <Phone />
                    </el-tab-pane>
                </el-tabs>

                <component :is="Email" v-if="loginType === 2" />



                <el-divider>
                    <span style="font-weight:300;font-size:13px">其他登录方式</span>
                </el-divider>


                <div class="footer">
                    <button @click="loginType = 2" v-if="loginType === 1">
                        <el-icon>
                            <Message />
                        </el-icon>
                        <span> 邮箱登录</span>

                    </button>
                    <button @click="loginType = 1" v-if="loginType === 2" style="width:130px">
                        <el-icon>
                            <Avatar />
                        </el-icon>
                        <span> 账号/手机登录</span>

                    </button>
                    <div @click="onOauth('gitee')">
                        <GiSvgIcon name="gitee" :size="24"></GiSvgIcon>
                    </div>
                    <div @click="onOauth('github')">
                        <GiSvgIcon name="github" :size="24"></GiSvgIcon>
                    </div>
                </div>
            </div>
        </div>


    </el-container>
    <div v-else class="login__mobile">

        <div class="header">
            <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-drxx92"></use>
            </svg>
            <p>哆啦A梦--智慧工程</p>
        </div>
        <div class="right">
            <el-tabs v-model="activeName" class="demo-tabs" v-if="loginType === 1">
                <el-tab-pane name="Account">
                    <template #label>

                        <p class="title">账号登录</p>
                    </template>
                    <component :is="Account" />
                </el-tab-pane>
                <el-tab-pane name="Phone">
                    <template #label>
                        <p>手机登录</p>
                    </template>
                    <component :is="Phone" />
                </el-tab-pane>
            </el-tabs>

            <component :is="Email" v-if="loginType === 2" />

            <div class="remember">
                <el-checkbox label="记住我" value="Value 1" size="large" />
                <p>忘记密码</p>
            </div>
            <div class="btn" @click="router.push('/')">
                <button>登录</button>
            </div>
            <el-divider style="margin-top:80px">
                <span style="font-weight:300;font-size:13px">其他登录方式</span>
            </el-divider>

            <div class="footer">
                <button @click="loginType = 2" v-if="loginType === 1">
                    <el-icon>
                        <Message />
                    </el-icon>
                    邮箱登录
                </button>
                <button @click="loginType = 1" v-if="loginType === 2" style="width:130px">
                    <el-icon>
                        <Avatar />
                    </el-icon>
                    账号/手机登录
                </button>

                <GiSvgIcon name="gitee" :size="24"></GiSvgIcon>
                <GiSvgIcon name="github" :size="24" @click="onOauth('gitee')"></GiSvgIcon>

            </div>
        </div>

    </div>


</template>

<style lang="scss" scoped>
.header {
    height: 100%;
    width: 100%;
    position: absolute;
    top: 3%;

    .img {


        img {
            width: 80%;
            height: 80%;
            object-fit: contain;
        }
    }

    .el-row {
        height: 100%;
    }

    .el-col {
        display: flex;
        height: 100%;
        align-items: flex-start;
    }

    p {
        // line-height: 30px;
        font-size: 25px;
        display: flex;
        margin: 7px;
        // margin-left: 10px;
        font-weight: 500;
    }

    .icon {
        rotate: -45deg;
    }
}

.el-container {
    padding: 20px;
    width: 100vw;
    height: 90vh;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;

    .login {
        width: 50%;
        height: 77%;
        // position: absolute;
        // top: 50%;
        // left: 50%;
        // transform: translate(-50%, -50%);
        border: 1px solid #ccc;
        display: flex;
        transition: all 0.5s ease;

        &.fly {
            height: 63%;
        }

        &.fold {
            height: 60%;
        }



        .left {
            flex: 1.4;
            background: linear-gradient(to right, rgb(var(--primary-6)), rgb(var(--primary-1)));

            img {
                width: 110%;
                height: auto;

            }
        }


    }

    .right {
        flex: 1;
        padding: 30px 10px;



        .demo-tabs {
            position: relative;

            p {
                font-size: 16px;
                font-weight: 300;
                margin-bottom: 10px;
                margin-left: 40px;
                width: 100px;
            }

            .el-tabs__nav-wrap:after {
                height: 0;
            }
        }


        .footer {
            display: flex;

            height: 40px;
            margin-left: 70px;

            div {
                margin-left: 15px;
                margin-top: 5px;
            }

            button {
                width: 100px;
                height: 32px;
                border: none;
                background: white;
                border-radius: 20px;
                border: 1px solid #ccc;
                display: flex;
                align-items: center;
                justify-content: center;

                span {
                    font-size: 1em;
                    font-weight: 300;
                    display: block;
                    margin-left: 5px;
                }

            }
        }



        .title {
            display: flex;
            align-items: center;
            justify-content: center;

            p {
                font-size: 16px;
                font-weight: 300;
                margin-right: 20px;
            }
        }

        .form {

            width: 80%;
            margin: 10px 30px auto;

            .el-form-item {
                margin-bottom: 15px;
                width: 100%;
                height: 40px;

                background-color: white;


                .el-input__inner {
                    background-color: white;

                }
            }
        }
    }



}

.login__mobile {
    .header {
        margin-left: 30px;
        margin-top: 30px;
        display: flex;
        font-size: 14px;
        margin-bottom: 40px;

        p {
            margin-left: 10px;
            font-size: 20px;
        }
    }

    .el-tabs__nav-wrap:after {
        height: 0;
    }

    .right {
        flex: 1;
        padding: 30px 10px;

        .remember {
            display: flex;
            justify-content: space-between;
            margin: 10px 40px;


            p {
                color: #1409ec;
                margin-top: 10px;
            }
        }


        .demo-tabs {
            position: relative;

            p {
                font-size: 16px;
                font-weight: 300;
                margin-bottom: 10px;
                // margin-left: 40px;
                width: 70px;
            }

            &.after {
                height: 0;
            }
        }


        .footer {
            display: flex;

            height: 40px;
            margin-left: 70px;
            margin-top: 30px;

            div {
                margin-left: 15px;
            }

            button {
                width: 100px;
                height: 32px;
                border: none;
                background: white;
                border-radius: 20px;
                border: 1px solid #ccc;
                font-size: 14px;
                font-weight: 300;
            }
        }

        .btn {
            width: 90%;

            margin: 20px auto;

            button {
                background: rgb(38, 119, 233);
                width: 100%;
                height: 40px;
                color: white;
                border: none;

            }
        }

        .title {
            display: flex;
            align-items: center;
            justify-content: center;

            p {
                font-size: 16px;
                font-weight: 300;
                margin-right: 20px;
            }
        }

        .form {

            width: 80%;
            margin: 20px 30px auto;

            .el-form-item {
                margin-bottom: 15px;
                width: 100%;
                height: 40px;

                background-color: white;


                .el-input__inner {
                    background-color: white;

                }
            }
        }
    }
}

.icon {
    width: 2em;
    height: 2em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
}
</style>