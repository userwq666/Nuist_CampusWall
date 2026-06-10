<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <button class="profile-back" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h2 class="profile-title">个人资料</h2>
      </div>

      <div v-if="loading" class="state-box">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="errorMsg" class="state-box error-box">
        <el-result icon="error" :title="errorMsg">
          <template #extra>
            <el-button type="primary" @click="loadProfile">重试</el-button>
          </template>
        </el-result>
      </div>

      <template v-else>
        <div class="avatar-card">
          <div class="avatar-wrap">
            <el-avatar :size="88" :src="previewUrl || form.imageUrl || undefined" class="profile-avatar">
              {{ form.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <input
              type="file"
              accept=".jpg,.jpeg,.png,.gif,.webp"
              class="avatar-input"
              @change="onAvatarFileChange"
            />
            <button class="avatar-overlay" @click="$event.target.previousElementSibling.click()">
              <el-icon><Camera /></el-icon>
              <span>更换</span>
            </button>
          </div>
          <div class="avatar-info">
            <div class="avatar-nickname">{{ form.nickname || '未设置昵称' }}</div>
            <div class="avatar-username">@{{ form.username }}</div>
          </div>
        </div>

        <el-form :model="form" label-position="top" class="profile-form">
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" maxlength="50" show-word-limit placeholder="设置你的昵称" />
          </el-form-item>

          <el-form-item label="教育邮箱">
            <el-input v-model="form.educationEmail" placeholder="xxx@xxx.edu" />
          </el-form-item>

          <el-divider />

          <div class="section-label">修改密码（可选）</div>

          <el-form-item label="旧密码">
            <el-input v-model="form.oldPassword" type="password" show-password placeholder="输入旧密码" />
          </el-form-item>

          <el-form-item label="新密码">
            <el-input v-model="form.newPassword" type="password" show-password placeholder="新密码（6-32位）" />
          </el-form-item>

          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="doSave" :disabled="!form.nickname?.trim()" size="large" class="save-btn">保存修改</el-button>
            <el-button @click="goBack" size="large" class="cancel-btn">取消</el-button>
          </div>
        </el-form>
      </template>
    </div>
  </div>

    <!-- Crop Dialog -->
    <el-dialog v-model="showCrop" title="裁切头像" width="420px" :close-on-click-modal="false" @closed="onCropClosed">
      <div class="crop-wrapper">
        <Cropper
          ref="cropperRef"
          :src="cropImg"
          :stencil-component="CircleStencil"
          :stencil-props="{ aspectRatio: 1 }"
          :auto-zoom="true"
          class="crop-cropper"
        />
      </div>
      <template #footer>
        <el-button @click="showCrop = false">取消</el-button>
        <el-button type="primary" @click="confirmCrop">确定</el-button>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfoApi, updateMyInfoApi } from '../../api/account'
import { uploadFileApi } from '../../api/file'
import { useAuthStore } from '../../stores/auth'
import { ArrowLeft, Camera } from '@element-plus/icons-vue'
import { Cropper, CircleStencil } from 'vue-advanced-cropper'
import 'vue-advanced-cropper/dist/style.css'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = reactive({
  username: '',
  nickname: '',
  educationEmail: '',
  imageUrl: '',
  oldPassword: '',
  newPassword: ''
})
const loading = ref(true)
const errorMsg = ref('')
const saving = ref(false)
const avatarFileList = ref([])
const previewUrl = ref('')
// crop
const showCrop = ref(false)
const cropImg = ref('')
const cropperRef = ref(null)

const loadProfile = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getUserInfoApi()
    const data = res.data || {}
    form.username = data.username || ''
    form.nickname = data.nickname || ''
    form.educationEmail = data.educationEmail || ''
    form.imageUrl = data.imageUrl || ''
    form.oldPassword = ''
    form.newPassword = ''
  } catch (e) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const onAvatarFileChange = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.warning('只能上传图片文件'); return }
  if (!isLt5M) { ElMessage.warning('图片大小不能超过 5MB'); return }
  cropImg.value = URL.createObjectURL(file)
  showCrop.value = true
  e.target.value = ''
}
const confirmCrop = () => {
  const { canvas } = cropperRef.value.getResult()
  if (!canvas) return
  canvas.toBlob((blob) => {
    const f = new File([blob], 'avatar.png', { type: 'image/png' })
    avatarFileList.value = [{ raw: f, name: 'avatar.png' }]
    previewUrl.value = URL.createObjectURL(blob)
    showCrop.value = false
    ElMessage.success('头像已裁切')
  }, 'image/png')
}

