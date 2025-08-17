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
    <a-divider />

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
          <a-popconfirm title="确定要删除这个应用吗？" ok-text="Yes" cancel-text="No" :ok-button-props="{ type: 'default' }"
            @confirm="doDelete(record.userId)">
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

#userManagePage :deep(.ant-row) {
  margin-top: 7px;
  margin-left: 15px;
  margin-right: 15px;
}

/* 输入框毛玻璃效果 - 只给最外层容器设置背景 */
#userManagePage :deep(.ant-input-affix-wrapper),
#userManagePage :deep(.ant-input) {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  border: none;
  border-radius: 8px !important;
  color: rgba(0, 0, 0, 0.9) !important;
  box-shadow: none !important;
  outline: none !important;
}

#userManagePage :deep(.ant-input-affix-wrapper),
#userManagePage :deep(.ant-select-selector) {
  border: 1px solid rgba(0, 0, 0, 0.5);
}

/* 确保内部input元素完全透明 */
#userManagePage :deep(.ant-input-affix-wrapper input.ant-input) {
  background-color: transparent !important;
  background-image: none !important;
  background: transparent !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

/* 占位符文字 */
#userManagePage :deep(.ant-input::placeholder),
#userManagePage :deep(.ant-select-selection-placeholder) {
  color: rgba(0, 0, 0, 0.6) !important;
}
</style>
