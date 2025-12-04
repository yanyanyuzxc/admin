<script lang="ts" setup>
import Overview from './overview/index.vue'
import Brower from './brower.vue'
import Terminal from './terminal.vue'
import Time from './time.vue'
import Hot from './hot.vue'
import Geo from './geo.vue'
import { useAppStore } from '@/store'
const appStore = useAppStore()
</script>

<template>
    <div class="container" :class="{ 'dark': appStore.theme === 'dark' }">
        <div class="overview">
            <Overview></Overview>
        </div>
        <div class="middle">
            <el-row :gutter="20">
                <el-col class="geo" :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                    <Geo></Geo>
                </el-col>
                <el-col class="right" :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                    <div class="terminal">
                        <Terminal></Terminal>
                    </div>
                    <div class="brower">
                        <Brower></Brower>
                    </div>
                </el-col>

            </el-row>
        </div>
        <el-row class="footer" :gutter="20">
            <el-col class="time" :xs="24" :sm="24" :md="24" :lg="18" :xl="18">
                <Time></Time>
            </el-col>
            <el-col class="hot" :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                <Hot></Hot>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
.container {
    height: fit-content;
    width: 100%;
    background: rgb(245, 247, 253);

    &.dark {
        background: var(--color-bg-1) !important;
    }

    .overview {
        height: 23%;
        width: 100%;

    }


    .middle {
        height: fit-content;

        .geo {
            height: 100%;
        }

        .right {
            display: flex;
            flex-direction: column;
            height: 100%;

            .terminal {
                flex: 1;
            }

            .brower {
                flex: 1;
            }
        }

    }

    .footer {
        height: 50%;
        width: 100%;
        margin-top: 20px;
    }
}
</style>