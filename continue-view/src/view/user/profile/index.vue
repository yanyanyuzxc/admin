<script lang="ts" setup>
import LeftBox from './BasicInfo.vue'
import RightBox from './Social.vue'
import PasswordPolicy from './Security.vue'
</script>

<template>
    <div class="gi_page">
        <el-row wrap :gutter="[16]" align="stretch">
            <el-col :xs="24" :sm="24" :md="10" :lg="8" :xl="7" :xxl="7">
                <LeftBox />
            </el-col>
            <el-col :xs="24" :sm="24" :md="14" :lg="16" :xl="17" :xxl="17">
                <div>
                    <PasswordPolicy />
                </div>
                <div style="margin-top: 16px">
                    <RightBox />
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped></style>