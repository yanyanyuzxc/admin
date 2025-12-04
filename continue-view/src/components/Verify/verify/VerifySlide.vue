<script lang="ts" setup>
import { onMounted, ref, reactive, nextTick, getCurrentInstance, computed } from 'vue'
import { getBehaviorCaptcha, checkBehaviorCaptcha } from '@/apis'
import { defineProps, withDefaults } from 'vue'
import type { BehaviorCaptchaResp, CheckBehaviorCaptchaResp } from '@/apis'
import { ArrowRight, Refresh } from '@element-plus/icons-vue'
import { resetSize } from '@/utils/verify.ts'
import { encryptByAes } from '@/utils/encrypt'
import { ElMessage } from 'element-plus'
let value1 = ref(0)
const formatTooltip = (val: number) => {
    return val / 100
}
const props = defineProps({
    captchaType: {
        type: String,
        default: ''
    },
    type: {
        type: String,
        default: '1'
    },
    mode: {
        type: String,
        default: 'fixed'
    },
    vSpace: {
        type: Number,
        default: 11,
    },
    explain: {
        type: String,
        default: '向右滑动完成验证',
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
        default() {
            return {
                width: '50px',
                height: '50px',
            }
        },
    },
    barSize: {
        type: Object,
        default() {
            return {
                width: '310px',
                height: '40px',
            }
        },
    },
})
const { mode, captchaType, type, blockSize, explain, barSize, vSpace } = props
const instance = getCurrentInstance();
const proxy = instance?.proxy!;
const backToken = ref('') // 后台返回的token
const blockBackImgBase = ref('') // 背景图片
const secretKey = ref('') // 后台返回的secretKey
const backImgBase = ref() // 验证码背景图片
const startMoveTime = ref() // 移动开始的时间
const endMovetime = ref() // 移动结束的时间
const tipsBackColor = ref() // 提示词的背景颜色
const tipWords = ref()
const text = ref()
const finishText = ref()
const setSize = reactive({
    imgHeight: '0',
    imgWidth: '0',
    barHeight: '0',
    barWidth: '0',
})
const top = ref(0)
const left = ref(0)
const moveBlockLeft = ref()
const leftBarWidth = ref()
// 移动中样式
const moveBlockBackgroundColor = ref()
const leftBarBorderColor = ref('#ddd')
const iconColor = ref()
const iconClass = ref('icon-right')
const status = ref(false) // 鼠标状态
const isEnd = ref(false) // 是够验证完成
const showRefresh = ref(true)
const transitionLeft = ref('')
const transitionWidth = ref('')
const startLeft = ref(0)
const rightBlock = ref()
//获取验证码
const getPicture = async () => {
    try {
        const data = { captchaType: captchaType }
        const res = await getBehaviorCaptcha(data)
        // console.log(res)
        if (res.code === '0') {
            backImgBase.value = res.data.originalImageBase64
            blockBackImgBase.value = res.data.jigsawImageBase64
            backToken.value = res.data.token
            secretKey.value = res.data.secretKey
        }
    } catch (error) {
        console.log(error)
    }

}
const move = (e: any) => {
    e = e || window.event
    if (status.value && isEnd.value === false) {
        let x: number
        if (!e.touches) {
            // 兼容PC端
            x = e.clientX
        } else {
            // 兼容移动端
            x = e.touches[0].pageX
        }
        const bar_area_left = barArea.value.getBoundingClientRect().left
        let move_block_left = x - bar_area_left // 小方块相对于父元素的left值
        //右侧块的宽度
        if (move_block_left >= barArea.value.offsetWidth - rightBlock.value - 2) {
            move_block_left = barArea.value.offsetWidth - rightBlock.value - 2
        }
        //左侧块的宽度
        if (move_block_left <= startLeft.value) {
            move_block_left = startLeft.value
        }
        // 拖动后小方块的left值
        moveBlockLeft.value = `${move_block_left - startLeft.value}px`
        leftBarWidth.value = `${move_block_left - startLeft.value}px`
    }
}
const barArea = computed(() => {
    return proxy.$el.querySelector('.bottom')
})
let passFlag = ref(true)
const end = async () => {
    //记录结束时间
    let endMovetime = Date.now()
    if (status.value && isEnd.value === false) {
        isEnd.value = true
        let moveLeftDistance = Number.parseInt(moveBlockLeft.value.replace('px', ''), 10)
        moveLeftDistance = (moveLeftDistance * 310) / Number.parseInt(`${setSize.imgWidth}`, 10)
        const data = {
            captchaType: captchaType,
            token: backToken.value,
            pointJson: secretKey.value
                ? encryptByAes(
                    JSON.stringify({ x: moveLeftDistance, y: 5.0 }),
                    secretKey.value,
                )
                : JSON.stringify({ x: moveLeftDistance, y: 5.0 }),
        }
        try {
            let res = await checkBehaviorCaptcha(data)
            if (res.success && res.data.repCode === '0000') {
                ElMessage.success('验证成功')
                iconClass.value = 'check'
                tipWords.value = `${(
                    (endMovetime - startMoveTime.value)
                    / 1000
                ).toFixed(2)}s验证成功`
                isEnd.value = true
                passFlag.value = true
                const captchaVerification = secretKey.value
                    ? encryptByAes(
                        `${backToken.value}---${JSON.stringify({
                            x: moveLeftDistance,
                            y: 5.0,
                        })}`,
                        secretKey.value,
                    )
                    : `${backToken.value}---${JSON.stringify({
                        x: moveLeftDistance,
                        y: 5.0,
                    })}`
                // console.log(captchaVerification)
                setTimeout(() => {
                    refresh()
                    proxy.$parent?.$emit('success', { captchaVerification })

                }, 2000)

            }
            else {
                passFlag.value = false
                tipWords.value = res.data.repMsg
                iconClass.value = ''
                ElMessage.error('验证失败，请重试')
                setTimeout(() => {
                    refresh()
                }, 2000)

            }


            // finishText.value = '验证成功'

            // let time = (endMovetime - startMoveTime.value) / 1000
        }
        catch (error) {
            console.log(error)
            passFlag.value = false
        }

    }

}
const start = (e: any) => {
    e = e || window.event
    let x: number
    if (!e.touches) {
        // 兼容PC端
        x = e.clientX
    } else {
        // 兼容移动端
        x = e.touches[0].pageX
    }
    startLeft.value = Math.floor(
        x - barArea.value.getBoundingClientRect().left,
    )
    // console.log(startLeft.value)
    startMoveTime.value = Date.now() // 移动开始时间
    rightBlock.value = parseInt(barSize.height) - startLeft.value // 右边的块

    console.log(rightBlock.value)
    if (isEnd.value === false) {
        text.value = ''//去除滑块右边的文字
        moveBlockBackgroundColor.value = '#337ab7' // 移动中样式
        leftBarBorderColor.value = '#337AB7'
        iconColor.value = '#fff'
        e.stopPropagation()//阻止事件冒泡
        status.value = true
    }
}
const refresh = () => {
    showRefresh.value = true
    finishText.value = ''
    passFlag.value = true
    tipWords.value = ''
    transitionLeft.value = 'left .3s'
    moveBlockLeft.value = 0
    status.value = false

    leftBarWidth.value = undefined
    transitionWidth.value = 'width .3s'

    leftBarBorderColor.value = '#ddd'
    moveBlockBackgroundColor.value = '#fff'
    iconColor.value = '#000'
    iconClass.value = 'icon-right'
    isEnd.value = false

    getPicture()
    setTimeout(() => {
        transitionWidth.value = ''
        transitionLeft.value = ''
        text.value = explain
    }, 300)
}
const init = () => {
    text.value = explain
    // getPicture()
    nextTick(() => {
        const { imgHeight, imgWidth, barHeight, barWidth } = resetSize(proxy)
        setSize.imgHeight = imgHeight
        setSize.imgWidth = imgWidth
        setSize.barHeight = barHeight
        setSize.barWidth = barWidth
        // proxy.$parent.$emit('ready', proxy)
    })
    window.removeEventListener('touchmove', (e) => {
        move(e)
    })
    window.removeEventListener('mousemove', (e) => {
        move(e)
    })

    // 鼠标松开
    window.removeEventListener('touchend', () => {
        end()
    })
    window.removeEventListener('mouseup', () => {
        end()
    })

    window.addEventListener('touchmove', (e) => {
        move(e)
    })
    window.addEventListener('mousemove', (e) => {
        move(e)
    })

    // 鼠标松开
    window.addEventListener('touchend', () => {
        end()
    })
    window.addEventListener('mouseup', () => {
        end()
    })
}
onMounted(() => {
    getPicture()
    init()
    proxy.$el.onselectstart = function () { return false }
})
</script>

