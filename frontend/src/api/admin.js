import request from './request'

// 管理员鉴权
export function adminPingApi() {
    return request.get('/admin/ping')
}

// 用户管理
export function adminUserPageApi(params) {
    return request.get('/admin/user/page', { params })
}
export function adminEnableUserApi(userId) {
    return request.post('/admin/user/enable/' + userId)
}
export function adminDisableUserApi(userId) {
    return request.post('/admin/user/disable/' + userId)
}

// 帖子管理
export function adminPostPageApi(params) {
    return request.get('/admin/post/page', { params })
}
export function adminPostDetailApi(postId) {
    return request.get('/admin/post/detail/' + postId)
}
export function adminEnablePostApi(postId) {
    return request.post('/admin/post/enable/' + postId)
}
export function adminDisablePostApi(postId) {
    return request.post('/admin/post/disable/' + postId)
}

// 评论管理
export function adminCommentPageApi(params) {
    return request.get('/admin/comment/page', { params })
}
export function adminCommentDetailApi(commentId) {
    return request.get('/admin/comment/detail/' + commentId)
}
export function adminEnableCommentApi(commentId) {
    return request.post('/admin/comment/enable/' + commentId)
}
export function adminDisableCommentApi(commentId) {
    return request.post('/admin/comment/disable/' + commentId)
}
