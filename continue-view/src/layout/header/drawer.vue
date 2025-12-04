<script lang="ts" setup>
import { ref } from 'vue'
import LayoutItem from './components/layoutItem.vue'
import { useAppStore } from '@/store'
let appStore = useAppStore()
const drawer = ref(false)
const open = () => {
    drawer.value = true
}
// 改变主题色
const changeColor = (colorObj: any) => {
    console.log(colorObj)
    appStore.setThemeColor(colorObj)
}
let color1 = ref('#13c2c2')
const predefineColors = [
    '#13c2c2',
    '#2f4554',
    '#61a0a8',
    '#d48265',
    '#f56c6c',
    '#909399',
    '#da6a41',
    '#5cb87a',
    '#5ad8a6',
    '#f69899',
    '#e7e6e6',]
defineExpose({
    open
})
const options = [
    { value: 'card', label: '卡片' },
    { value: 'border-card', label: '边框卡片' }
]
const animation = ref(false)
</script>

<template>
    <el-drawer v-model="drawer" resizable class="el-drawer-wrapper" size="21%" :lock-scroll="false">
        <template #header>
            <div class="header-class">
                <span>项目配置</span>
            </div>
        </template>
        <template #default>
            <div class="content">
                <div class="pink">
                    <span>「复制配置」按钮，并将配置粘贴到 src/config/settings.ts 文件中。</span>
                </div>
                <div class="top">
                    <el-divider>
                        <span>系统布局</span>
                    </el-divider>
                    <div class="layout">
                        <el-badge class="item" :hidden="appStore.layout === 'mix'" color="transparent" :offset="[-5, 4]"
                            badge-style="border:none">
                            <template #content>
                                <GiSvgIcon name="check-circle" size="16" color="rgb(var(--success-6))">
                                </GiSvgIcon>
                            </template>

                            <LayoutItem mode="left" @click="appStore.layout = 'left'"></LayoutItem>
                            <p class="layout-text">默认布局</p>
                        </el-badge>



                        <el-badge class="item" :hidden="appStore.layout === 'left'" color="transparent"
                            :offset="[-5, 4]" badge-style="border:none">
                            <template #content>
                                <GiSvgIcon name="check-circle" size="16" color="rgb(var(--success-6))"></GiSvgIcon>
                            </template>

                            <LayoutItem mode="left" @click="appStore.layout = 'mix'"></LayoutItem>
                            <p class="layout-text">混合布局</p>
                        </el-badge>
                    </div>
                </div>
                <div class="bottom">
                    <el-divider>
                        <span>系统主题</span>
                    </el-divider>
                    <div class="color">
                        <el-color-picker v-model="appStore.themeColor" show-alpha :predefine="predefineColors"
                            @active-change="changeColor" />
                    </div>
                </div>


                <div class="footer">
                    <el-divider>
                        <span>界面显示</span>
                    </el-divider>
                    <el-descriptions title="User Info" :column="1" class="descriptions">
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>页签显示</p>
                                    <el-switch v-model="appStore.tab" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>页签风格</p>
                                    <el-select v-model="appStore.tabMode" placeholder="Select" style="width: 140px">
                                        <el-option v-for="item in options" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>动画显示</p>
                                    <el-switch v-model="animation" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>动画显示</p>
                                    <el-select v-model="animation" placeholder="Select" style="width: 140px">
                                        <el-option v-for="item in options" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>深色菜单</p>
                                    <el-switch v-model="appStore.menuDark" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>手风琴效果</p>
                                    <el-switch v-model="value1" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>版权显示</p>
                                    <el-switch v-model="value1" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>水印</p>
                                    <el-switch v-model="value1" />
                                </div>
                            </template>
                        </el-descriptions-item>
                        <el-descriptions-item>
                            <template #default>
                                <div class="subtitle">
                                    <p>水印信息</p>
                                    <el-input placeholder="留空则显示用户名" style="width: 200px"></el-input>
                                </div>
                            </template>
                        </el-descriptions-item>
                    </el-descriptions>
                </div>



            </div>
        </template>
        <template #footer>

            <el-divider>
                <span>其他</span>
            </el-divider>
            <div class="other">


                <div class="subtitle">
                    <p>手风琴效果</p>
                    <el-switch v-model="appStore.enableColorWeaknessMode" />
                </div>
                <div class="subtitle">
                    <p>手风琴效果</p>
                    <el-switch v-model="appStore.enableMourningMode" />
                </div>
            </div>
        </template>
    </el-drawer>
</template>

<style lang="scss" scoped>
.el-drawer {
    overflow-y: hidden;
}

.el-drawer-wrapper {
    .other {
        .subtitle {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;

            p {
                font-size: 1.1em;

            }

            height: 30px;
        }
    }

    .footer {
        width: 100%;

        .descriptions {
            width: 100%;

            .subtitle {
                width: 100%;
                display: flex;
                justify-content: space-between;
                align-items: center;

                p {
                    font-size: 1.1em;

                }

                height: 30px;
            }
        }
    }



    .bottom {
        .color {
            width: 300px;
        }
    }

    .layout {
        display: flex;
        justify-content: center;
        align-items: center;

        .item {
            margin-right: 20px;
        }
    }

    // width: 100px !important;
    .header-class {
        padding-bottom: 5px;
        border-bottom: 1px solid #e6e6e6;
        // padding-bottom: 10px;
    }

    .title {
        font-size: 1.2em;
    }

    span {
        font-size: 1.1em;
        font-weight: 400;
    }

    .content {
        margin-top: -20px;

        .pink {
            height: 50%;
            padding: 10px;
            background-color: rgba(239, 232, 255);
        }
    }


}
</style>