import api from "../utils/api"
const baseUrl = "/categories"
async function findAll () {
    let resp = await api({
        url: baseUrl + "/tree",
        method: "get",
    })
    return resp.data.children;
}
async function apiFindNameTree (params) {
    let resp = await api({
        url: baseUrl + "/nameTree",
        method: "get",
        params: {
            ...params
        }
    })
    console.log("resp", resp)
    if (resp.success)
        return resp.data.children;
    else
        return [];
}
async function findById () {
    let resp = await api({
        url: baseUrl + `/${id}`,
        method: "get",
    })
    return resp;
}
async function findCategoryNames () {
    let resp = await api({
        url: baseUrl + "/names",
        method: "get",
    })
    return resp.data;
}
async function deleteById (id) {
    let resp = await api({
        url: baseUrl + `/${id}`,
        method: "delete",
    });
    return resp;
}
async function save (doctor) {

    let resp = await api({
        url: baseUrl,
        method: "post",
        data: doctor
    })
    return resp;
}
async function update (doctor) {
    console.log("doctor", doctor)
    let p = Object.assign({}, doctor)
    delete p.children;
    console.log("p", p)
    let resp = await api({
        url: baseUrl,
        method: "put",
        data: doctor
    })
    return resp;
}
export { findAll, deleteById, save, update, findById, apiFindNameTree, findCategoryNames }