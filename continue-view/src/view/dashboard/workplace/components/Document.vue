<script lang="ts" setup>
const links = [
    { text: '项目简介', url: 'https://continew.top/docs/admin/guide/introduction.html' },
    { text: '快速开始', url: 'https://continew.top/docs/admin/guide/quick-start.html' },
    { text: '常见问题', url: 'https://continew.top/docs/admin/faq.html' },
    { text: '更新日志', url: 'https://continew.top/docs/admin/changelog/' },
    { text: '贡献指南', url: 'https://continew.top/about/contributing.html' },
    { text: '赞助支持 💖', url: 'https://continew.top/sponsor/' },
]
</script>

<template>
    <el-card>
        <template #header>
            <div class="title">
                <span>帮助文档</span>
                <el-link href="https://continew.top" target="_blank" rel="noopener">更多</el-link>
            </div>
        </template>
        <template #default>
            <el-row :gutter="20">
                <el-col v-for="link in links" :key="link.text" :span="12">
                    <el-link :href="link.url" target="_blank" rel="noopener">
                        {{ link.text }}
                    </el-link>
                </el-col>
            </el-row>
        </template>
    </el-card>
</template>

<style lang="scss" scoped>
.title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: var(--color-text-1);
}

.el-card {
    background: var(--color-bg-1);
}

.el-col {
    margin-bottom: 20px;
}

.el-link {
    color: var(--color-text-2);
}

::v-deep .el-card__header {
    height: 30px;
    padding: 15px;
    border-bottom: 0px;
}
</style>