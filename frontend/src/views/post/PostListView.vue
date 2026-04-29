<template>
  <div>
    <h2>帖子主页</h2>
    <button @click="loadPosts" :disabled="loading">
      {{ loading ? '加载中...' : '刷新' }}
    </button>

    <p v-if="errorMsg" style="color: red">{{ errorMsg }}</p>

    <ul>
      <li v-for="item in list" :key="item.id">
        {{ item.title }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import {PostListApi} from '../../api/post'

const loading = ref(false)
const errorMsg = ref('')
const list = ref([])

const loadPosts = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await PostListApi({ pageNum: 1, pageSize: 5 })
    list.value = res.data.records || []
  } catch (e) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(loadPosts)
</script>
