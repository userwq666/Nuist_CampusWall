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
          <el-avatar :size="36">{{ post.username?.charAt(0) }}</el-avatar>
          <div class="author-info">
            <span class="author-name">@{{ post.username }}</span>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
        </div>
      </div>

      <!-- Post Image -->
      <div v-if="post.imageUrl" class="detail-image">
        <img :src="post.imageUrl" :alt="post.title" @error="onImageError" />
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
          :placeholder="replyTo ? '回复 @' + replyTo.username : '说点什么...'"
          type="textarea"
          :rows="2"
          class="comment-input"
        />
        <div class="comment-input-area-upload">
          <el-upload
            :before-upload="handleCommentBeforeUpload"
            :auto-upload="false"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.webp"
            list-type="picture-card"
            v-model:file-list="commentFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <div class="comment-input-actions">
          <span v-if="replyTo" class="reply-hint">
            回复 @{{ replyTo.username }}
            <el-icon @click="cancelReply"><Close /></el-icon>
          </span>
          <el-button type="primary" size="small" @click="submitComment" :disabled="!commentText.trim()">发送</el-button>
        </div>
      </div>

      <!-- Comments -->
      <div class="comments-section">
        <h3 class="comments-title">评论 ({{ totalComments }})</h3>
        <div v-if="comments.length === 0" class="no-comments">暂无评论，来说点什么吧</div>
        <div v-for="(comment, idx) in comments" :key="comment.id" class="comment-item">
          <el-avatar :size="32">{{ comment.username?.charAt(0) }}</el-avatar>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-author">{{ comment.username }}</span>
              <template v-if="comment.replyToUserId">
                <span class="reply-tag">回复</span>
                <span class="reply-author">@{{ comment.replyToUsername }}(#{{ comment.replyToFloor }})</span>
              </template>
              <template v-else>
                <span class="reply-author">@{{ comment.postAuthorUsername }}</span>
              </template>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              <span class="comment-floor">#{{ idx + 1 }}:</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div v-if="comment.imageUrl" class="comment-image">
              <img :src="comment.imageUrl" @error="onImageError" />
            </div>
            <div class="comment-actions">
              <span class="comment-action" @click="startReply(comment)">回复</span>
              <span v-if="isMyComment(comment)" class="comment-action delete-action" @click="deleteComment(comment.id)">删除</span>
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
      <el-skeleton :rows="8" animated />
    </div>

    <!-- Error -->
    <div v-if="errorMsg" class="error-state">
      <el-result icon="error" :title="errorMsg">
        <template #extra>
          <el-button type="primary" @click="loadPost">重试</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, EditPen, Delete, Star, ChatDotSquare, Close, Plus } from '@element-plus/icons-vue'
import { PostDetailApi, UpdatePostApi, DeletePostApi } from '@/api/post'
import { commentPageApi, commentCreateApi, commentDeleteApi } from '@/api/comment'
import { uploadFileApi } from '@/api/file'
import { likePostApi, unlikePostApi, checkLikeApi } from '@/api/like'

const route = useRoute()
const router = useRouter()

const post = ref({})
const comments = ref([])
const totalComments = ref(0)
const loading = ref(false)
const errorMsg = ref('')
const commentText = ref('')
const commentFileList = ref([])
const replyTo = ref(null)
const hasMoreComments = ref(false)
const commentPageNum = ref(1)
const showEditDialog = ref(false)
const editLoading = ref(false)
const editForm = ref({ title: '', content: '' })
const isLiked = ref(false)

const isOwner = computed(() => {
  const uid = localStorage.getItem('userId')
  const postUserId = post.value.userId ? String(post.value.userId) : ''
  return uid && uid === postUserId
})

const isMyComment = (comment) => {
  const uid = localStorage.getItem('userId')
  const commentUserId = comment.userId ? String(comment.userId) : ''
  return uid && uid === commentUserId
}

