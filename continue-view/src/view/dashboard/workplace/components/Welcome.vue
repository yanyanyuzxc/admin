<script lang="ts" setup>
import { useUserStore } from '@/store';
import { goodTimeText } from '@/utils/index.ts';
let userStore = useUserStore();

</script>

<template>
    <div class="card">
        <div style="color:rgb(var(--primary-6))">

            <Avatar :name="userStore.nickname" :size="77" />
        </div>
        <div class="welcome">
            <p class="hello">{{ goodTimeText() }}!{{ userStore.nickname }}</p>

            <p>北海虽赊，扶摇可接；东隅已逝，桑榆非晚。</p>
        </div>
    </div>
</template>
<style lang="scss" scoped>
.card {
    display: flex;
    align-items: center;
    width: 97%;
    height: 100%;
    padding-left: 3%;
}

:deep(.el-avatar) {
    --el-avatar-text-color: $color-theme;
}

.welcome {
    width: 80%;
    margin-left: 10px;
    color: $color-text-3;

    .hello {
        font-size: 1.25rem;
        color: $color-text-1;
        margin-bottom: 10px;
        justify-content: flex-start;
    }

}
</style>
