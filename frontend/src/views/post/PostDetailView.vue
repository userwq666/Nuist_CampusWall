<template>
  <div class="detail-page">
    <div class="detail-container" v-if="!loading && !errorMsg">
      <!-- Back -->
      <div class="detail-back" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </div>

      <!-- Post Header -->
      <div class="detail-header">
        <h1 class="detail-title">{{ post.title }}</h1>
        <div class="detail-author">
          <el-avatar :size="36">{{ post.userId }}</el-avatar>
          <div class="author-info">
            <span class="author-name">@{{ post.userId }}</span>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
        </div>
      </div>

      <!-- Post Image -->
      <div v-if="post.imageUrl" class="detail-image">
        <img :src="post.imageUrl" :alt="post.title" />
      </div>

      <!-- Post Content -->
      <div class="detail-content">{{ post.content }}</div>

      <!-- Actions -->
      <div class="detail-actions">
        <button class="action-btn edit-btn" v-if="isOwner" @click="openEditDialog">
          <el-icon><EditPen /></el-icon>
          <span>编辑</span>
        </button>
        <button class="action-btn delete-btn" v-if="isOwner" @click="doDeletePost">
          <el-icon><Delete /></el-icon>
          <span>删除</span>
        </button>
        <button class="action-btn like-btn" :class="{ liked: isLiked }" @click="toggleLike">
          <el-icon><Star /></el-icon>
          <span>点赞 {{ post.likeCount || 0 }}</span>
        </button>
        <div class="action-stat">
          <el-icon><ChatDotSquare /></el-icon>
          <span>评论 {{ comments.length > 0 ? totalComments : 0 }}</span>
        </div>
      </div>

      <!-- Comment Input -->
      <div class="comment-input-area">
        <el-input
          v-model="commentText"
          :placeholder="replyTo ? '回复 @' + replyTo.userId : '说点什么...'"
          type="textarea"
          :rows="2"
          class="comment-input"
        />
        <div class="comment-input-actions">
          <span v-if="replyTo" class="reply-hint">
            回复 @{{ replyTo.userId }}
            <el-icon @click="cancelReply"><Close /></el-icon>
          </span>
          <el-button type="primary" size="small" @click="submitComment" :disabled="!commentText.trim()">发送</el-button>
        </div>
      </div>

      <!-- Comments -->
      <div class="comments-section">
        <h3 class="comments-title">评论 ({{ totalComments }})</h3>
        <div v-if="comments.length === 0" class="no-comments">暂无评论，来说点什么吧</div>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :size="32">{{ comment.userId }}</el-avatar>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-author">@{{ comment.userId }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <span class="comment-action" @click="startReply(comment)">回复</span>
              <span v-if="isMyComment(comment)" class="comment-action delete-action" @click="deleteComment(comment.id)">删除</span>
            </div>
            <!-- Replies -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <span class="reply-author">@{{ reply.userId }}</span>
                <span v-if="reply.replyToUserId" class="reply-to">回复 @{{ reply.replyToUserId }}</span>
                <span class="reply-text">: {{ reply.content }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Load more comments -->
        <div v-if="hasMoreComments" class="load-more-comments" @click="loadMoreComments">
          加载更多评论...
        </div>
      </div>
        <!-- Edit Post Dialog -->
    <el-dialog v-model="showEditDialog" title="编辑帖子" width="600px" :close-on-click-modal="false">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="正文" required>
          <el-input v-model="editForm.content" type="textarea" :rows="6" maxlength="10000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="doUpdatePost" :disabled="!editForm.title.trim() || !editForm.content.trim()">保存</el-button>
      </template>
    </el-dialog>
  </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- Error -->
    <div v-if="errorMsg" class="error-state">
      <el-result icon="error" :title="errorMsg">
        <template #extra>
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PostDetailApi, UpdatePostApi, DeletePostApi } from '../../api/post'
import { commentCreateApi, commentPageApi, commentDeleteApi } from '../../api/comment'
import { doLikeApi, undoLikeApi } from '../../api/like'
import { useAuth } from '../../composables/useAuth'
import { ArrowLeft, Star, ChatDotSquare, Close, EditPen, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const { isLoggedIn, currentUser } = useAuth()
const authStore = useAuthStore()

const post = ref({})
const loading = ref(true)
const errorMsg = ref('')
const isLiked = ref(false)
const isOwner = ref(false)
const showEditDialog = ref(false)
const editForm = reactive({ title: '', content: '' })
const editLoading = ref(false)
const comments = ref([])
const totalComments = ref(0)
const commentText = ref('')
const replyTo = ref(null)
const commentPage = ref(1)
const hasMoreComments = ref(true)
const commentPageSize = 5

const loadPost = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await PostDetailApi(route.params.id)
    post.value = res.data || {}
    isOwner.value = authStore.userInfo && authStore.userInfo.id === post.value.userId
    loadComments(true)
  } catch (e) {
    errorMsg.value = e.message || '帖子加载失败'
  } finally {
    loading.value = false
  }
}

