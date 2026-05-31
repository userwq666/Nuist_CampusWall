# 图片上传修复记录

## 问题描述

Playwright 浏览器测试中，发帖时图片上传失败：
- handleBeforeUpload 正确接收到 File 对象（缩略图正常显示）
- 但 doCreatePost 中 fileToUpload 始终为 null，导致 uploadFileApi 从未被调用
- 最终帖子没有 imageUrl，图片不显示

## 根因分析

发帖页 PostListView.vue 中，handleBeforeUpload 用独立变量 fileToUpload = file 保存文件引用并返回 true。
但 Element Plus 的 el-upload 组件在 before-upload 返回 true 后会对 File 对象进行内部处理，导致保存的引用失效。

Element Plus 的标准做法是从 fileList[0].raw 获取原始 File 对象，
el-upload 收到文件后会自动放入 v-model:file-list 绑定的数组，且每个元素保留 .raw 属性指向原始 File。

## 修改详情

### 1. frontend/src/views/post/PostListView.vue

改动：
- 删除 let fileToUpload = null 变量
- handleBeforeUpload 只做类型/大小校验，不再保存文件
- doCreatePost 中改从 createForm.fileList[0]?.raw 获取文件
- onCreateClosed 中删除 fileToUpload = null

### 2. frontend/src/views/post/PostDetailView.vue

改动（同步修复评论图片上传）：
- 删除 const commentFileToUpload = ref(null) 变量
- handleCommentBeforeUpload 改为返回 true（之前返回 false 导致缩略图不显示）
- submitComment 中改从 commentFileList.value[0]?.raw 获取文件
- 修复正则替换导致的行合并语法错误

## 测试结果

测试文件：src/test/playwright/visible_test.cjs

全 36 个用例通过：
- 图片上传并显示缩略图
- 发布成功，对话框关闭
- 详情页图片渲染
- 评论提交 / 回复评论 / 点赞切换

## 经验教训

1. 使用 Element Plus 的 el-upload 时，应通过 fileList[0].raw 获取原始文件，不要用独立变量保存 before-upload 的参数
2. handleCommentBeforeUpload（评论上传）原先返回 false 导致缩略图消失，改为 true 后通过 fileList 管理显示
3. 正则替换多行代码时要小心换行符和分号问题，避免语法错误
