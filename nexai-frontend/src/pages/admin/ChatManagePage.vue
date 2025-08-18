<template>
  <div id="chatManagePage">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" @finish="doSearch">
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="ID">
            <a-input v-model:value="searchParams.id" placeholder="输入ID" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="消息内容">
            <a-input v-model:value="searchParams.message" placeholder="输入消息内容" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="消息类型">
            <a-select v-model:value="searchParams.messageType" placeholder="选择消息类型" allowClear>
              <a-select-option value="user">用户消息</a-select-option>
              <a-select-option value="ai">AI消息</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="应用ID">
            <a-input v-model:value="searchParams.appId" placeholder="输入应用ID" allowClear />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="用户ID">
            <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="排序字段">
            <a-select v-model:value="searchParams.sortField" placeholder="请选择排序字段" allowClear>
              <a-select-option value="id">ID</a-select-option>
              <a-select-option value="message">消息内容</a-select-option>
              <a-select-option value="messageType">消息类型</a-select-option>
              <a-select-option value="appId">应用ID</a-select-option>
              <a-select-option value="userId">用户ID</a-select-option>
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
              <a-button type="primary" @click="resetSearch" shape="circle" :icon="h(RedoOutlined)"
                :class="'btn-light-green'" ghost />
              <a-button type="primary" html-type="submit" shape="circle" :icon="h(SearchOutlined)" ghost />
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-divider />

    <!-- 表格 -->
    <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange"
      :scroll="{ x: 1400 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'message'">
          <a-tooltip :title="record.message">
            <div class="message-text">{{ record.message }}</div>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex === 'messageType'">
          <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'">
            {{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ formatTime(record.updateTime) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="primary" size="small" ghost @click="viewAppChat(record.appId)">
              查看对话
            </a-button>
            <a-popconfirm title="确定要删除这条消息吗？" ok-text="Yes" cancel-text="No" :ok-button-props="{ type: 'default' }"
              @confirm="deleteMessage(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAppChatHistoryByAdmin, deleteChatHistoryByAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'
import { SearchOutlined, RedoOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 300,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 80,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 80,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 130,
  },
  {
    title: '时间更新',
    dataIndex: 'updateTime',
    width: 130,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.ChatHistory[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ChatHistoryQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 定义默认值
const defaultSearchParams: API.ChatHistoryQueryRequest = {
  pageNum: 1,
  pageSize: 10,
}

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppChatHistoryByAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.list ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

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

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 查看应用对话
const viewAppChat = (appId: number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 删除消息
const deleteMessage = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteChatHistoryByAdmin({ id })
    if (res.data.statusCode === 200) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}

const resetSearch = () => {
  for (const key in searchParams) {
    delete (searchParams as any)[key]
  }
  Object.assign(searchParams, defaultSearchParams)
  doSearch()
}

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

#chatManagePage {
  padding: 24px;
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-top: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

#chatManagePage :deep(.ant-row) {
  margin-top: 7px;
  margin-left: 15px;
  margin-right: 15px;
}

/* 输入框毛玻璃效果 - 只给最外层容器设置背景 */
#chatManagePage :deep(.ant-input-affix-wrapper),
#chatManagePage :deep(.ant-input) {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  border: none;
  border-radius: 8px !important;
  color: rgba(0, 0, 0, 0.9) !important;
  box-shadow: none !important;
  outline: none !important;
}

#chatManagePage :deep(.ant-input-affix-wrapper),
#chatManagePage :deep(.ant-select-selector) {
  border: 1px solid rgba(0, 0, 0, 0.5);
}

/* 确保内部input元素完全透明 */
#chatManagePage :deep(.ant-input-affix-wrapper input.ant-input) {
  background-color: transparent !important;
  background-image: none !important;
  background: transparent !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

/* 占位符文字 */
#chatManagePage :deep(.ant-input::placeholder),
#chatManagePage :deep(.ant-select-selection-placeholder) {
  color: rgba(0, 0, 0, 0.6) !important;
}

.message-text {
  max-width: 420px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>
