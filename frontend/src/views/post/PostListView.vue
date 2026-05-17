<template>
  <div class="post-list-page">
    <div class="post-list-container">
      <!-- Tabs -->
      <div class="tabs-bar">
        <el-tabs v-model="activeTab" @tab-change="onTabChange">
          <el-tab-pane label="推荐" name="recommend" />
          <el-tab-pane label="公告" name="notice" />
        </el-tabs>
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
      <div v-else-if="loading && posts.length === 0" class="waterfall">
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
      <div v-else-if="posts.length === 0" class="empty-state">
        <el-empty :description="activeTab==='notice' ? '暂无公告' : '暂无帖子'" />
      </div>

      <!-- Waterfall Grid -->
      <div v-else class="waterfall">
        <div v-for="post in posts" :key="post.id" class="post-card" @click="goDetail(post.id)">
          <div class="card-image">
            <img v-if="post.imageUrl" :src="post.imageUrl" :alt="post.title" @load="onImgLoad" />
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PostListApi } from '../../api/post'
import { Picture, Goods, Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const activeTab = ref('recommend')
const posts = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const errorMsg = ref('')
const hasMore = ref(true)
const pageNum = ref(1)
const pageSize = 9

const loadPosts = async (reset = true) => {
  if (reset) {
    pageNum.value = 1
    posts.value = []
    hasMore.value = true
    loading.value = true
  }
  errorMsg.value = ''
  try {
    const api = activeTab.value === 'notice' ? '/post/notice/page' : '/post/page'
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await PostListApi(api, params)
    const records = res.data?.records || []
    posts.value.push(...records)
    hasMore.value = records.length >= pageSize.value
  } catch (e) {
    if (reset) errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const onTabChange = () => {
  loadPosts(true)
}

const goDetail = (id) => {
  router.push('/post/' + id)
}

// IntersectionObserver for infinite scroll
let observer = null
const sentinelRef = ref(null)

const onImgLoad = () => {
  // Trigger reflow for waterfall
}

const loadMore = () => {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  pageNum.value++
  loadPosts(false)
}

onMounted(() => {
  // Check URL tab param
  if (route.query.tab === 'notice') activeTab.value = 'notice'
  loadPosts(true)

  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) loadMore()
  }, { rootMargin: '200px' })
  // Will be attached to sentinel via DOM update
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
}
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
</style>
