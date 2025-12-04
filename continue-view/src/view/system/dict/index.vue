<script lang="ts" setup>
import { ref } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import SparkMD5 from 'spark-md5'

let p = 0;
let url = ref('');
const block = ref('upload');
let cancelTimer: (() => void) | null = null;

// 点击上传：触发隐藏的文件选择框
const handleClick = () => {
    if (block.value === 'upload') {
        const input = document.querySelector(".input-file") as HTMLInputElement;
        input.click();
    }
};
// 定义函数类型：接收文件和切片大小，返回切片数组
const createChunks = (file: File, chunkSize: number): Blob[] => {
    const chunks: Blob[] = [];
    // 循环切割文件，每次取 chunkSize 大小的片段
    for (let i = 0; i < file.size; i += chunkSize) {
        // slice 方法：end 超过文件大小会自动取到末尾，避免越界
        const chunk = file.slice(i, i + chunkSize);
        chunks.push(chunk);
    }
    return chunks;
};
//处理文件名，使用哈希算法
const hash = (chunks: Blob[]) => {
    return new Promise((resolve) => {
        const spark = new SparkMD5.ArrayBuffer()

        const read = (index: number) => {
            const reader = new FileReader();

            if (index >= chunks.length) {
                const hash = spark.end();
                resolve(hash);
                return
            }
            reader.onload = (e) => {
                const bytes = e.target?.result as ArrayBuffer;
                spark.append(bytes);
                index++;
                read(index);
            }
            reader.readAsArrayBuffer(chunks[index]);
        }
        read(0)
    })
}
// 共用文件处理逻辑（点击/拖拽都用）
const handleFile = async (file: File) => {
    const chunks = createChunks(file, 1024 * 1000); // 2MB
    const hashTitle = await hash(chunks)
    console.log(hashTitle)
    // 上传文件
    // if (file) {
    //     block.value = 'progress';
    //     url.value = '';
    //     p = 0;
    //     cancelTimer?.();

    //     // 读取文件预览
    //     const reader = new FileReader();
    //     reader.onload = (e) => {
    //         url.value = e.target?.result as string;
    //     };
    //     reader.readAsDataURL(file);

    //     // 模拟上传进度
    //     cancelTimer = load(() => {
    //         p++;
    //         setProgress(p);
    //         if (p >= 100) {
    //             cancelTimer?.();
    //             block.value = 'img';
    //         }
    //     });
    // }
};

// 点击选择文件回调
const handleLoad = (e: Event) => {
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) handleFile(file);
};

// 拖拽放下事件（核心：阻止跳转+处理文件）
const handleDrop = (e: DragEvent) => {
    e.preventDefault(); // 阻止浏览器打开文件
    e.stopPropagation();

    // e.dataTransfer?.items?.forEach((file)=>{
    //     console.log(file)
    // })

    if (e.dataTransfer?.items) {
        for (const item of e.dataTransfer.items) {
            const entry = item.webkitGetAsEntry();
            if (entry?.isDirectory) {
                // console.log('directory')
                const reader = (entry as FileSystemDirectoryEntry).createReader();
                reader.readEntries((entries: any) => {
                    console.log(entries)
                })
            }
            else {
                (entry as FileSystemFileEntry)?.file((file: any) => {
                    console.log(file)
                })
            }
        }
    }
    const file = e.dataTransfer?.files?.[0];

    if (file && block.value === 'upload') {
        handleFile(file);
    }
};

// 阻止拖拽相关默认行为（必须绑在可见容器上）
const preventDragDefault = (e: DragEvent) => {
    e.preventDefault();

    e.stopPropagation();
};

// 模拟上传进度
const load = (onProgress: () => void) => {
    onProgress();
    const timer = setInterval(() => {
        if (p < 100) onProgress();
        else clearInterval(timer);
    }, 50);
    return () => clearInterval(timer);
};

// 更新进度条
const setProgress = (percent: number) => {
    const progressBar = document.querySelector(".progress-bar") as HTMLDivElement;
    if (progressBar) progressBar.style.width = `${Math.min(percent, 100)}%`;
};

// 删除预览图
const handleDelete = () => {
    url.value = '';
    block.value = 'upload';
    p = 0;
    cancelTimer?.();
    const progressBar = document.querySelector(".progress-bar") as HTMLDivElement;
    if (progressBar) progressBar.style.width = '0%';
};
</script>

<template>
    <!-- 关键：所有拖拽事件都绑在可见的 .input 容器上 -->
    <div class="input" @click="handleClick" @dragover="preventDragDefault" @dragenter="preventDragDefault"
        @dragleave="preventDragDefault" @drop="handleDrop">
        <!-- 文件选择框隐藏（单独隐藏，不影响父容器） -->
        <!-- <input type="file" @change="handleLoad" class="input-file" style="display: none;" multiple webkitdirectory /> -->
        <input type="file" @change="handleLoad" class="input-file" style="display: none;" multiple />
        <!-- 上传图标 -->
        <div class="upload" v-show="block === 'upload'">
            <El-icon size="100">
                <Plus />
            </El-icon>
        </div>

        <!-- 进度条 -->
        <div class="progress" v-show="block === 'progress'">
            <div class="progress-bar"></div>
        </div>

        <!-- 图片预览（用v-bind绑定，更可靠） -->
        <div class="img" v-show="block === 'img'">
            <img :src="url" alt="预览图" />
        </div>
    </div>
    <el-button @click="handleDelete" style="margin-top: 10px;">删除</el-button>
</template>

<style lang="scss" scoped>
.progress {
    height: 20px;
    width: 160px;
    background-color: #f5f5f5;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-top: 10px;
    overflow: hidden;
}

.img {
    width: 80%;
    height: 80%;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }
}

.progress-bar {
    height: 100%;
    background-color: #409eff;
    border-radius: 5px;
    transition: width 0.1s ease;
}

.input {
    width: 300px;
    height: 300px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    color: rgb(209, 211, 213);
    border: 1px dashed #ccc;
    cursor: pointer;
    transition: border-color 0.3s ease;

    &:hover {
        border-color: #409eff; // 提示可交互
    }
}
</style>