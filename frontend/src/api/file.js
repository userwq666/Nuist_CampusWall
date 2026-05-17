import request from './request'

// 上传文件
export function uploadFileApi(file, fileType) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileType', fileType)
    return request.post('/file/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}
