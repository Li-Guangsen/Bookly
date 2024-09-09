import api from "../utils/api"

//get请求 不能使用请求体传参，只能通过地址栏传参，需要使用params
async function findAll (pageNo = 1, pageSize = 5, params = {}) {
    // console.log("findAll", pageNo, pageSize, params)
    let resp = await api({
        url: "/orders",
        method: "get",
        params: {
            pageNo,
            pageSize,
            ...params,
        }
    })
    return resp
}
async function deleteById (id) {
    let resp = await api({
        url: `/orders/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (order) {
    let resp = await api({
        url: "/orders",
        method: "post",
        data: order
    })
    return resp;
}
async function update (order) {
    let resp = await api({
        url: "/orders",
        method: "put",
        data: order
    })
    return resp;
}
async function updateStatus (order) {
    let resp = await api({
        url: "/orders/status",
        method: "put",
        data: order
    })
    // console.log("updateStatus", resp)
    return resp;
}
export { findAll, deleteById, save, update, updateStatus }