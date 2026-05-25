import request from './request'

// 创建评论
export function commentCreateApi(data) {
    return request.post('/comment/create', data)
}

// 获取帖子评论分页
export function commentPageApi(params) {
    return request.get('/comment/page', { params })
}

// 获取我的评论分页
export function commentMyPageApi(params) {
    return request.get('/comment/my/page', { params })
}

// 删除评论
export function commentDeleteApi(id) {
    return request.post('/comment/delete/' + id)
}
