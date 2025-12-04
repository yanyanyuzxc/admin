<template>
    <div class="workplace">
        <div class="left">
            <div class="welcome">
                <Welcome />
            </div>
            <div class="project">
                <Project />
            </div>
            <div class="latest">
                <Latest />
            </div>
        </div>
        <div class="right">
            <div class="operation">
                <Operation />
            </div>
            <div class="swiper">
                <Swiper />
            </div>
            <div class="notice">
                <Notice />
            </div>
            <div class="doucument">
                <Doucument />
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import Doucument from './components/Document.vue'
import Operation from './components/QuickOperation.vue'
import Swiper from './components/Swiper.vue'
import Notice from './components/Notice.vue'
import Welcome from './components/Welcome.vue'
import Project from './components/Project.vue'
import Latest from './components/Latest.vue'
</script>

<style scoped lang="scss">
// @import '@/styles/reset.scss';

.workplace {
    display: flex;
    height: 100%;
    width: 100%;
    background: var(--color-bg-3);
}

.left * {
    background-color: var(--color-bg-1);


}

.left {
    flex: 1;

    .welcome {
        height: 18%;

        width: 100%;
        margin-bottom: 10px;
    }

    .project {
        height: 60%;
        margin-bottom: 10px;
    }

    .latest {
        flex: 1;
    }
}



.right {
    width: 280px;
    margin-left: 14px;

    .operation {
        height: 30%;
        margin-bottom: 20px;
        background-color: var(--color-bg-1);
    }

    .swiper {
        height: 23%;
        margin-bottom: 20px;
    }

    .notice {
        height: 13%;

    }

    .doucument {
        height: 25%;
        margin-top: 20px;
    }
}
</style>