const loadComments = async (reset = false) => {
  if (reset) {
    commentPage.value = 1
    comments.value = []
    hasMoreComments.value = true
  }
  try {
    const res = await commentPageApi({ postId: route.params.id, pageNum: commentPage.value, pageSize: commentPageSize })
    const data = res.data || {}
    totalComments.value = data.total || 0
    const records = data.records || []
    // Group replies under parent comments
    if (reset) {
      comments.value = buildCommentTree(records)
    } else {
      buildCommentTree(records, true)
    }
    hasMoreComments.value = records.length >= commentPageSize
  } catch (e) {
    console.error('Comment load failed:', e)
  }
}

const buildCommentTree = (records, append = false) => {
  const parents = records.filter(c => !c.replyToCommentId)
  parents.forEach(p => {
    p.replies = records.filter(c => c.replyToCommentId === p.id)
  })
  if (append) {
    // Merge with existing comments: add new parents, append replies to existing parents
    parents.forEach(newP => {
      const existing = comments.value.find(c => c.id === newP.id)
      if (existing) {
        if (newP.replies && newP.replies.length > 0) {
          existing.replies = [...(existing.replies || []), ...newP.replies]
        }
      } else {
        comments.value.push(newP)
      }
    })
    return []
  }
  return parents
}

const loadMoreComments = () => {
  commentPage.value++
  loadComments(false)
}

const toggleLike = async () => {
  if (!isLoggedIn.value) { router.push('/login'); return }
  try {
    if (isLiked.value) {
      await undoLikeApi({ targetId: post.value.id, targetType: 'POST' })
      isLiked.value = false
      post.value.likeCount = Math.max(0, (post.value.likeCount || 1) - 1)
    } else {
      await doLikeApi({ targetId: post.value.id, targetType: 'POST' })
      isLiked.value = true
      post.value.likeCount = (post.value.likeCount || 0) + 1
    }
  } catch (e) { ElMessage.warning(e.message || '操作失败') }
}

const submitComment = async () => {
  if (!isLoggedIn.value) { router.push('/login'); return }
  const text = commentText.value.trim()
  if (!text) return
  try {
    const payload = { postId: Number(route.params.id), content: text }
    if (replyTo.value) {
      payload.replyToCommentId = replyTo.value.id
      payload.replyToUserId = replyTo.value.userId
    }
    await commentCreateApi(payload)
    commentText.value = ''
    replyTo.value = null
    loadComments(true)
  } catch (e) {
    ElMessage.error(e.message || '评论发送失败')
  }
}

const startReply = (comment) => { replyTo.value = comment }
const cancelReply = () => { replyTo.value = null }
const isMyComment = (comment) => authStore.userInfo?.id === comment.userId

const deleteComment = async (id) => {
  try {
    await commentDeleteApi(id)
    loadComments(true)
  } catch (e) { ElMessage.error(e.message || '删除失败') }
}

