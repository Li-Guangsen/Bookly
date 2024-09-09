<template>
  <div v-loading="loading">
    <div class="admin-container">
      <!-- 检索区查询表单 -->
      <div class="search">
        <el-form :inline="true" ref="searchFromRef" :model="sm">
          <!-- 表单项 -->
          <el-form-item label="主键" prop="id">
            <el-input placeholder="请输入ID" v-model="sm.id" />
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input placeholder="请输入管理员账号" v-model="sm.username" />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input placeholder="请输入管理员姓名" v-model="sm.name" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input placeholder="请输入管理员手机号" v-model="sm.phone" />
          </el-form-item>
        </el-form>
      </div>
      <!-- 操作区 -->
      <div class="operate">
        <el-button type="primary" @click="add" :icon="Plus">新增</el-button>
        <el-button type="primary" @click="search" :icon="Search"
          >查询</el-button
        >
        <el-button type="primary" @click="resetSearch" :icon="Refresh"
          >重置</el-button
        >
      </div>
    </div>

    <!-- 数据展示区 -->
    <div class="data">
      <!-- 表格 -->
      <div class="grid" style="margin-top: 5px">
        <el-table
          :data="tableData"
          border
          style="width: 100%"
          :header-cell-style="headercellStyle"
        >
          <el-table-column prop="id" label="ID" width="80" sortable />
          <el-table-column prop="name" label="管理员姓名" width="150" />
          <el-table-column prop="username" label="账号" width="200" />
          <el-table-column prop="phone" label="手机号" width="200" />
          <el-table-column label="是否启用" width="150" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="editStatus(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            prop="note"
            label="备注"
            width="500"
            show-overflow-tooltip
          />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="editRow(scope.row)">
                Edit
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="deleteRow(scope.row)"
              >
                Delete
              </el-button>
              <el-button size="small" @click="editUserPassRow(scope.row)">
                EditAdminPassword
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination" style="margin: 5px">
        <el-pagination
          v-model:current-page="pi.pageNo"
          v-model:page-size="pi.pageSize"
          :page-sizes="[5, 10, 15, 30, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pi.total"
          background
          @change="paginate"
        />
      </div>
    </div>
    <el-dialog
      v-model="pUShow"
      title="修改管理员密码"
      width="640"
      draggable
      :close-on-click-modal="false"
      @closed="closeUserPass"
    >
      <el-form :model="pufm" ref="pufRef">
        <el-form-item label="密码:" label-width="80" prop="password">
          <el-input v-model="pufm.password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pUShow = false">取消</el-button>
          <el-button type="primary" @click="submitUserPassForm">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 新增对话框 -->
    <el-dialog
      v-model="show"
      :title="dialogTitle"
      width="500"
      draggable
      :close-on-click-modal="false"
      @closed="close"
    >
      <!-- 新增操作表单 -->
      <el-form :model="sfm" ref="sfRef">
        <el-row :gutter="20">
          <el-col :span="12"> </el-col>
          <el-col :span="12"> </el-col>
        </el-row>
        <el-form-item label="账号:" label-width="120" prop="username">
          <el-input
            v-model="sfm.username"
            style="width: 300px"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item label="姓名:" label-width="120" prop="name">
          <el-input
            v-model="sfm.name"
            style="width: 300px"
            placeholder="请输入管理员姓名"
          />
        </el-form-item>
        <el-form-item label="手机号:" label-width="120" prop="phone">
          <el-input
            v-model="sfm.phone"
            style="width: 300px"
            placeholder="请输入管理员手机号"
          />
        </el-form-item>
        <el-form-item label="备注:" label-width="45" prop="note">
          <el-input
            v-model="sfm.note"
            :rows="8"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <!-- 对话框按钮 # 插槽 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="show = false">取消</el-button>
          <el-button type="primary" @click="submitForm"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { Plus, Delete, Edit, Refresh, Search, Share, Upload, } from '@element-plus/icons-vue'
