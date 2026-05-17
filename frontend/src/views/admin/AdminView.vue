<template>
  <div class="admin-page">
    <div class="admin-sidebar">
      <el-menu
        :default-active="activeMenu"
        @select="onMenuSelect"
        class="admin-menu"
      >
        <el-menu-item index="users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="posts">
          <el-icon><Document /></el-icon>
          <span>帖子管理</span>
        </el-menu-item>
        <el-menu-item index="comments">
          <el-icon><ChatDotSquare /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
      </el-menu>
    </div>

    <div class="admin-content">
      <!-- Users Table -->
      <div v-if="activeMenu === 'users'" class="admin-table-wrap">
        <h2>用户管理</h2>
        <el-table :data="users" stripe v-loading="usersLoading" style="width:100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="nickname" label="昵称" width="120" />
          <el-table-column prop="educationEmail" label="邮箱" min-width="180" />
          <el-table-column label="角色" width="80">
            <template #default="{ row }"><el-tag :type="row.role==='ADMIN'?'danger':'info'" size="small">{{ row.role }}</el-tag></template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }"><el-tag :type="row.status==='ENABLE'?'success':'warning'" size="small">{{ row.status }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button :type="row.status==='ENABLE'?'warning':'success'" size="small" @click="toggleUser(row)">
                {{ row.status==='ENABLE' ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination
            v-model:current-page="userPage"
            :page-size="userPageSize"
            :total="userTotal"
            layout="prev, pager, next"
            @current-change="loadUsers"
          />
        </div>
      </div>

      <!-- Posts Table -->
      <div v-if="activeMenu === 'posts'" class="admin-table-wrap">
        <h2>帖子管理</h2>
        <el-table :data="posts" stripe v-loading="postsLoading" style="width:100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="userId" label="作者ID" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }"><el-tag :type="row.status==='ENABLE'?'success':'warning'" size="small">{{ row.status }}</el-tag></template>
          </el-table-column>
          <el-table-column label="时间" width="160">
            <template #default="{ row }">{{ row.createTime }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="showPostDetail(row)">详情</el-button>
              <el-button :type="row.status==='ENABLE'?'warning':'success'" size="small" @click="togglePost(row)">
                {{ row.status==='ENABLE' ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination
            v-model:current-page="postPage"
            :page-size="postPageSize"
            :total="postTotal"
            layout="prev, pager, next"
            @current-change="loadPosts"
          />
        </div>
      </div>

      <!-- Comments Table -->
      <div v-if="activeMenu === 'comments'" class="admin-table-wrap">
        <h2>评论管理</h2>
        <el-table :data="comments" stripe v-loading="commentsLoading" style="width:100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
          <el-table-column prop="postId" label="所属帖子" width="100" />
          <el-table-column prop="userId" label="作者ID" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }"><el-tag :type="row.status==='ENABLE'?'success':'warning'" size="small">{{ row.status }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="showCommentDetail(row)">详情</el-button>
              <el-button :type="row.status==='ENABLE'?'warning':'success'" size="small" @click="toggleComment(row)">
                {{ row.status==='ENABLE' ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination
            v-model:current-page="commentPage"
            :page-size="commentPageSize"
            :total="commentTotal"
            layout="prev, pager, next"
            @current-change="loadComments"
          />
        </div>
      </div>
    </div>

    <!-- Post Detail Dialog -->
    <el-dialog v-model="postDialogVisible" title="帖子详情" width="600px">
      <div v-if="postDetailData">
        <p><strong>标题:</strong> {{ postDetailData.title }}</p>
        <p><strong>内容:</strong></p>
        <p style="white-space:pre-wrap;background:#f9f9f9;padding:12px;border-radius:8px">{{ postDetailData.content }}</p>
        <p v-if="postDetailData.imageUrl"><strong>图片:</strong> {{ postDetailData.imageUrl }}</p>
      </div>
    </el-dialog>

    <!-- Comment Detail Dialog -->
    <el-dialog v-model="commentDialogVisible" title="评论详情" width="500px">
      <div v-if="commentDetailData">
        <p><strong>内容:</strong> {{ commentDetailData.content }}</p>
        <p><strong>帖子ID:</strong> {{ commentDetailData.postId }}</p>
        <p><strong>作者ID:</strong> {{ commentDetailData.userId }}</p>
        <p><strong>状态:</strong> {{ commentDetailData.status }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  adminUserPageApi, adminEnableUserApi, adminDisableUserApi,
  adminPostPageApi, adminPostDetailApi, adminEnablePostApi, adminDisablePostApi,
  adminCommentPageApi, adminCommentDetailApi, adminEnableCommentApi, adminDisableCommentApi
} from '../../api/admin'
import { User, Document, ChatDotSquare } from '@element-plus/icons-vue'

const activeMenu = ref('users')

// Users
const users = ref([])
const usersLoading = ref(false)
const userPage = ref(1)
const userPageSize = 10
const userTotal = ref(0)

// Posts
const posts = ref([])
const postsLoading = ref(false)
const postPage = ref(1)
const postPageSize = 10
const postTotal = ref(0)

// Comments
const comments = ref([])
const commentsLoading = ref(false)
const commentPage = ref(1)
const commentPageSize = 10
const commentTotal = ref(0)

// Dialogs
const postDialogVisible = ref(false)
const postDetailData = ref(null)
const commentDialogVisible = ref(false)
const commentDetailData = ref(null)

const onMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'users' && users.value.length === 0) loadUsers()
  if (index === 'posts' && posts.value.length === 0) loadPosts()
  if (index === 'comments' && comments.value.length === 0) loadComments()
}

// Users
const loadUsers = async () => {
  usersLoading.value = true
  try {
    const res = await adminUserPageApi({ pageNum: userPage.value, pageSize: userPageSize })
    const data = res.data || {}
    users.value = data.records || []
    userTotal.value = data.total || 0
  } catch (e) { /* ignore */ }
  finally { usersLoading.value = false }
}

const toggleUser = async (row) => {
  try {
    if (row.status === 'ENABLE') {
      await adminDisableUserApi(row.id)
    } else {
      await adminEnableUserApi(row.id)
    }
    loadUsers()
  } catch (e) { /* ignore */ }
}

// Posts
const loadPosts = async () => {
  postsLoading.value = true
  try {
    const res = await adminPostPageApi({ pageNum: postPage.value, pageSize: postPageSize })
    const data = res.data || {}
    posts.value = data.records || []
    postTotal.value = data.total || 0
  } catch (e) { /* ignore */ }
  finally { postsLoading.value = false }
}

const showPostDetail = async (row) => {
  try {
    const res = await adminPostDetailApi(row.id)
    postDetailData.value = res.data || row
    postDialogVisible.value = true
  } catch (e) { postDetailData.value = row; postDialogVisible.value = true }
}

const togglePost = async (row) => {
  try {
    if (row.status === 'ENABLE') await adminDisablePostApi(row.id)
    else await adminEnablePostApi(row.id)
    loadPosts()
  } catch (e) { /* ignore */ }
}

// Comments
const loadComments = async () => {
  commentsLoading.value = true
  try {
    const res = await adminCommentPageApi({ pageNum: commentPage.value, pageSize: commentPageSize })
    const data = res.data || {}
    comments.value = data.records || []
    commentTotal.value = data.total || 0
  } catch (e) { /* ignore */ }
  finally { commentsLoading.value = false }
}

const showCommentDetail = async (row) => {
  try {
    const res = await adminCommentDetailApi(row.id)
    commentDetailData.value = res.data || row
    commentDialogVisible.value = true
  } catch (e) { commentDetailData.value = row; commentDialogVisible.value = true }
}

const toggleComment = async (row) => {
  try {
    if (row.status === 'ENABLE') await adminDisableCommentApi(row.id)
    else await adminEnableCommentApi(row.id)
    loadComments()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-page {
  display: flex;
  min-height: calc(100vh - 56px);
}
.admin-sidebar {
  width: 220px;
  background: white;
  border-right: 1px solid var(--border);
  padding-top: 16px;
  flex-shrink: 0;
}
.admin-menu { border-right: none; }
.admin-content {
  flex: 1;
  padding: 24px 32px;
  overflow-x: auto;
}
.admin-table-wrap h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
}
.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .admin-sidebar { width: 60px; }
  .admin-sidebar :deep(.el-menu-item span) { display: none; }
  .admin-content { padding: 16px; }
}
</style>
