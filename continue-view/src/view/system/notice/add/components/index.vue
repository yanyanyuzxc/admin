<script lang="ts" setup>
import { AiEditor, type AiEditorOptions } from 'aieditor'
import 'aieditor/dist/style.css'
import { useAppStore } from '@/store'
// 2. 定义组件名称
defineOptions({ name: 'AiEditor' })
// 3. 输入：父组件传给编辑器的参数
const props = defineProps<{
    modelValue: string // 双向绑定的内容（v-model 核心）
    options?: AiEditorOptions // 自定义配置（如占位符、主题）
}>()

// 4. 输出：编辑器给父组件回传的事件
const emit = defineEmits<(e: 'update:modelValue', value: string) => void>()
const divRef = ref<any>() // 根容器（挂载编辑器）
const aieditor = ref<AiEditor | null>(null) // 编辑器实例（方便后续调用 API）
const appStore = useAppStore() // Pinia 实例（可选，主题同步）
// 2. 编辑器配置：整合默认配置 + 父组件自定义配置
const editorConfig = reactive<AiEditorOptions>({
    element: '', // 挂载容器（后续赋值）
    theme: appStore.theme, // 同步项目主题（可选）
    placeholder: '请输入内容', // 默认占位符
    content: '', // 初始内容（后续从 props 同步）
    draggable: false, // 禁用拖拽（可按需改）
    // 内容变化时触发：同步给父组件 + 更新目录
    onChange: (editor: AiEditor) => {
        emit('update:modelValue', editor.getHtml()) // 回传内容给父组件
        updateOutLine(editor) // 更新目录
    },
    // 编辑器创建完成时触发：初始化目录
    onCreated: (editor: AiEditor) => {
        updateOutLine(editor)
    },
})
// 3. 初始化函数：创建编辑器实例
const init = () => {
    editorConfig.element = divRef.value // 绑定挂载容器
    editorConfig.content = props.modelValue // 同步父组件传入的初始内容
    aieditor.value = new AiEditor(editorConfig) // 创建实例
}

// 4. 生命周期：组件挂载时初始化
onMounted(() => {
    init()
})
// 监听父组件传入的 modelValue：如果父组件改了内容，同步到编辑器
watch(() => props.modelValue, (newValue) => {
    // 避免重复更新（编辑器内容和父组件一致时不处理）
    if (newValue !== aieditor.value?.getHtml()) {
        aieditor.value?.destroy() // 销毁旧实例
        editorConfig.content = newValue // 同步新内容
        aieditor.value = new AiEditor(editorConfig) // 重新创建实例
    }
})
// 更新目录函数：接收编辑器实例，动态生成目录
const updateOutLine = (editor: AiEditor) => {
    const outlineContainer = document.querySelector('#outline')
    // 清空旧目录
    while (outlineContainer?.firstChild) {
        outlineContainer.removeChild(outlineContainer.firstChild)
    }
    //获取层级
    const outlines = editor.getOutline()
    // 生成目录
    outlines.forEach((outline) => {
        const child = document.createElement('div')
        child.classList.add(`aie-title${outline.level}`)
        child.style.marginLeft = `${14 * (outline.level - 1)}px`
        child.style.padding = `4px 0`
        child.innerHTML = `<a href="#${outline.id}">${outline.text}</a>` //无js的时候实现跳转效果
        // child.addEventListener('click', (e) => {
        //     e.preventDefault()
        //     const el = editor.innerEditor.view.dom.querySelector(`#${outline.id}`) as HTMLElement
        //     el.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' })
        //     setTimeout(() => {
        //         editor.focusPos(outline.pos + outline.size - 1)
        //     }, 1000)
        // })
        outlineContainer?.appendChild(child)
    })
}

</script>

<template>
    <div ref="divRef" class="container" style="height: 600px">
        <div class="aie-container">
            <div class="aie-header-panel">
                <div class="aie-container-header"></div>
            </div>
            <div class="aie-main">
                <div class="aie-directory-content">
                    <div class="aie-directory">
                        <h5>目录</h5>
                        <div id="outline">
                        </div>
                    </div>
                </div>
                <div class="aie-container-panel">
                    <div class="aie-container-main"></div>
                </div>
            </div>
            <div class="aie-container-footer"></div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.aie-header-panel {
    position: sticky;
    // top: 51px;
    z-index: 1;
}

.aie-header-panel aie-header>div {
    align-items: center;
    justify-content: center;
    padding: 10px 0;
}

.container {
    height: 100%;
}

.container {
    height: 100%;
    width: 100%;
    box-sizing: border-box;
}

// 主体区域：flex 布局，目录+编辑区并排
.aie-main {
    position: relative;
    overflow: hidden;
    flex: 1;
    padding: 1rem 0;
    background-color: var(--color-bg-2); // 项目全局背景色
}

// 目录区：左侧固定，小屏隐藏
.aie-directory {
    position: absolute;
    top: 30px;
    left: 10px;
    width: 260px;
    z-index: 0;

    // 目录标题
    h5 {
        font-size: 16px;
        text-indent: 4px;
        line-height: 32px;
    }

    // 目录链接
    a {
        display: inline-block;
        width: 100%;
        height: 30px;
        font-size: 14px;
        text-indent: 4px;
        line-height: 30px;
        text-decoration: none;
        white-space: nowrap; // 不换行
        overflow: hidden;
        text-overflow: ellipsis; // 超出部分省略号
    }

    // 链接 hover 效果
    a:hover {
        cursor: pointer;
        border-radius: 4px;
        background-color: #334d660f;
    }
}

// 目录容器：整体缩进
#outline {
    text-indent: 2rem;
}

// 编辑区：居中显示，限制最大宽度（避免内容太宽）
.aie-container-panel {
    width: calc(100% - 2rem - 2px);
    max-width: 826.77px; // 最大宽度，提升阅读体验
    margin: 0 auto;
    border: 1px solid var(--color-border-1);
    background-color: #fff; // 白色背景，突出编辑内容
    height: 100%;
    padding: 1rem;
    overflow: auto;
    box-sizing: border-box;
}

// 响应式：小屏隐藏目录
@media screen and (max-width: 1280px) {
    .aie-directory {
        display: none;
    }
}

// 响应式：中屏缩小目录宽度
@media screen and (max-width: 1400px) {
    .aie-directory {
        width: 200px;
    }
}
</style>