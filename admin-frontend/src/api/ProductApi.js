import api from "../utils/api"
const baseUrl = "/products"

//get请求 不能使用请求体传参，只能通过地址栏传参，需要使用params
async function findAll (pageNo = 1, pageSize = 5, params = {}) {
    // console.log("findAll", pageNo, pageSize, params)
    let resp = await api({
        url: baseUrl + "/all",
        method: "get",
        params: {
            pageNo,
            pageSize,
            ...params,
        }
    })
    // console.log("resp", resp)
    return resp
}
async function deleteById (id) {
    let resp = await api({
        url: baseUrl + `/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (subeject) {
    let resp = await api({
        url: baseUrl,
        method: "post",
        data: subeject
    })
    return resp;
}
async function updateStatus (ids, status) {
    let resp = await api({
        url: baseUrl + `/${status}`,
        method: "put",
        data: ids
    })
    return resp;
}
async function update (subeject) {
    let resp = await api({
        url: baseUrl,
        method: "put",
        data: subeject
    })
    return resp;
}
export { findAll, deleteById, save, update, updateStatus }