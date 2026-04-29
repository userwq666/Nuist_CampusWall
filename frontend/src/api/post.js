import request from './request'

// 获取帖子列表接口
export function PostListApi(params) {
    return request.get('/post/page', { params })
}
