<script lang="ts" setup>
import { ref } from 'vue'
const drawRef = ref(false)
const open = () => {
    drawRef.value = true
}
defineExpose({
    open
})
</script>

<template>
    <el-drawer title="导入用户" show-close v-model="drawRef">
        <template #default>
            <el-form>
                <el-form-item>
                    <el-alert type="primary">
                        <template #title>
                            <span> 请按照模板要求填写数据，填写完毕后，请先上传并进行解析。</span>
                            <GiSvgIcon name="file-excel" :size="16" />
                            <el-link>下载模板</el-link>
                        </template>
                    </el-alert>
                </el-form-item>
                <el-form-item>
                    <el-upload class="upload-demo" drag
                        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" multiple>
                        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                        <div class="el-upload__text">
                            Drop file here or <em>click to upload</em>
                        </div>
                        <template #tip>
                            <div class="el-upload__tip">
                                jpg/png files with a size less than 500kb
                            </div>
                        </template>
                    </el-upload>
                </el-form-item>
                <el-form-item label="用户已存在">
                    <el-space>
                        <el-radio-group size="large" fill="#6cf">
                            <el-radio-button label="New York" value="New York" />
                            <el-radio-button label="Washington" value="Washington" />
                            <el-radio-button label="Los Angeles" value="Los Angeles" />
                        </el-radio-group>
                    </el-space>
                </el-form-item>
                <el-form-item label="邮箱已存在">
                    <el-space>
                        <el-radio-group size="large" fill="#6cf">
                            <el-radio-button label="New York" value="New York" />
                            <el-radio-button label="Washington" value="Washington" />
                            <el-radio-button label="Los Angeles" value="Los Angeles" />
                        </el-radio-group>
                    </el-space>
                </el-form-item>
                <el-form-item label="手机已存在">
                    <el-space>
                        <el-radio-group size="large" fill="#6cf">
                            <el-radio-button label="New York" value="New York" />
                            <el-radio-button label="Washington" value="Washington" />
                            <el-radio-button label="Los Angeles" value="Los Angeles" />
                        </el-radio-group>
                    </el-space>
                </el-form-item>
                <el-form-item label="默认状态">
                    <el-switch></el-switch>
                </el-form-item>
            </el-form>
        </template>
        <template #footer>
            <el-button>取消</el-button>
            <el-button>确认</el-button>
        </template>
    </el-drawer>
</template>

<style lang="scss" scoped></style>