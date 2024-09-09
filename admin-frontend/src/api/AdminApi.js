import api from "../utils/api"
const baseURL = "/admins"
//get请求 不能使用请求体传参，只能通过地址栏传参，需要使用params
async function findAll (pageNo = 1, pageSize = 5, params = {}) {
    // console.log("findAll", pageNo, pageSize, params)
    let resp = await api({
        url: baseURL,
        method: "get",
        params: {
            pageNo,
            pageSize,
            ...params,
        }
    })
    return resp
}
async function getUsername () {
    let resp = await api({
        url: baseURL + "/username",
        method: "get",
    });
    return resp;
}
async function deleteById (id) {
    let resp = await api({
        url: baseURL + `/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (adm) {
    let resp = await api({
        url: baseURL,
        method: "post",
        data: adm
    })
    return resp;
}
async function login (adm) {
    let resp = await api({
        url: baseURL + "/login",
        method: "post",
        data: adm
    })
    return resp;
}
async function update (adm) {
    let resp = await api({
        url: baseURL,
        method: "put",
        data: adm
    })
    return resp;
}
async function apiUpdatePass (adm) {
    let resp = await api({
        url: baseURL + "/password",
        method: "put",
        data: adm
    })
    return resp;
}
export { findAll, deleteById, save, update, login, apiUpdatePass, getUsername }