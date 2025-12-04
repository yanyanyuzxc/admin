<script lang="ts" setup>
import { ref } from 'vue'
import Demo1 from './Demo1.vue'
import Demo2 from './Demo2.vue';
import Pv from './Pv.vue';
import IPAddress from './IPAddress.vue';
</script>

<template>
    <div class="box">
        <p>
            数据总览
        </p>
        <el-row :gutter="10">
            <el-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                <Pv />
            </el-col>
            <el-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                <IPAddress />
            </el-col>
            <el-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                <Demo1 />
            </el-col>
            <el-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
                <Demo2 />
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
p {
    padding: 10px;
    margin-top: 0px;
    padding-top: 0px;
    margin-bottom: 0px;
    color: var(--color-text-1);
    font-size: 1.1em;
}

.el-col {
    margin-bottom: 20px;
}

.box {
    padding: 20px;
    height: 100%;
    background: var(--color-bg-1);
}
</style>