import { findAll as apiFindAll, deleteById as apiDeleteById, save as apiSave, update as apiUpdate, apiUpdatePass as apiAdminUpdatePass } from "../api/AdminApi"
import { nextTick, onMounted, ref, toRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(true)
//当组件加载完成后，自动调用findAll方法
onMounted(async () => {
  search()
  document.title = '管理员管理'
  loading.value = false
})
const tableData = ref()
// 前两项是双向数据绑定
const pi = ref({
  pageNo: 1,
  pageSize: 5,
  total: 1000
})
async function search () {
  let params = sm.value
  // console.log(params)
  let resp = await apiFindAll(pi.value.pageNo, pi.value.pageSize, params)
  //   console.log(resp)
  tableData.value = resp.rows
  pi.value = resp.pi
}
function paginate () {
  console.log(pi.value.pageNo, pi.value.pageSize)
  search()
}
//查询条件引用
const sm = ref({
  id: '',
  name: "",
  username: "",
  phone: ""
})
//查询表单实例引用
let searchFromRef
function resetSearch () {
  searchFromRef.resetFields()
}
function submitForm () {
  let stu = toRaw(sfm.value)
  if (stu.id && stu.id != '') {//修改
    submitEdit(stu)
  } else {//添加
    submitAdd(stu)
  }
}
function submitUserPassForm () {
  let stu = toRaw(pufm.value)
  submitEditUserPass(stu)
}
async function submitEditUserPass (stu) {
  let resp = await apiAdminUpdatePass(stu)
  if (resp.success) {
    ElMessage({
      type: 'success',
      message: '操作成功',
    })
    pUShow.value = false
    search()
  }
  else {
    ElMessage({
      message: '操作失败',
      type: 'warning',
    })
  }
}
function deleteRow (row) {
  let id = row.id
  console.log(id)
  deleteById(id)
}
function deleteById (id) {
  ElMessageBox.confirm(
    '是否确认删除选中管理员数据?',
    '删除确认',
    {
      type: 'warning',
    }
  ).then(async () => {//点击确认
    let resp = await apiDeleteById(id)
    if (resp.success) {
      ElMessage({
        type: 'success',
        message: '删除成功',
      })
      search()
    }
    else {
      ElMessage({
        message: '删除失败',
        type: 'warning',
      })
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '删除取消',
    })
  })
}
const show = ref(false)//控制对话框是否显示
const pUShow = ref(false)
function editUserPassRow (row) {
  pUShow.value = true
  pufm.value = row
}
function editStatus (row) {
  console.log(row)
  submitEdit(row)
}
function editRow (row) {
  //克隆防止修改数据时影响原数据
  row = Object.assign({}, row)
  // console.log(row)
  show.value = true
  dialogTitle.value = '修改管理员信息'
  nextTick(() => {
    sfm.value = row
  })
}
async function submitEdit (stu) {
  let resp = await apiUpdate(stu)
  // console.log("0000" + resp)
  if (resp.success) {
    ElMessage({
      type: 'success',
      message: '操作成功',
    })
    show.value = false
    search()
  }
  else {
    ElMessage({
      message: '操作失败',
      type: 'warning',
    })
  }
}

function add () {
  // console.log(sfm.value)
  sfm.value.id = ''
  // console.log(sfm.value)
  show.value = true
  dialogTitle.value = '添加管理员(密码默认123456)'
}
async function submitAdd (stu) {
  let resp = await apiSave(stu)
  console.log(resp)
  if (resp.success) {
    ElMessage({
      type: 'success',
      message: '操作成功',
    })
    show.value = false
    search()
  }
  else {
    ElMessage({
      message: '操作失败',
      type: 'warning',
    })
  }
}
const adminFormModel = ref({
  name: "",
  username: "",
  phone: "",
  note: "",
})
const sfm = adminFormModel
const passwordUserFormModel = ref({
  id: '',
  password: ''
})
const pufm = passwordUserFormModel
//新增修改表单实例
let sfRef
let pufRef
function closeUserPass () {
  pufRef.value = { id: '', password: '' }
}
//重置表单
function close () {
  sfRef.resetFields()
}
let dialogTitle = ref()
function a () {
  console.log('a')
}
function headercellStyle () {
  return {
    backgroundColor: '#f5f7fa',
    textAlign: 'center',
    color: "#000"
  }
}
</script>
<style>
.el-popper {
  max-width: 470px;
}
.admin-container {
  border: 1px solid #ebeef5; /* 添加边框样式 */
  padding: 10px; /* 可选：添加内边距 */
  border-radius: 2px;
}
</style>

