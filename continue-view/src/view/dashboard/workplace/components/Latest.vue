<script lang="ts" setup>
import { useUserStore } from '@/store'
import { onMounted, ref } from 'vue'
import http from '@/utils/http'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')
let userStore = useUserStore()
const dataList = ref<DataItem[]>([])
const loading = ref(false)
interface commit {
    sha: string,
    url: string,
    message: string
}
export interface DataItem {
    platform: string
    type: string
    actor: {
        username: string
        nickname: string
        avatar: string
        url: string
    }
    repo: {
        name: string
        alias: string
        url: string
    }
    payload: {
        action: string,
        state: string,
        url: string,
        stateString: string,
        title: string,
        branch: string,
        commits: commit[],
        ref: string,
        refType: string
    }
    createTime: string
    createTimeString: string
}
const getData = async () => {
    try {
        loading.value = true
        let result = await http.get('https://api.charles7c.top/git/orgs/events/continew')

        if (result.success) {
            const data = result.data
            data.forEach((item: any) => {
                dataList.value.push({
                    ...item,
                    createTimeString: dayjs(new Date(item.createTime)).fromNow(),
                })
            })
        }
    } catch (error) {
        console.log(error)
    } finally {
        loading.value = false
    }
}
onMounted(() => {
    getData()
})
</script>

<template>
    <div class="latest">
        <div class="top">
            <span>最近更新</span>
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

        <!-- <el-skeleton :rows="5" animated /> -->

        <div>
            <!-- <el-empty>暂无动态</el-empty> -->

            <div class="body">

                <div class="item" v-for="(item, index) in dataList" :key="index">
                    <el-badge class="item-1" :offset="[-5, 2]">
                        <div>
                            <Avatar :src="item.actor.avatar" :name="userStore.nickname" :size="44" />
                        </div>
                        <template #content>
                            <GiSvgIcon v-if="item.platform === 'GitHub'" name="github" :size="15" />
                            <GiSvgIcon v-else-if="item.platform === 'Gitee'" name="gitee" :size="15" />
                        </template>
                    </el-badge>

                    <div class="content">
                        <p :title="item.createTime" class="name">
                            <span>{{ item.actor.username }}</span>
                            <span style="display:inline-block; margin-left: 10px;">{{ item.createTimeString }}</span>
                        </p>
                        <div v-if="item.type === 'PUSH'" style="margin-top:0">

                            <span>推送到了<el-link :href="item.repo.url" target="_blank" :underline="false"
                                    type="primary">{{
                                        item.repo.alias
                                    }}</el-link></span>
                            {{ `的 ${item.payload.branch} 分支 ${item.payload.commits.length} 个提交` }}
                            <div v-for="(commit, idx) in item.payload.commits" :key="idx" class="commit">

                                <el-link :href="commit.url" target="_blank" rel="noopener" style="font-size: 12px"
                                    :title="commit.message" type="primary">{{ commit.sha.substring(0, 7) }}</el-link>
                                <a :href="commit.url" target="_blank" rel="noopener" :title="commit.message"
                                    class="link">{{
                                        commit.message }}</a>

                            </div>
                        </div>
                        <p v-if="item.type === 'ISSUE_OPEN'">
                            在 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            创建了 Issue <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                        </p>
                        <p v-if="item.type === 'ISSUE_CLOSE' || item.type === 'ISSUE_REOPEN'">
                            更改了 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            的 Issue <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                            状态为 {{ item.payload.stateString }}
                        </p>
                        <p v-if="item.type === 'ISSUE_COMMENT'">
                            评论了 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            的 Issue <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                        </p>
                        <p v-if="item.type === 'PULL_REQUEST_OPEN'">
                            在 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            创建了 Pull Request <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                        </p>
                        <p v-if="item.type === 'PULL_REQUEST_MERGE'">
                            接受了 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            的 Pull Request <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                        </p>
                        <p v-if="item.type === 'PULL_REQUEST_CLOSE' || item.type === 'PULL_REQUEST_REOPEN'">
                            更改了 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            的 Pull Request <a-link :href="item.payload.url" target="_blank" rel="noopener">{{
                                item.payload.title
                            }}</a-link>
                            状态为 {{ item.payload.stateString }}
                        </p>
                        <p v-if="item.type === 'CREATE'">
                            推送了新的 {{ item.payload.refType }}
                            <a-link :href="item.payload.url" target="_blank" rel="noopener">{{ item.payload.ref
                            }}</a-link>
                            到 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                        </p>
                        <p v-if="item.type === 'DELETE'">
                            删除了 <a-link :href="item.repo.url" target="_blank" rel="noopener">{{ item.repo.alias
                            }}</a-link>
                            的 {{ item.payload.ref }} {{ item.payload.refType }}
                        </p>
                        <!-- <p v-else>暂无</p> -->
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.latest {
    padding: 2%;
    height: 100%;

    .el-badge * {
        border: 0px;
    }

    .item {
        margin-bottom: 20px;
        display: flex;
        border-bottom: 1px solid var(--color-border);
        min-height: fit-content;
        padding: 10px 0;

        .content {
            margin-left: 20px;
            margin-top: 0px;

            color: var(--color-text-1);
        }


        p {



            .commit {
                margin-top: 10px;
            }

            .link {
                text-decoration: none;
                color: var(--color-text-5);
            }
        }
    }

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

    .top {
        @include flex-between;
        height: 10%;
        color: var(--color-text-1);

        span {
            font-size: 1.2em;
        }
    }
}
</style>