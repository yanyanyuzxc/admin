<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'
let dialogVisible = ref(false)

const handleInput = () => {
    dialogVisible.value = true
}
const Switch = () => {

}
const Select = () => {

}
</script>

<template>
    <div class="search">
        <div class="input" @click="handleInput">
            <el-icon color="">
                <Search />

            </el-icon>
            <span>搜索</span>

            <el-tag style="height:15px; margin-left:15px;border:none; background-color: var(--color-bg-3);" type="info"
                effect="plain">
                <span class="btn" style="color:var(--color-text-3);">Crtl+k</span>
            </el-tag>
        </div>
        <el-dialog v-model="dialogVisible" width="500" :show-close="false">
            <template #header>
                <div class="search">
                    <el-input :prefix-icon="Search" placeholder="搜索页面" style="width: 250px;"></el-input>
                    <el-button @click="dialogVisible = false" class="btn" type="info" size="small">ESC 退出</el-button>
                </div>
                <el-divider style="margin: 30px 0" />
                <el-divider style="margin:0" />
            </template>


            <div class="dialog-footer">
                <el-button @click="Switch">
                    <GiSvgIcon name="select" :size="12" class="shortcut-icon" />
                    切换
                </el-button>
                <el-button @click="Select">
                    <GiSvgIcon name="shortcut-enter" :size="12" class="shortcut-icon" />选择
                </el-button>
            </div>

        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.input {
    width: 40%;
    padding: 5px;
    // margin-top: 20px;
    height: 30px;
    // border: 1px solid rgba(0, 15, 15, 0.12);
    border-radius: 10px;

    min-width: 150px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(242, 243, 245);

    background-color: var(--color-bg-4);

    .el-icon {
        // margin: 5px 7px;
        margin-right: 4px;
        font-size: 1em;
        color: var(--color-text-3);
    }

    p {
        font-size: 1em;
        flex-wrap: nowrap;
        // overflow: hidden;
        // margin-top: 5px;
    }

    .btn {
        font-size: 1em;
        font-weight: 300;

    }
}

.dialog-footer {
    display: flex;
    justify-content: center;

    .el-button {
        background: rgba(242, 243, 245);
    }
}

.search {
    display: flex;
    justify-content: space-between;
    width: fit-content;


    .btn {
        width: 60px;
        margin-top: 5px;
        background: rgba(242, 243, 245);
        border: none;
        color: rgb(105, 104, 104);
        border-radius: 10px;
        font-weight: 300;
    }
}
</style>