<template>
    <div class="card" style="position:relative">
        <div class="middle" :style="{ width: setSize.imgWidth, height: setSize.imgHeight }">
            <img :src="`data:image/png;base64,${backImgBase}`" alt=""
                style="width: 100%; height: 100%; display: block" />
            <el-icon class="refresh-icon" @click="refresh">
                <Refresh />
            </el-icon>
            <transition name="tips">
                <span v-if="tipWords" class="verify-tips" :class="passFlag ? 'suc-bg' : 'err-bg'">{{ tipWords }}</span>
            </transition>
        </div>
        <div class="bottom" @click="formatTooltip(value1)" :style="{
            'width': setSize.imgWidth,
            'height': barSize.height,
            'line-height': barSize.height,
            'border': `1px solid ${leftBarBorderColor}`,
            'display': 'flex',
            'margin-top': '10px',
            'position': 'relative'
        }">
            <div class="left-bar" :style="{
                'width': leftBarWidth !== undefined ? leftBarWidth : barSize.height,
                'height': barSize.height,
                'transition': transitionWidth,

            }">

                <div class="verify-move-block" :style="{
                    'width': barSize.height,
                    'height': barSize.height,
                    'position': 'absolute',
                    'background-color': moveBlockBackgroundColor,
                    'left': moveBlockLeft,
                    'transition': transitionLeft,
                    'border': `1px solid ${leftBarBorderColor}`,
                }" @touchstart="start" @mousedown="start">
                    <GiSvgIcon :name="iconClass" :color="iconColor" />
                    <div class="verify-sub-block" :style="{
                        'width':
                            `${Math.floor((parseInt(setSize.imgWidth) * 47) / 310)}px`,
                        'height': setSize.imgHeight,
                        'position': 'absolute',
                        'top': `-${parseInt(setSize.imgHeight) + vSpace}px`,
                        'background-size': `${setSize.imgWidth} ${setSize.imgHeight}`,
                    }">
                        <img :src="`data:image/png;base64,${blockBackImgBase}`" alt="" style="
                width: 100%;
                height: 100%;
                display: block;
                -webkit-user-drag: none;
                padding-left: 2px;
              " />
                    </div>
                </div>


            </div>
            <div class="msg" v-if="!status">
                <span>向右滑动完成验证</span>
            </div>

        </div>

    </div>
</template>
<style lang="scss" scoped>
.verify-move-block {
    display: flex;
    align-items: center;
    justify-content: center;

}

.bottom {
    display: flex;
    align-items: center;

    .msg {
        margin-left: 100px;
        font-size: 16px;
        color: #45494c;
    }

}

.left-bar {
    position: absolute;
    top: -1px;
    left: -1px;
    background: #f0fff0;
    cursor: pointer;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    border: 1px solid #ddd;
}

.middle {
    position: relative;

    .refresh-icon {
        font-size: 20px;
        position: absolute;
        top: 10px;
        right: 10px;
        color: #999;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
            color: #333;
        }
    }
}

.verify-tips {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 30px;
    line-height: 30px;
    color: #fff;

}

.suc-bg {
    background-color: rgba(92, 184, 92, 0.5);
    filter: progid:DXImageTransform.Microsoft.gradient(startcolorstr=#7f5CB85C, endcolorstr=#7f5CB85C);
}

.err-bg {
    background-color: rgba(217, 83, 79, 0.5);
    filter: progid:DXImageTransform.Microsoft.gradient(startcolorstr=#7fD9534F, endcolorstr=#7fD9534F);
}
</style>