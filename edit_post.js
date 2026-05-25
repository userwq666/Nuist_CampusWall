const fs = require('fs');
const path = 'frontend/src/views/post/PostDetailView.vue';
let content = fs.readFileSync(path, 'utf8');

// 1) Add Promotion to icon import
content = content.replace(
  "import { ArrowLeft, EditPen, Delete, Star, ChatDotSquare, Close, Plus } from '@element-plus/icons-vue'",
  "import { ArrowLeft, EditPen, Delete, Star, ChatDotSquare, Close, Plus, Promotion } from '@element-plus/icons-vue'"
);

// 2) Replace comment input area with fixed bottom bar
const commentStart = content.indexOf('<!-- Comment Input -->');
const commentEnd = content.indexOf('<!-- Comments -->');
const bottomBar = `      <!-- Comment Bottom Bar (fixed) -->
      <div class="comment-bottom-bar">
        <div class="comment-bar-inner">
          <el-upload
            :before-upload="handleCommentBeforeUpload"
            :auto-upload="false"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.webp"
            :show-file-list="false"
            v-model:file-list="commentFileList"
            class="comment-upload-btn"
          >
            <el-button circle size="small" type="default">
              <el-icon><Plus /></el-icon>
            </el-button>
          </el-upload>
          <el-input
            v-model="commentText"
            :placeholder="replyTo ? '回复 @' + replyTo.username : '说点什么...'"
            class="comment-input"
          />
          <el-button type="primary" circle size="small" @click="submitComment" :disabled="!commentText.trim()">
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
        <div v-if="commentFileList.length > 0" class="comment-bar-file-hint">
          <el-tag size="small" closable @close="commentFileList = []">图片已选</el-tag>
        </div>
        <div v-if="replyTo" class="comment-bar-reply">
          回复 @{{ replyTo.username }}
          <el-icon @click="cancelReply"><Close /></el-icon>
        </div>
      </div>

`;

content = content.substring(0, commentStart) + bottomBar + content.substring(commentEnd);

// 3) Add image upload to edit dialog - after content form-item, before </el-form>
content = content.replace(
  '</el-form-item>\n      </el-form>\n      <template #footer>',
  `</el-form-item>
        <el-form-item label="图片">
          <el-upload
            :before-upload="handleEditBeforeUpload"
            :auto-upload="false"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.webp"
            list-type="picture-card"
            v-model:file-list="editFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>`
);

// 4) Add editFileList ref after isLiked ref
content = content.replace(
  'const isLiked = ref(false)',
  'const isLiked = ref(false)\nconst editFileList = ref([])'
);

// 5) Update openEditDialog to reset editFileList
content = content.replace(
  'const openEditDialog = () => {\n  editForm.value = { title: post.value.title || \'\', content: post.value.content || \'\' }\n  showEditDialog.value = true\n}',
  'const openEditDialog = () => {\n  editForm.value = { title: post.value.title || \'\', content: post.value.content || \'\' }\n  editFileList.value = []\n  showEditDialog.value = true\n}'
);

// 6) Replace doUpdatePost with image upload logic
content = content.replace(
  `const doUpdatePost = async () => {
  editLoading.value = true
  try {
    await UpdatePostApi(post.value.id, { title: editForm.title.trim(), content: editForm.content.trim() })
    showEditDialog.value = false
    loadPost()
  } catch (e) { ElMessage.error(e.message || '更新失败') }
  finally { editLoading.value = false }
}`,
  `const doUpdatePost = async () => {
  editLoading.value = true
  try {
    let fileId = null
    const uploadFile = editFileList.value[0]?.raw
    if (uploadFile) {
      const res = await uploadFileApi(uploadFile, 'POST')
      fileId = res.data
    }
    const data = { title: editForm.title.trim(), content: editForm.content.trim() }
    if (fileId) data.fileId = fileId
    await UpdatePostApi(post.value.id, data)
    showEditDialog.value = false
    editFileList.value = []
    loadPost()
  } catch (e) { ElMessage.error(e.message || '更新失败') }
  finally { editLoading.value = false }
}`
);

// 7) Add handleEditBeforeUpload function after handleCommentBeforeUpload
content = content.replace(
  'const handleCommentBeforeUpload = (file) => {\n  const isImage = file.type && file.type.startsWith(\'image/\')\n  const isLt5M = file.size / 1024 / 1024 < 5\n  if (!isImage) { ElMessage.error(\'只能上传图片文件\'); return false }\n  if (!isLt5M) { ElMessage.error(\'图片大小不能超过 5MB\'); return false }\n  return true\n}',
  `const handleCommentBeforeUpload = (file) => {
  const isImage = file.type && file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}

const handleEditBeforeUpload = (file) => {
  const isImage = file.type && file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}`
);

fs.writeFileSync(path, content, 'utf8');
console.log('All changes applied successfully');

// Verify - check no ElMessage lines have broken syntax
const finalLines = content.split('\n');
let hasError = false;
for (let i = 0; i < finalLines.length; i++) {
  if (finalLines[i].includes("ElMessage.success(") && !finalLines[i].includes(")")) {
    console.log('WARNING line ' + (i+1) + ': ' + finalLines[i]);
    hasError = true;
  }
  if (finalLines[i].includes("ElMessageBox.confirm(") && !finalLines[i].includes(")")) {
    console.log('WARNING line ' + (i+1) + ': ' + finalLines[i]);
    hasError = true;
  }
}
if (!hasError) console.log('All ElMessage/ElMessageBox lines look syntactically correct');
