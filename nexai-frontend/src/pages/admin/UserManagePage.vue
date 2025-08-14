<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" @finish="doSearch">
      <!-- 第一行 -->
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="ID">
            <a-input v-model:value="searchParams.id" placeholder="输入ID" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="账号">
            <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="用户名">
            <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="角色">
            <a-select v-model:value="searchParams.userRole" placeholder="请选择角色" allowClear>
              <a-select-option :value="0">管理员</a-select-option>
              <a-select-option :value="1">普通用户</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 第二行 -->
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="简介">
            <a-input v-model:value="searchParams.userProfile" placeholder="输入简介" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="排序字段">
            <a-select v-model:value="searchParams.sortField" placeholder="请选择排序字段" allowClear>
              <a-select-option value="userId">ID</a-select-option>
              <a-select-option value="userName">用户名</a-select-option>
              <a-select-option value="userAccount">账号</a-select-option>
              <a-select-option value="userRole">角色</a-select-option>
              <a-select-option value="createTime">创建时间</a-select-option>
              <a-select-option value="updateTime">更新时间</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="排序方式">
            <a-select v-model:value="searchParams.sortOrder" placeholder="请选择排序方式" allowClear>
              <a-select-option value="asc">升序</a-select-option>
              <a-select-option value="desc">降序</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item>
            <a-space>
              <!-- 重置按钮 -->
              <a-button type="primary" @click="resetSearch" shape="circle" :icon="h(RedoOutlined)"
                :class="'btn-light-green'" ghost />
              <!-- 搜索按钮 -->
              <a-button type="primary" html-type="submit" shape="circle" :icon="h(SearchOutlined)" ghost />
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <!-- 表格 -->
    <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 0">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ dayjs(record.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-popconfirm title="确定要删除这个应用吗？" ok-text="Yes" cancel-text="No" @confirm="doDelete(record.userId)">
            <a-button danger size="small">删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref, h } from 'vue'
import { deleteUser, listUserByPage } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { SearchOutlined, RedoOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'userId',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 展示的数据
const data = ref<API.UserLoginVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserPageQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 定义默认值
const defaultSearchParams: API.UserPageQueryRequest = {
  pageNum: 1,
  pageSize: 10,
}

// 获取数据
const fetchData = async () => {
  const res = await listUserByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.list ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格分页变化时的操作
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 重置方法
const resetSearch = () => {
  for (const key in searchParams) {
    delete (searchParams as any)[key]
  }
  Object.assign(searchParams, defaultSearchParams)
  doSearch()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.statusCode === 200) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.btn-light-green {
  border-color: #73d13d;
  color: #73d13d;
}

.btn-light-green:hover {
  border-color: #73d13d !important;
  color: #73d13d !important;
}

#userManagePage {
  padding: 24px;
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-top: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 输入框毛玻璃效果 - 完全无边框版本 */
#userManagePage :deep(.ant-input),
#userManagePage :deep(.ant-input-affix-wrapper),
#userManagePage :deep(.ant-input-group-wrapper),
#userManagePage :deep(.ant-input-wrapper),
#userManagePage :deep(.ant-input-group),
#userManagePage :deep(.ant-input-group-addon),
#userManagePage :deep(.ant-input-suffix),
#userManagePage :deep(.ant-input-prefix) {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  border: none !important;
  border-radius: 8px !important;
  color: rgba(0, 0, 0, 0.9) !important;
  box-shadow: none !important;
  outline: none !important;
}

#userManagePage :deep(.ant-input:hover),
#userManagePage :deep(.ant-input-affix-wrapper:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  border: none !important;
  border-color: transparent !important;
  box-shadow: none !important;
  outline: none !important;
}

#userManagePage :deep(.ant-input:focus),
#userManagePage :deep(.ant-input-affix-wrapper-focused),
#userManagePage :deep(.ant-input-affix-wrapper:focus-within) {
  background: rgba(255, 255, 255, 0.2) !important;
  border: none !important;
  border-color: transparent !important;
  box-shadow: none !important;
  outline: none !important;
}

/* 强制移除所有可能的边框 */
#userManagePage :deep(.ant-input),
#userManagePage :deep(.ant-input-affix-wrapper),
#userManagePage :deep(.ant-input:focus),
#userManagePage :deep(.ant-input-affix-wrapper-focused) {
  border-top: none !important;
  border-right: none !important;
  border-bottom: none !important;
  border-left: none !important;
  border-width: 0 !important;
  border-style: none !important;
  border-color: transparent !important;
}

/* 清除按钮透明化 */
#userManagePage :deep(.ant-input-clear-icon) {
  background: transparent !important;
  color: rgba(0, 0, 0, 0.45) !important;
}

/* 输入框内部所有元素透明化 */
#userManagePage :deep(.ant-input-affix-wrapper > input.ant-input) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

/* 选择器毛玻璃效果 */
#userManagePage :deep(.ant-select-selector) {
  background: rgba(255, 255, 255, 0.2) !important;
  backdrop-filter: blur(15px) saturate(150%) !important;
  -webkit-backdrop-filter: blur(15px) saturate(150%) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  border-radius: 8px !important;
}

#userManagePage :deep(.ant-select-selector:hover) {
  background: rgba(255, 255, 255, 0.25) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
}

#userManagePage :deep(.ant-select-focused .ant-select-selector) {
  background: rgba(255, 255, 255, 0.3) !important;
  border-color: #40E0D0 !important;
  box-shadow: 0 0 0 2px rgba(64, 224, 208, 0.2) !important;
}

/* 占位符文字 */
#userManagePage :deep(.ant-input::placeholder) {
  color: rgba(0, 0, 0, 0.45) !important;
}

#userManagePage :deep(.ant-select-selection-placeholder) {
  color: rgba(0, 0, 0, 0.45) !important;
}

/* 选择器内部文字 */
#userManagePage :deep(.ant-select-selection-item) {
  color: rgba(0, 0, 0, 0.85) !important;
}

/* 表格毛玻璃效果 */
#userManagePage :deep(.ant-table) {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(10px) !important;
  -webkit-backdrop-filter: blur(10px) !important;
  border-radius: 8px !important;
}

#userManagePage :deep(.ant-table-thead > tr > th) {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px) !important;
  -webkit-backdrop-filter: blur(10px) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2) !important;
}

#userManagePage :deep(.ant-table-tbody > tr > td) {
  background: rgba(255, 255, 255, 0.05) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

#userManagePage :deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(255, 255, 255, 0.1) !important;
}
</style>
