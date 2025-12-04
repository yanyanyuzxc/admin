<script lang="ts" setup>
import { isHttp } from '@/utils/validate'
import { ref, onMounted } from 'vue'
export interface DataItem {
    name: string
    img: string
    url: string
}

const images = ref<DataItem[]>([
    {
        name: '公众号',
        img: `https://continew.top/images/sponsor/ads/cn-qrcode.jpg?${new Date().getTime()}`,
        url: 'https://continew.top/discussion.html',
    },
    {
        name: '赞助',
        img: `https://continew.top/images/sponsor/ads/cn-sponsor.jpg?${new Date().getTime()}`,
        url: 'https://continew.top/sponsor/',
    },
])
const dataList = ref<DataItem[]>([])
const loading = ref(false)
const getDataList = async () => {
    try {
        loading.value = true
        const base = `https://continew.top`
        const data = await (await fetch(`${base}/sponsor.json?${new Date().getTime()}`)).json()
        if (data) {
            // 只获取 special 和 platinum 赞助者
            const sponsors = [...data.special, ...data.platinum]
            sponsors.forEach((item) => {
                if (!item.name) {
                    return
                }
                dataList.value.push({
                    name: item.name,
                    img: isHttp(item.img) ? item.img : `${base}/images/sponsor/ads/${item.img}`,
                    url: item.url,
                })
            })
            dataList.value = [...dataList.value, ...images.value]
        } else {
            dataList.value = images.value
        }
    } catch (err) {
        // console.log(err)
    } finally {
        loading.value = false
    }
}

onMounted(async () => {
    await getDataList()
})
</script>

<template>
    <el-carousel height="150px">
        <el-carousel-item v-for="item in dataList" :key="item.url">
            <div>
                <el-link :href="item.url" target="_blank" rel="noopener">
                    <img :src="item.img" style="width: 100%; height: 150px;" :alt="item.name" />
                </el-link>
            </div>
        </el-carousel-item>
    </el-carousel>
</template>

<style lang="scss" scoped>
li ::v-deep {
    width: 20px;
}
</style>