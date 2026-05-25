import request from "./request.js";

// 登录接口
export function loginApi(payload) {
    return  request.post('/account/login', payload);
}

// 注册接口
export function registerApi(payload) {
    return request.post('/account/register', payload)
}

// 获取用户信息接口
export function getUserInfoApi() {
    return request.get('/account/my')
}


// 更新个人信息接口
export function updateMyInfoApi(data) {
    return request.post('/account/my/update', data)
}
