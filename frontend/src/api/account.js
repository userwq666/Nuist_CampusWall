import request from "./request.js";

export function loginApi(payload) {
    return  request.post('/account/login', payload);
}