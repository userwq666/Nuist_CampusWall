import axios from 'axios'

// 统一请求实例
const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截：自动携带 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截：统一处理三段式返回 { code, message, data }
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 正常业务
    if (res && res.code === 0) {
      return res
    }

    // 理论上极少走到这里（因为业务失败一般是 HTTP 4xx/5xx）
    const msg = res?.message || '请求失败，请稍后重试'
    return Promise.reject(new Error(msg))
  },
  (error) => {
    let msg = '请求失败，请稍后重试'

    if (error.response) {
      const status = error.response.status
      const serverCode = error.response.data?.code
      const serverMsg = error.response.data?.message

      if (serverMsg) msg = serverMsg

      // 状态码兜底
      if (status === 401) msg = serverMsg || '未登录或登录已过期'
      if (status === 403) msg = serverMsg || '没有权限'
      if (status === 404) msg = serverMsg || '资源不存在'
      if (status === 409) msg = serverMsg || '数据冲突'
      if (status === 413) msg = serverMsg || '上传内容过大'
      if (status === 415) msg = serverMsg || '文件类型不支持'
      if (status === 422) msg = serverMsg || '参数错误'
      if (status === 500) msg = serverMsg || '服务器异常'

      const e = new Error(msg)
      e.status = status
      e.code = serverCode
      return Promise.reject(e)
    }

    // 网络错误（后端未启动/DNS/超时等）
    if (error.code === 'ECONNREFUSED') {
      msg = '后端服务未启动，请先启动后端'
    } else if (error.code === 'ECONNABORTED') {
      msg = '请求超时，请检查网络与后端状态'
    }
    return Promise.reject(new Error(msg))
  }
)

export default request