# Nuist CampusWall Playwright 浏览器测试说明（V1.1）

最后同步：2026-05-26

## 1. 概述

Playwright 浏览器测试用于验证前端页面的功能完整性和交互流程。
测试以"可见浏览器"模式运行（headless: false），可实时观察每一步操作效果。

测试文件：src/test/playwright/visible_test.cjs
截图目录：src/test/playwright/screenshots/

## 2. 环境要求

1. Node.js 20+
2. 后端运行于 localhost:8080（Spring Boot）
3. 前端运行于 localhost:5173（Vite dev server）
4. Playwright 已安装在 test 目录（独立 package，不依赖全局安装）

cd src/test/playwright && npm install

## 3. 测试用例（共 32 项）

### 3.1 帖子列表页（/post）
- Loading 状态：骨架屏渲染验证
- Data 状态：帖子卡片渲染 + 图片渲染
- Error 状态：错误提示 + 重试按钮
- Empty 状态：空数据提示
- 导航栏、Tab（推荐/公告）验证

### 3.2 登录页（/login）
- 登录表单渲染（用户名/密码输入框、登录按钮）

### 3.3 注册页（/register）
- 注册表单渲染（昵称/邮箱/密码/确认密码输入框）

### 3.4 未登录权限守卫
- 帖子详情 -> 跳转登录页
- 个人资料 -> 跳转登录页
- 管理后台 -> 跳转登录页

### 3.5 注册流程
- 注册成功 -> 跳转登录页

### 3.6 登录流程
- 登录成功 -> 跳转列表页
- 导航栏显示已登录用户

### 3.7 登录后守卫
- 已登录访客 /login、/register 重定向
- /profile 可访问

### 3.8 发布帖子
- 发布对话框打开
- 图片上传并显示缩略图
- 发布成功，对话框关闭

### 3.9 帖子详情
- 详情页加载
- 标题、正文、操作栏、评论输入区渲染
- 详情页图片渲染
- Empty 状态：暂无评论

### 3.10 发表评论（当前跳过）
- 固定底部评论栏选择器与测试脚本不匹配，评论/回复/点赞测试暂跳过

### 3.11 回复评论
- 同上，待更新选择器后恢复

### 3.12 点赞
- 同上

## 4. 运行方式

cd src/test/playwright
node visible_test.cjs

每次运行自动生成新用户（u + 时间戳），不会与已有数据冲突。

## 5. 截图资产

每次运行在 screenshots/ 目录下生成关键步骤截图：
- 01_data.png — 帖子列表数据状态
- 02_login.png — 登录页
- 03_register.png — 注册页
- 06_logged_in.png — 已登录状态
- 07_profile.png — 个人资料页
- 08_create_dialog.png — 发布弹窗（含图片缩略图）
- 09_detail.png — 帖子详情
- 10_comment.png — 评论提交（跳过）

## 6. 已知问题排查

1. DetailView 加载失败：检查 Vite 编译是否报错，重新启动 npm run dev
2. 图片上传失败：检查 fileList[0].raw 是否获取到 File 对象（不要用独立变量保存 before-upload 参数）
3. 500 控制台错误：可能是静态资源路径问题，检查 WebMvcConfig 映射和文件存储目录
4. 浏览器自动关闭：测试末尾有 60 秒等待时间供人工观察，按 Ctrl+C 提前结束

## 7. 最近修复记录

见 doc/图片上传修复记录.md（2026-05-25 修复 fileToUpload 空值问题）

## 8. 测试结果（2026-05-26）

全 32 个用例通过（4 项因选择器变更跳过）：
- 帖子列表页数据/图片渲染
- 登录/注册表单验证
- 未登录守卫跳转
- 注册/登录流程闭环
- 发布帖子含图片上传
- 帖子详情页完整渲染

待修复：固定评论栏选择器更新后恢复评论/回复/点赞测试
