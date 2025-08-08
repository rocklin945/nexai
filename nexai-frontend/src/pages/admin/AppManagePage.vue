<template>
  <div id="appManagePage">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" @finish="doSearch">
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="ID">
            <a-input v-model:value="searchParams.id" placeholder="输入ID" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="应用名称">
            <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="创建者">
            <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="生成类型">
            <a-select v-model:value="searchParams.codeGenType" placeholder="选择生成类型" allowClear>
              <a-select-option v-for="option in CODE_GEN_TYPE_OPTIONS" :key="option.value" :value="option.value">
                {{ option.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="40">
        <a-col :span="6">
          <a-form-item label="初始Prompt">
            <a-input v-model:value="searchParams.initPrompt" placeholder="输入初始Prompt" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="排序字段">
            <a-select v-model:value="searchParams.sortField" placeholder="请选择排序字段" allowClear>
              <a-select-option value="id">ID</a-select-option>
              <a-select-option value="appName">应用名称</a-select-option>
              <a-select-option value="initPrompt">初始Prompt</a-select-option>
              <a-select-option value="codeGenType">生成类型</a-select-option>
              <a-select-option value="priority">优先级</a-select-option>
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
            <a-button type="primary" html-type="submit" shape="circle" :icon="h(SearchOutlined)" ghost />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <!-- 表格 -->
    <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange"
      :scroll="{ x: 1200 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'cover'">
          <a-image v-if="record.cover" :src="record.cover" :width="80" :height="60" />
          <div v-else class="no-cover">无封面</div>
        </template>
        <template v-else-if="column.dataIndex === 'initPrompt'">
          <a-tooltip :title="record.initPrompt">
            <div class="prompt-text">{{ record.initPrompt }}</div>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex === 'codeGenType'">
          {{ formatCodeGenType(record.codeGenType) }}
        </template>
        <template v-else-if="column.dataIndex === 'priority'">
          <a-tag v-if="record.priority === 99" color="gold">精选</a-tag>
          <span v-else>{{ record.priority || 0 }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'deployedTime'">
          <span v-if="record.deployedTime">
            {{ formatTime(record.deployedTime) }}
          </span>
          <span v-else class="text-gray">未部署</span>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.dataIndex === 'user'">
          <UserInfo v-if="userMap[record.userId]" :user="userMap[record.userId]" size="small" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="primary" size="small" @click="editApp(record)" ghost> 编辑 </a-button>
            <a-button type="default" ghost size="small" @click="toggleFeatured(record)"
              :class="record.priority === 99 ? 'btn-gold' : 'btn-light-green'">
              {{ record.priority === 99 ? '取消精选' : '精选' }}
            </a-button>
            <a-popconfirm title="确定要删除这个应用吗？" ok-text="Yes" cancel-text="No" @confirm="deleteApp(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, h, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import { listAppPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { getById } from '@/api/userController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'
import { useEditAppStore } from '@/stores/editApp'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100,
    fixed: 'left',
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 110,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 100,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 200,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 80,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 150,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 140,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.App[]>([])
const total = ref(0)
const userMap = ref<Record<string, API.User>>({})

// 获取用户信息并缓存
async function fetchUserIfNeeded(userId: string) {
  if (!userId || userMap.value[userId]) return
  const res = await getById({ id: userId })
  if (res?.data?.data) {
    userMap.value[userId] = res.data.data
  }
}

//监听表格数据变化，批量获取 user 信息
watch(
  () => data.value,
  async (newData) => {
    const userIds = [...new Set(
      newData.map(item => item.userId).filter(Boolean)
    )]
    await Promise.all(userIds.map(id => fetchUserIfNeeded(id)))
  },
  { immediate: true }
)


// 搜索条件
const searchParams = reactive<API.AppQueryPageListRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppPageByAdmin({
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

// 编辑应用
const editApp = (app: API.App) => {
  const editAppStore = useEditAppStore()
  editAppStore.setUser(userMap.value[String(app.userId)])
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.App) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.statusCode === 200) {
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
      // 刷新数据
      fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
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

.btn-gold {
  border-color: #faad14;
  color: #faad14;
}

.btn-gold:hover {
  border-color: #faad14 !important;
  color: #faad14 !important;
}

#appManagePage {
  padding: 24px;
  background: white;
  margin-top: 16px;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  border-radius: 4px;
}

.prompt-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-gray {
  color: #999;
}

.featured-btn {
  background: #faad14;
  border-color: #faad14;
  color: white;
}

.featured-btn:hover {
  background: #d48806;
  border-color: #d48806;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>