const loadPost = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await PostDetailApi(id)
    post.value = res.data || {}
    await loadComments(true)
    try {
      const likeRes = await checkLikeApi({ targetType: 1, targetId: Number(id) })
      isLiked.value = likeRes.data || false
    } catch (e) { /* ignore */ }
  } catch (e) {
    errorMsg.value = e.message || '帖子加载失败'
  } finally {
    loading.value = false
  }
}

const loadComments = async (reset = true) => {
  if (reset) {
    commentPageNum.value = 1
    comments.value = []
  }
  try {
    const res = await commentPageApi({
      postId: post.value.id,
      pageNum: commentPageNum.value,
      pageSize: 10
    })
    const page = res.data
    if (page && page.records) {
      comments.value = reset ? page.records : [...comments.value, ...page.records]
      totalComments.value = page.total || 0
      hasMoreComments.value = page.records.length >= 10
    }
  } catch (e) { /* ignore */ }
}

const loadMoreComments = () => {
  commentPageNum.value++
  loadComments(false)
}

const submitComment = async () => {
  if (!commentText.value.trim()) return
  try {
    let fileId = null
    const uploadFile = commentFileList.value[0]?.raw
    if (uploadFile) {
      const res = await uploadFileApi(uploadFile, 'COMMENT')
      fileId = res.data
      if (!fileId) { ElMessage.warning('上传成功但未获取到文件 ID'); return }
    }
    const dto = {
      postId: post.value.id,
      content: commentText.value.trim(),
    }
    if (fileId) dto.fileId = fileId
    if (replyTo.value) {
      dto.replyToCommentId = replyTo.value.id
      dto.replyToUserId = Number(replyTo.value.userId)
    }
    await commentCreateApi(dto)
    commentText.value = ''
    commentFileList.value = []    replyTo.value = null
    loadComments(true)
  } catch (e) {
    ElMessage.error(e.message || '评论失败')
  }
}

const handleCommentBeforeUpload = (file) => {
  const isImage = file.type && file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}

const startReply = (comment) => {
  replyTo.value = { id: comment.id, userId: comment.userId, username: comment.username }
  commentText.value = ''
}

const cancelReply = () => {
  replyTo.value = null
}

const deleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
    await commentDeleteApi(id)
    ElMessage.success('删除成功')
    loadComments(true)
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const toggleLike = async () => {
  try {
    if (isLiked.value) {
      await unlikePostApi({ targetType: 1, targetId: post.value.id })
      isLiked.value = false
      post.value.likeCount = Math.max(0, (post.value.likeCount || 1) - 1)
      ElMessage.success('已取消点赞')
    } else {
      await likePostApi({ targetType: 1, targetId: post.value.id })
      isLiked.value = true
      post.value.likeCount = (post.value.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    }
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const onImageError = () => {
  // 图片加载失败静默处理
}

const doDeletePost = async () => {
  try {
    await ElMessageBox.confirm('确定删除这篇帖子吗？删除后不可恢复。', '提示', { type: 'warning' })
    await DeletePostApi(post.value.id)
    ElMessage.success('删除成功')
    router.push('/post')
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const openEditDialog = () => {
  editForm.value = { title: post.value.title || '', content: post.value.content || '' }
  showEditDialog.value = true
}

const doUpdatePost = async () => {
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
.comment-input-area-upload { margin-bottom: 8px; }
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
  gap: 6px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}
.comment-author { font-size: 13px; font-weight: 500; color: var(--text-primary); }
.comment-floor { font-size: 11px; color: var(--text-tertiary); font-weight: 400; }
.comment-time { font-size: 11px; color: var(--text-tertiary); }
.comment-content { font-size: 14px; line-height: 1.6; margin-bottom: 6px; }
.comment-image { margin-bottom: 6px; border-radius: 8px; overflow: hidden; max-width: 240px; }
.comment-image img { width: 100%; display: block; }
.reply-tag { font-size: 12px; color: var(--text-tertiary); }
.comment-header .reply-author { font-size: 12px; font-weight: 500; color: var(--primary); }
.comment-actions { display: flex; gap: 12px; }
.comment-action {
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
}
.comment-action:hover { color: var(--primary); }
.delete-action:hover { color: #E74C3C; }

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