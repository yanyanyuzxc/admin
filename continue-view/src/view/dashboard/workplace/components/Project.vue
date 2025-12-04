<script setup lang="ts">
const list = [
    {
        alias: 'ContiNew Admin',
        name: 'continew-admin',
        owner: 'continew-org',
        desc: '🔥Almost最佳后端规范🔥持续迭代优化的前后端分离中后台管理系统框架，开箱即用，持续提供舒适的开发体验。',
        logo: 'https://continew.top/logo.svg',
        url: 'https://gitee.com/continew/continew-admin/stargazers',
        status: '迭代',
        statusColor: 'rgb(var(--primary-6))',
    },
    {
        alias: 'ContiNew Starter',
        name: 'continew-starter',
        owner: 'continew-org',
        desc: '🔥高质量Starter🔥包含了一系列经过企业实践优化的依赖包（如 MyBatis-Plus、SaToken），可轻松集成到应用中，为开发人员减少手动引入依赖及配置的麻烦，为 Spring Boot Web 项目的灵活快速构建提供支持。',
        logo: 'https://continew.top/logo.svg',
        url: 'https://gitee.com/continew/continew-starter/stargazers',
        status: '迭代',
        statusColor: 'rgb(var(--primary-6))',
    },
    {
        alias: 'ContiNew Admin UI',
        name: 'continew-admin-ui',
        owner: 'continew-org',
        desc: '全新 3.x 版本，基于 Gi Demo 前端模板开发的 ContiNew Admin 前端适配项目。',
        logo: 'https://continew.top/logo.svg',
        url: 'https://gitee.com/continew/continew-admin-ui/stargazers',
        status: '迭代',
        statusColor: 'rgb(var(--primary-6))',
    },
    {
        alias: 'ContiNew Admin UI Arco',
        name: 'continew-admin-ui-arco',
        owner: 'continew-org',
        desc: '2.5 版本，基于 Arco Design Pro 前端模板开发的 ContiNew Admin 前端适配项目。',
        logo: 'https://continew.top/logo.svg',
        url: 'https://gitee.com/continew/continew-admin-ui-arco/stargazers',
        status: '归档',
        statusColor: 'rgb(var(--warning-6))',
    },
    {
        alias: 'ContiNew Cloud',
        name: 'continew-admin',
        owner: 'continew',
        desc: 'ContiNew Admin 微服务版本。基于 SpringBoot 3.x、Spring Cloud 2023 & Alibaba。',
        logo: 'https://continew.top/logo.svg',
        url: '#',
        status: '孵化',
        statusColor: 'rgb(var(--danger-6))',
    },
    {
        alias: 'charles7c.github.io',
        name: 'charles7c.github.io',
        owner: 'charles7c',
        desc: '基于 VitePress 构建的个人知识库/博客。扩展 VitePress 默认主题：增加ICP备案号、公安备案号显示，增加文章元数据信息（原创标识、作者、发布时间、分类、标签）显示，增加文末版权声明，增加 Gitalk 评论功能，主页美化、自动生成侧边栏、文章内支持 Mermaid 流程图、MD公式、MD脚注、增加我的标签、我的归档等独立页面，以及浏览器滚条等细节优化。',
        logo: 'https://charles7c.top/logo.png',
        url: 'https://github.com/Charles7c/charles7c.github.io/stargazers',
        status: '归档',
        statusColor: 'rgb(var(--warning-6))',
    },
]
</script>


<template>
    <div class="project">
        <div class="top">
            <span>我的项目</span>
            <el-dropdown>
                <p class="el-dropdown-link">
                    更多
                </p>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item> <a-link href="https://gitee.com/charles7c" target="_blank"
                                rel="noopener">Gitee</a-link></el-dropdown-item>
                        <el-dropdown-item> <a-link href="https://gitcode.com/charles_7c" target="_blank"
                                rel="noopener">GitCode</a-link></el-dropdown-item>
                        <el-dropdown-item> <a-link href="https://github.com/charles7c" target="_blank"
                                rel="noopener">GitHub</a-link></el-dropdown-item>

                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
        <div class="body">
            <el-row :gutter="20">
                <el-col :span="8" v-for="(item, index) in list" :key="index" class="col">
                    <el-card>
                        <template #header>
                            <div class="title">
                                <div class="img-box">
                                    <img :src="item.logo" alt="">
                                </div>
                                <p>{{ item.alias }}</p>
                                <p class="status" :style="`background-color: ${item.statusColor}`">{{ item.status }}
                                </p>
                            </div>
                        </template>
                        <template #default>
                            <span>{{ item.desc }}</span>
                        </template>
                        <template #footer>
                            <el-tooltip content="点个赞吧">
                                <template #default>
                                    <div class="icon-hover">
                                        <a :href="item.url" target="_blank" rel="noopener">
                                            <GiSvgIcon name="thumb-up" size="24" color="rgb(var(--primary-6))">
                                            </GiSvgIcon>
                                        </a>
                                    </div>
                                </template>
                            </el-tooltip>
                        </template>
                    </el-card>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.project * {
    color: var(--color-text-1);
}

.project {
    padding: 2%;
    height: 100%;
    color: var(--color-text-1);

    /* 或者更精确地针对这个场景 */
    .el-dropdown-link {
        padding: 3px;
        font-size: 1.2em;
    }

    .el-dropdown .el-dropdown-link:focus {
        outline: none;
        background: rgb(232, 233, 234);
        color: rgba(var(--primary-6))
            /* 可选：如果需要自定义焦点样式，可以在这里添加 */
            /* box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2); */
    }

    .icon-hover {
        position: absolute;
        right: 10px;
        bottom: 10px;
        color: rgba(var(--primary-6));
        width: 30px;
        height: 30px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        transition: all 0.1s;
        animation: icon-hover-animated 1.2s ease-in-out infinite;
    }

    .icon-hover:hover {
        background-color: rgba(var(--primary-6));
    }

    @keyframes icon-hover-animated {
        50% {
            transform: scale(0.8);
        }
    }

    .el-card {
        margin-bottom: 20px;
        position: relative;

        & ::v-deep .el-card__header {
            padding: 7px 15px;
            border-bottom: 0px;
            height: 15%;
        }

        & ::v-deep .el-card__footer {
            border-top: 0px;
        }

        & ::v-deep .el-card__body {
            height: 30px;
            padding: 7px 20px;

            span {
                @include text-ellipsis(2);

            }
        }
    }



    .body {
        margin-top: 10px;

        .title {
            display: flex;
            align-items: center;
            position: relative;

            .status {
                background: red;
                position: absolute;
                top: -15px;
                width: 60px;
                text-align: center;
                right: -30px;
                transform: rotate(45deg);
            }
        }

        .img-box {
            width: 8%;
            height: 8%;
            margin: auto 10px;

            img {
                width: 100%;
                height: 100%;
            }
        }

    }

    .top {
        @include flex-between;
        height: 10%;


        span {
            font-size: 1.2em;

        }
    }
}
</style>