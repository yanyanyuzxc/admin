<script lang="ts" setup>
import { ref } from 'vue'
import { defineProps } from 'vue'
import VerifySlide from './verify/VerifySlide.vue'
const props = defineProps({
    captchaType: {
        type: String,
        required: true,
    },
    figure: {
        type: Number,
    },
    arith: {
        type: Number,
    },
    mode: {
        type: String,
        default: 'pop',
    },
    space: {
        type: Number,
    },
    explain: {
        type: String,
    },
    imgSize: {
        type: Object,
        default() {
            return {
                width: '310px',
                height: '155px',
            }
        },
    },
    blockSize: {
        type: Object,
    },
    barSize: {
        type: Object,
    },
})
const { captchaType, mode } = toRefs(props)
const clickShow = ref(false)
const showBox = computed(() => {
    if (mode.value === 'pop') {
        return clickShow.value
    }
    return true
})
const show = () => {
    if (mode.value === 'pop') {
        clickShow.value = true
    }

}
const hide = () => {
    if (mode.value === 'pop') {
        clickShow.value = false
    }
}
const instance = ref({})
/**
  * refresh
  * @description 刷新
  */
const refresh = () => {
    instance.value = {}
}
defineExpose({
    show,
    hide,
    refresh,
})

</script>

<template>
    <div class="verify" v-show="showBox">
        <div class="verifybox" :style="{ 'max-width': `${parseInt(imgSize.width) + 30}px` }">
            <div class="verifybox-top">
                <span>请完成安全验证</span>

                <el-icon @click="$emit('close')">
                    <Close />
                </el-icon>

            </div>
            <div class="bottom" v-if="mode === 'pop'">
                <VerifySlide @success="$emit('success')" />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.verify {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1001;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    /* display: none; */
    transition: all 0.5s;
}

.verifybox-top {
    padding: 0 15px;
    height: 50px;
    line-height: 50px;
    text-align: left;
    font-size: 16px;
    color: #45494c;
    border-bottom: 1px solid #e4e7eb;
    box-sizing: border-box;
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.verifybox {
    position: relative;
    box-sizing: border-box;
    border-radius: 2px;
    border: 1px solid #aeb0b1;
    background-color: #fff;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}

.bottom {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    margin-top: 10px;

    margin: 20px auto;
}
</style>