const doDeletePost = async () => {
  const confirmed = await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).catch(() => false)
    if (!confirmed) return
  try {
    await DeletePostApi(post.value.id)
    router.push('/post')
  } catch (e) { ElMessage.error(e.message || '删除失败') }
}

const openEditDialog = () => {
  editForm.title = post.value.title
  editForm.content = post.value.content
  showEditDialog.value = true
}

const doUpdatePost = async () => {
  if (!editForm.title.trim() || !editForm.content.trim()) return
  editLoading.value = true
  try {
    await UpdatePostApi(post.value.id, { title: editForm.title.trim(), content: editForm.content.trim() })
    showEditDialog.value = false
    loadPost()
  } catch (e) { ElMessage.error(e.message || '更新失败') }
  finally { editLoading.value = false }
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const goBack = () => router.push('/post')

onMounted(loadPost)
watch(() => route.params.id, loadPost)
</script>

<style scoped>
.edit-btn:hover { border-color: #409EFF; color: #409EFF; background: #ECF5FF; }
.delete-btn:hover { border-color: #E74C3C; color: #E74C3C; background: #FEF0F0; }

.detail-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 20px;
}
.detail-container {
  background: white;
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
}

.detail-back {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 20px;
  transition: color 0.2s;
}
.detail-back:hover { color: var(--primary); }

.detail-header { margin-bottom: 20px; }
.detail-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 16px;
}
.detail-author {
  display: flex;
  align-items: center;
  gap: 12px;
}
.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.author-name { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.post-time { font-size: 12px; color: var(--text-tertiary); }

.detail-image {
  margin-bottom: 20px;
  border-radius: var(--radius-md);
  overflow: hidden;
}
.detail-image img { width: 100%; display: block; }

.detail-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-primary);
  white-space: pre-wrap;
  margin-bottom: 24px;
}

.detail-actions {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px 0;
  border-top: 1px solid var(--border);
  border-bottom: 1px solid var(--border);
  margin-bottom: 24px;
}
.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.2s;
}
.action-btn:hover { border-color: var(--primary); color: var(--primary); }
.action-btn.liked { background: var(--primary-light); border-color: var(--primary); color: var(--primary); }
.action-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* Comment Input */
.comment-input-area { margin-bottom: 24px; }
.comment-input { --el-input-border-radius: 12px; }
.comment-input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.reply-hint {
  font-size: 13px;
  color: var(--primary);
  display: flex;
  align-items: center;
  gap: 4px;
}
.reply-hint .el-icon { cursor: pointer; }

/* Comments */
.comments-section { }
.comments-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}
.no-comments {
  text-align: center;
  padding: 32px;
  color: var(--text-tertiary);
  font-size: 14px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.comment-body { flex: 1; }
.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.comment-author { font-size: 13px; font-weight: 500; color: var(--text-primary); }
.comment-time { font-size: 11px; color: var(--text-tertiary); }
.comment-content { font-size: 14px; line-height: 1.6; margin-bottom: 6px; }
.comment-actions { display: flex; gap: 12px; }
.comment-action {
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
}
.comment-action:hover { color: var(--primary); }
.delete-action:hover { color: #E74C3C; }

.replies {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f9f9f9;
  border-radius: 8px;
}
.reply-item {
  font-size: 13px;
  line-height: 1.6;
  padding: 4px 0;
}
.reply-author { font-weight: 500; color: var(--primary); }
.reply-to { color: var(--text-secondary); }
.reply-text { color: var(--text-primary); }

.load-more-comments {
  text-align: center;
  padding: 12px;
  font-size: 14px;
  color: var(--primary);
  cursor: pointer;
}
.load-more-comments:hover { opacity: 0.8; }

.loading-state { padding: 40px; }
.error-state { padding: 60px 0; text-align: center; }

@media (max-width: 640px) {
  .detail-container { padding: 20px; }
  .detail-title { font-size: 20px; }
}
</style>
