<script lang="ts" setup>
import { type ColumnItem, GiForm } from '@/components/GiForm'
import { ElMessage } from 'element-plus'
import type { LabelValueState } from '@/type/global'
import { useResetReactive } from '@/hooks'
import type { TableInstance } from '@arco-design/web-vue'
import { useDict } from '@/hooks/app'
import { useWindowSize } from '@vueuse/core'
import { type FieldConfigResp, type GeneratorConfigResp, getGenConfig, listFieldConfig, listFieldConfigDict, saveGenConfig } from '@/apis/code/generator'

const title = ref('')
const { width } = useWindowSize()
const visible = ref(false)
// 打开
const onOpen = async (tableName: string, comment: string) => {
  reset()
  comment = comment ? `（${comment}）` : ' '
  title.value = `${tableName}${comment}配置`
  // 查询生成配置
  const { data } = await getGenConfig(tableName)
  Object.assign(form, data)
  form.isOverride = form.isOverride || false
  visible.value = true
  // 查询字段配置
  await getDataList(tableName, false)
  const res = await listFieldConfigDict()
  dictList.value = res.data
}
const [form, resetForm] = useResetReactive({
  isOverride: false,
})
// 重置
const reset = () => {
  formRef.value?.formRef?.resetFields()
  resetForm()
}
const dataList = ref<FieldConfigResp[]>([])
const cancelClick = () => {
  visible.value = false
}

const emit = defineEmits<{
  (e: 'save-success'): void
}>()
const formRef = ref<InstanceType<typeof GiForm>>()

// 保存
const save = async () => {
  try {

    const isInvalid = await formRef.value?.formRef?.validate()
    if (!isInvalid) {
      activeKey.value = '1'
      return false
    }
    await saveGenConfig(form.tableName, {
      genConfig: form,
      fieldConfigs: dataList.value,
    } as GeneratorConfigResp)
    ElMessage.success('保存成功')
    emit('save-success')
    visible.value = false
    return true
  } catch (error) {
    return false
  }
}
const formColumns: ColumnItem[] = reactive([
  {
    label: '作者名称',
    field: 'author',
    type: 'input',
    required: true,
    props: {
      maxLength: 100,
    },
  },
  {
    label: '业务名称',
    field: 'businessName',
    type: 'input',
    props: {
      placeholder: '自定义业务名称，例如：用户',
      maxLength: 50,
    },
    rules: [{ required: true, message: '请输入业务名称' }],
  },
  {
    label: '所属模块',
    field: 'moduleName',
    type: 'input',
    props: {
      placeholder: '项目模块名称，例如：contiold-system',
      maxLength: 60,
      showWordLimit: true,
    },
    rules: [{ required: true, message: '请输入所属模块' }],
  },
  {
    label: '模块包名',
    field: 'packageName',
    type: 'input',
    props: {
      placeholder: '项目模块包名，例如：top.continew.admin.system',
      maxLength: 60,
    },
    rules: [{ required: true, message: '请输入模块包名' }],
  },
  {
    label: '去表前缀',
    field: 'tablePrefix',
    type: 'input',
    props: {
      placeholder: '数据库表前缀，例如：sys_',
      maxLength: 20,
    },
  },
  {
    label: '是否覆盖',
    field: 'isOverride',
    type: 'switch',
    props: {
      type: 'round',
      checkedValue: true,
      uncheckedValue: false,
      checkedText: '是',
      uncheckedText: '否',
    },
  },
])
// Table 字段配置
const columns: TableInstance['columns'] = [
  { title: '名称', slotName: 'fieldName' },
  { title: '类型', slotName: 'fieldType' },
  { title: '描述', slotName: 'comment', width: 170 },
  { title: '列表', slotName: 'showInList', width: 60, align: 'center' },
  { title: '表单', slotName: 'showInForm', width: 60, align: 'center' },
  { title: '必填', slotName: 'isRequired', width: 60, align: 'center' },
  { title: '查询', slotName: 'showInQuery', width: 60, align: 'center' },
  { title: '表单类型', slotName: 'formType' },
  { title: '查询方式', slotName: 'queryType' },
  { title: '关联字典', slotName: 'dictCode' },
]
const activeKey = ref('1')
const loading = ref(false)
const { form_type_enum, query_type_enum } = useDict('form_type_enum', 'query_type_enum')


