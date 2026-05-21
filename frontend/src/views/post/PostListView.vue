<template>
  <div class="post-list-page">
    <div class="post-list-container">
      <!-- Tabs -->
      <div class="tabs-bar">
        <el-tabs v-model="activeTab" @tab-change="onTabChange">
          <el-tab-pane label="推荐" name="recommend" />
          <el-tab-pane label="公告" name="notice" />
          <el-tab-pane label="我的" name="my" />
        </el-tabs>
        <el-button type="primary" class="create-btn" @click="showCreateDialog = true" v-if="isLoggedIn">
          <el-icon><Plus /></el-icon>发布
        </el-button>
      </div>

      <!-- Search indicator -->
      <div v-if="isSearching && searchText" class="search-indicator">
        <el-icon><Search /></el-icon>
        <span>搜索: "{{ searchText }}" 共 {{ filteredPosts.length }} 条结果</span>
        <el-button text size="small" @click="clearSearch">清除</el-button>
      </div>

      <!-- Error -->
      <div v-if="errorMsg" class="error-state">
        <el-result icon="error" :title="errorMsg">
          <template #extra>
            <el-button type="primary" @click="loadPosts">重试</el-button>
          </template>
        </el-result>
      </div>

      <!-- Loading skeleton -->
      <div v-else-if="loading && filteredPosts.length === 0" class="waterfall">
        <div v-for="n in 6" :key="n" class="skeleton-card">
          <el-skeleton animated>
            <template #template>
              <div style="padding: 0">
                <el-skeleton-item variant="image" style="width:100%; height:200px; border-radius:12px 12px 0 0" />
                <div style="padding:12px">
                  <el-skeleton-item variant="p" style="width:80%; height:16px; margin-bottom:8px" />
                  <el-skeleton-item variant="p" style="width:50%; height:12px" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <!-- Empty state -->
      <div v-else-if="filteredPosts.length === 0" class="empty-state">
        <el-empty :description="activeTab==='notice' ? '暂无公告' : '暂无帖子'" />
      </div>

      <!-- Waterfall Grid -->
      <div v-else class="waterfall">
        <div v-for="post in filteredPosts" :key="post.id" class="post-card" @click="goDetail(post.id)">
          <div class="card-image">
            <img v-if="post.imageUrl" :src="post.imageUrl" :alt="post.title" @error="onImageError($event)" />
            <div v-else class="card-image-placeholder">
              <el-icon :size="32" color="#CCC"><Picture /></el-icon>
            </div>
            <span v-if="activeTab==='notice'" class="notice-badge">公告</span>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ post.title }}</h3>
            <div class="card-meta">
              <span class="card-author">@{{ post.userId }}</span>
              <span class="card-likes">
                <el-icon :size="14"><Goods /></el-icon>
                {{ post.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading more -->
      <div v-if="loadingMore" class="loading-more">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <!-- No more -->
      <div v-if="!hasMore && posts.length > 0" class="no-more">—— 已经到底了 ——</div>
      <!-- Sentinel for infinite scroll -->
      <div ref="sentinelRef" class="sentinel"></div>
    </div>

    <!-- Create Post Dialog -->
    <el-dialog v-model="showCreateDialog" title="发布帖子" width="600px" :close-on-click-modal="false" @closed="onCreateClosed">
      <el-form :model="createForm" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="createForm.title" placeholder="起个吸引人的标题..." maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="正文" required>
          <el-input v-model="createForm.content" type="textarea" :rows="6" placeholder="分享你的想法..." maxlength="10000" show-word-limit />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            :before-upload="handleBeforeUpload"
            :auto-upload="false"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.webp"
            list-type="picture-card"
            v-model:file-list="createForm.fileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="doCreatePost" :disabled="!createForm.title.trim() || !createForm.content.trim()">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PostListApi, MyPostListApi, CreatePostApi } from '../../api/post'
import { uploadFileApi } from '../../api/file'
import { Picture, Goods, Loading, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuth } from '../../composables/useAuth'

const route = useRoute()
const router = useRouter()
const { isLoggedIn } = useAuth()
const activeTab = ref('recommend')
const posts = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const errorMsg = ref('')
const hasMore = ref(true)
const searchText = ref('')
const isSearching = ref(false)

// Filtered posts based on search
const filteredPosts = computed(() => {
  if (!isSearching.value || !searchText.value.trim()) return posts.value
  const q = searchText.value.trim().toLowerCase()
  return posts.value.filter(p => p.title && p.title.toLowerCase().includes(q))
})
const pageNum = ref(1)
const pageSize = 9
const showCreateDialog = ref(false)

const loadPosts = async (reset = true) => {
  if (reset) {
    pageNum.value = 1
    posts.value = []
    hasMore.value = true
    loading.value = true
  }
  errorMsg.value = ''
  try {
    let api, apiFn
    if (activeTab.value === 'notice') {
      api = '/post/notice/page'
    } else if (activeTab.value === 'my') {
      apiFn = MyPostListApi
    } else {
      api = '/post/page'
    }
    const params = { pageNum: pageNum.value, pageSize: pageSize }
    let res
    if (apiFn) {
      res = await apiFn(params)
    } else {
      res = await PostListApi(api, params)
    }
    const records = res.data?.records || []
    posts.value.push(...records)
    hasMore.value = records.length >= pageSize
  } catch (e) {
    if (reset) errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const onTabChange = () => {
  if (activeTab.value === 'my' && !isLoggedIn.value) {
    ElMessage.warning('请先登录后查看我的帖子')
    router.push('/login')
    return
  }
  loadPosts(true)
}

const goDetail = (id) => {
  router.push('/post/' + id)
}

// IntersectionObserver for infinite scroll
let observer = null
const sentinelRef = ref(null)

const loadMore = () => {
  if (loadingMore.value || !hasMore.value || errorMsg.value) return
  loadingMore.value = true
  pageNum.value++
  loadPosts(false)
}

// Create post form
const createForm = reactive({ title: '', content: '', fileList: [] })
const createLoading = ref(false)
const fileToUpload = ref(null)

const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
const handleBeforeUpload = (file) => {
  if (!ALLOWED_TYPES.includes(file.type)) { ElMessage.warning('仅支持 JPG/PNG/GIF/WebP 格式'); return false }
  if (file.size / 1024 / 1024 > 5) { ElMessage.warning('图片大小不能超过 5MB'); return false }
  fileToUpload.value = file
  return true
}

const doCreatePost = async () => {
  if (!isLoggedIn.value) { router.push('/login'); return }
  createLoading.value = true
  try {
    let fileId = null
    if (fileToUpload.value) {
      const res = await uploadFileApi(fileToUpload.value, 'POST')
      fileId = res.data
    }
    await CreatePostApi({ title: createForm.title.trim(), content: createForm.content.trim(), fileID: fileId })
    showCreateDialog.value = false
    loadPosts(true)
  } catch (e) {
    ElMessage.error(e.message || '发帖失败')
  } finally { createLoading.value = false }
}

const clearSearch = () => {
  searchText.value = ''
  isSearching.value = false
  router.push({ path: '/post' })
}

const onImageError = (e) => { e.target.style.display = 'none' }

const onCreateClosed = () => {
  createForm.title = ''
  createForm.content = ''
  createForm.fileList = []
  fileToUpload.value = null
}

onMounted(() => {
  // Check URL tab param
  if (route.query.tab === 'notice') activeTab.value = 'notice'
  // Check URL search param
  if (route.query.search) {
    searchText.value = route.query.search
    isSearching.value = true
  }
  loadPosts(true)

  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) loadMore()
  }, { rootMargin: '200px' })
  if (sentinelRef.value) observer.observe(sentinelRef.value)
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.post-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.post-list-container { width: 100%; }
.tabs-bar {
  margin-bottom: 20px;
  background: white;
  border-radius: 12px;
  padding: 0 16px;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tabs-bar .el-tabs { flex: 1; }
.create-btn { height: 36px; border-radius: 20px; font-size: 14px; }
.tabs-bar :deep(.el-tabs__header) { margin: 0; }
.tabs-bar :deep(.el-tabs__item) { font-size: 15px; height: 48px; font-weight: 500; }
.tabs-bar :deep(.el-tabs__item.is-active) { color: var(--primary); }
.tabs-bar :deep(.el-tabs__active-bar) { background: var(--primary); }

.waterfall {
  column-count: 3;
  column-gap: 16px;
}
@media (max-width: 900px) { .waterfall { column-count: 2; } }
@media (max-width: 540px) { .waterfall { column-count: 1; } }

.post-card {
  break-inside: avoid;
  margin-bottom: 16px;
  background: var(--card-bg);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.post-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-image {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: #f5f5f5;
}
.card-image img {
  width: 100%;
  display: block;
  object-fit: cover;
}
.card-image-placeholder {
  width: 100%;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
}
.notice-badge {
  position: absolute;
  top: 8px; left: 8px;
  background: var(--primary);
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.card-body { padding: 12px; }
.card-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
}
.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
}
.card-likes {
  display: flex;
  align-items: center;
  gap: 4px;
}
.card-author { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 100px; }

/* Skeleton */
.skeleton-card {
  break-inside: avoid;
  margin-bottom: 16px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: white;
  box-shadow: var(--shadow-sm);
}

.error-state, .empty-state {
  padding: 60px 0;
  text-align: center;
}

.loading-more {
  text-align: center;
  padding: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.no-more {
  text-align: center;
  padding: 24px;
  color: var(--text-tertiary);
  font-size: 13px;
}
.search-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  margin-bottom: 16px;
  background: var(--primary-light);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--primary);
}
.search-indicator .el-button { margin-left: auto; }

.sentinel { height: 1px; }
</style>
