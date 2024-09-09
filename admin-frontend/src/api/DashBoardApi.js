import api from "../utils/api"


async function DoctorCount () {
    let resp = await api({
        url: `/doctors/count`,
        method: "get",
    });
    return resp;
}
async function SubjectCount () {
    let resp = await api({
        url: `/subjects/count`,
        method: "get",
    });
    return resp;
}
async function OrderCount () {
    let resp = await api({
        url: `/orders/count`,
        method: "get",
    });
    return resp;
}
async function PatientCount () {
    let resp = await api({
        url: `/patients/count`,
        method: "get",
    });
    return resp;
}
async function doctorOrderCount () {
    let resp = await api({
        url: `/orders/doctorCount`,
        method: "get",
    });
    // console.log(resp)
    return resp;
}
async function subjectOrderCount () {
    let resp = await api({
        url: `/orders/subjectCount`,
        method: "get",
    });
    // console.log(resp)
    return resp;
}
async function workDateOrderCount () {
    let resp = await api({
        url: `/orders/workDateCount`,
        method: "get",
    });
    // console.log(resp)
    return resp;
}
export { DoctorCount, SubjectCount, OrderCount, PatientCount, doctorOrderCount, subjectOrderCount, workDateOrderCount }