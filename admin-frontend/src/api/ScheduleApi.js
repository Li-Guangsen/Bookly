import api from "../utils/api"

//get请求 不能使用请求体传参，只能通过地址栏传参，需要使用params
async function findAll (pageNo = 1, pageSize = 5, params = {}) {
    // console.log("findAll", pageNo, pageSize, params)
    let resp = await api({
        url: "/schedules",
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
        url: `/schedules/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (schedule) {
    let resp = await api({
        url: "/schedules",
        method: "post",
        data: schedule
    })
    return resp;
}
async function addOrder (order) {
    let resp = await api({
        url: "/schedules/addOrder",
        method: "post",
        data: order
    })
    return resp;
}
async function update (schedule) {
    let resp = await api({
        url: "/schedules",
        method: "put",
        data: schedule
    })
    return resp;
}
export { findAll, deleteById, save, update, addOrder }