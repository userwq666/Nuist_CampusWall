import request from './request'

// 获取帖子列表接口（支持自定义路径，默认 /post/page）
export function PostListApi(urlOrParams, params) {
    if (typeof urlOrParams === 'string') {
        return request.get(urlOrParams, { params })
    }
    return request.get('/post/page', { params: urlOrParams })
}

// 获取我的帖子
export function MyPostListApi(params) {
    return request.get('/post/my/page', { params })
}

// 获取帖子详情
export function PostDetailApi(id) {
    return request.get('/post/' + id)
}

// 创建帖子
export function CreatePostApi(data) {
    return request.post('/post/create', data)
}

// 更新帖子
export function UpdatePostApi(id, data) {
    return request.post('/post/update/' + id, data)
}

// 删除帖子
export function DeletePostApi(id) {
    return request.post('/post/delete/' + id)
}