const onCropClosed = () => {
  if (cropImg.value) URL.revokeObjectURL(cropImg.value)
  cropImg.value = ''
  if (!previewUrl.value) avatarFileList.value = []
}


const doSave = async () => {
  saving.value = true
  try {
    const payload = { nickname: form.nickname.trim() }
    if (form.educationEmail?.trim()) payload.educationEmail = form.educationEmail.trim()
    if (form.oldPassword) payload.oldPassword = form.oldPassword
    if (form.newPassword) payload.newPassword = form.newPassword

    if (avatarFileList.value.length > 0) {
      const f = avatarFileList.value[0]?.raw
      if (f) {
        const res = await uploadFileApi(f, 'AVATAR')
        const fid = res.data
        if (fid) payload.fileID = fid
      }
    }

    await updateMyInfoApi(payload)
    ElMessage.success('保存成功')

    const infoRes = await getUserInfoApi()
    const authStore = useAuthStore()
    authStore.userInfo = infoRes.data
    localStorage.setItem('userInfo', JSON.stringify(infoRes.data))
    router.push('/post')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const goBack = () => router.back()

onMounted(loadProfile)
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 56px);
  display: flex;
  justify-content: center;
  padding: 32px 16px;
  background: var(--bg);
}

.profile-card {
  width: 100%;
  max-width: 1200px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px 0;
}

.profile-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: var(--bg);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.profile-back:hover {
  background: var(--primary-light);
  color: var(--primary);
}

.profile-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

/* Avatar card */
.avatar-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 28px 24px;
  margin: 0 24px 8px;
  margin-top: 20px;
  background: linear-gradient(135deg, #F0F4FA, #E8ECF3);
  border-radius: var(--radius-md);
}

.avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.profile-avatar {
  border: 3px solid white;
  box-shadow: var(--shadow-md);
}

.avatar-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
  font-size: 11px;
  line-height: 1.2;
}
.avatar-overlay:hover { opacity: 1; }
.avatar-overlay .el-icon { font-size: 18px; }

.avatar-info {
  flex: 1;
  min-width: 0;
}

.avatar-nickname {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-username {
  font-size: 14px;
  color: var(--text-secondary);
}

/* Form */
.profile-form {
  padding: 8px 24px 24px;
}

.profile-form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  padding-bottom: 4px;
}

.profile-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--border) inset;
}
.profile-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary) inset;
}
.profile-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary) inset;
}
.profile-form :deep(.el-input--disabled .el-input__wrapper) {
  background: var(--bg);
}

.section-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.save-btn {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  font-size: 15px;
}

.cancel-btn {
  height: 44px;
  border-radius: 12px;
  font-size: 15px;
  min-width: 100px;
}

/* States */
.state-box { padding: 60px 24px; }
.error-box :deep(.el-result__title) { font-size: 16px; }

@media (max-width: 480px) {
  .profile-page { padding: 16px 12px; }
  .avatar-card {
    flex-direction: column;
    text-align: center;
    padding: 24px 16px;
  }
  .profile-form { padding: 8px 16px 20px; }
}
/* Crop Dialog */
.crop-wrapper { width: 100%; height: 320px; }
.crop-cropper { width: 100%; height: 100%; background: #f0f0f0; }

</style>
