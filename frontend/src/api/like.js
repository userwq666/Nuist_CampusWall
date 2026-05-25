import request from './request'

// 点赞
export function doLikeApi(data) {
    return request.post('/like/do', data)
}

// 取消点赞
export function undoLikeApi(data) {
    return request.post('/like/undo', data)
}

// 检查是否已点赞
export function checkLikeApi(data) {
    return request.post('/like/check', data)
}
