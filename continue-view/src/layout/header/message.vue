<script lang="ts" setup>

</script>

<template>
    <div class="header">
        <span>通知</span>
    </div>
    <div class="middle">
        <el-empty />
    </div>
    <div class="footer">
        <div class="left">
            <span>查看更多</span>
            <el-icon>
                <ArrowRight />
            </el-icon>
        </div>
        <div class="right">
            <span>全部已读</span>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.middle {
    width: 100%;
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    margin: 20px 0;
    height: 150px;
    display: flex;
    justify-content: center;
    align-items: center;

    .el-empty {
        height: 100%;
        width: 100%;
        scale: 0.7;
    }
}

.footer {
    color: rgba(22, 93, 255);
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>