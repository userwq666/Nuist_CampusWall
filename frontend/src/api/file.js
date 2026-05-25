import request from './request'

// 上传文件
export function uploadFileApi(file, fileType) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/file/upload?fileType=' + fileType, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}
