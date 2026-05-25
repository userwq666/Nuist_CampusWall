import request from './request'

// 上传文件
export function uploadFileApi(file, fileType) {
    const formData = new FormData()
    formData.append('file', file)
    // 不手动设置 Content-Type，让 axios 自动生成正确的 multipart/form-data; boundary=...
    return request.post('/file/upload?fileType=' + fileType, formData)
}
