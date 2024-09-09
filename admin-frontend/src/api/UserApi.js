import api from "../utils/api"
const baseUrl = "/users"
//get请求 不能使用请求体传参，只能通过地址栏传参，需要使用params
async function findAll (pageNo = 1, pageSize = 5, params = {}) {
    // console.log("findAll", pageNo, pageSize, params)
    let p = Object.assign({}, params)
    let resp = await api({
        url: baseUrl,
        method: "get",
        params: {
            pageNo,
            pageSize,
            ...p,
        }
    })
    return resp
}
async function deleteById (id) {
    let resp = await api({
        url: baseUrl + `/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (patient) {
    let resp = await api({
        url: baseUrl,
        method: "post",
        data: patient
    })
    return resp;
}
async function update (patient) {
    let resp = await api({
        url: baseUrl,
        method: "put",
        data: patient
    })
    return resp;
}
async function apiUpdatePass (patient) {
    let resp = await api({
        url: baseUrl + "/password",
        method: "put",
        data: patient
    })
    return resp;
}
export { findAll, deleteById, save, update, apiUpdatePass }