// 同步
const handleRefresh = async (tableName: string) => {
  await getDataList(tableName, true)
}
const dictList = ref<LabelValueState[]>([])
// 查询列表数据
const getDataList = async (tableName: string, requireSync: boolean) => {
  try {
    // loading.value = true
    const { data } = await listFieldConfig(tableName, requireSync)
    // console.log(data)
    dataList.value = data

  } finally {
    loading.value = false
  }
}
const handleChange = (newDataList: FieldConfigResp[]) => {
  dataList.value = newDataList
}
defineExpose({
  onOpen
})
</script>
<template>
  <el-drawer v-model="visible" size="80%">
    <template #header>
      <div class="custom-header">
        <span>{{ title }}应用表配置</span>
      </div>
    </template>
    <template #default>
      <div class="body">
        <el-tabs v-model:active-key="activeKey">
          <el-tab-pane key="1" label="生成配置">
            <GiForm ref="formRef" v-model="form" :columns="formColumns" />
          </el-tab-pane>
          <el-tab-pane key="2" label="字段配置">
            <GiTable row-key="tableName" :data="dataList" :columns="columns" :loading="loading" :pagination="false"
              :draggable="{ type: 'handle', width: 40 }" :disabled-tools="['setting', 'refresh']"
              :disabled-column-keys="['tableName']" @change="handleChange">
              <template #toolbar-left>
                <el-popconfirm content="是否确定同步最新数据表结构？同步后只要不点击确定保存，则不影响原有配置数据。" type="warning"
                  @ok="handleRefresh(form.tableName)">
                  <template #reference>
                    <el-tooltip content="同步最新数据表结构">

                      <el-button type="primary" :disabled="dataList.length !== 0 && dataList[0].createTime == null">
                        同步
                      </el-button>

                    </el-tooltip>
                  </template>
                </el-popconfirm>
              </template>
              <template #fieldName="{ row }">
                <el-input v-model="row.fieldName" />
              </template>
              <template #fieldType="{ row }">
                <el-select v-model="row.fieldType" placeholder="请选择字段类型" :error="!row.fieldType">
                  <el-option value="String">String</el-option>
                  <el-option value="Integer">Integer</el-option>
                  <el-option value="Long">Long</el-option>
                  <el-option value="Float">Float</el-option>
                  <el-option value="Double">Double</el-option>
                  <el-option value="Boolean">Boolean</el-option>
                  <el-option value="BigDecimal">BigDecimal</el-option>
                  <el-option value="LocalDate">LocalDate</el-option>
                  <el-option value="LocalTime">LocalTime</el-option>
                  <el-option value="LocalDateTime">LocalDateTime</el-option>
                </el-select>
              </template>
              <template #comment="{ row }">
                <el-input v-model="row.comment" />
              </template>
              <template #showInList="{ row }">
                <el-checkbox v-model="row.showInList" value="true" />
              </template>
              <template #showInForm="{ row }">
                <el-checkbox v-model="row.showInForm" value="true" />
              </template>
              <template #isRequired="{ row }">
                <el-checkbox v-if="row.showInForm" v-model="row.isRequired" value="true" />
                <el-checkbox v-else disabled />
              </template>
              <template #showInQuery="{ row }">
                <el-checkbox v-model="row.showInQuery" value="true" />
              </template>
              <template #formType="{ row }">
                <el-select v-if="row.showInForm || row.showInQuery" v-model="row.formType" :options="form_type_enum"
                  :default-value="1" placeholder="请选择表单类型" />
                <span v-else>无需设置</span>
              </template>
              <template #queryType="{ row }">

                <el-select v-if="row.showInQuery" v-model="row.queryType" :options="query_type_enum" :default-value="1"
                  placeholder="请选择查询方式" />
                <span v-else>无需设置</span>
              </template>
              <template #dictCode="{ row }">
                <el-select v-model="row.dictCode" :options="dictList" placeholder="请选择字典类型" clearable
                  placement="bottom-start">
                </el-select>
              </template>
            </GiTable>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelClick">cancel</el-button>
        <el-button type="primary" @click="save">confirm</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style lang="scss" scoped>
:deep(.el-drawer__header) {
  margin-bottom: 10px !important;
}




.custom-header {
  font-size: 18px;
  font-weight: 500;

}
</style>