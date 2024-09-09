<template>
  <div v-loading="loading">
    <div
      style="
        border: 1px solid #ebeef5; /* 添加边框样式 */
        padding: 10px; /* 可选：添加内边距 */
        border-radius: 5px;
      "
    >
      <!-- 检索区查询表单 -->
      <div class="search">
        <el-form :inline="true" ref="searchFromRef" :model="sm">
          <!-- 表单项 -->
          <el-form-item label="主键" prop="id">
            <el-input placeholder="请输入ID" v-model="sm.id" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input placeholder="请输入手机号" v-model="sm.phone" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input placeholder="请输入昵称" v-model="sm.nickname" />
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
          <el-table-column prop="id" label="ID" width="70" sortable />
          <el-table-column prop="icon" label="头像" width="80">
            <template #default="{ row }">
              <el-image style="width: 100%; height: 100%" :src="row.icon" />
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="昵称" width="200" />
          <el-table-column prop="phone" label="用户名/手机号" width="200" />
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
            prop="description"
            label="备注"
            width="480"
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
              <el-button size="small" @click="editPassRow(scope.row)">
                EditPassword
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
      v-model="pShow"
      title="修改密码"
      width="640"
      draggable
      :close-on-click-modal="false"
      @closed="closePass"
    >
      <el-form :model="pfm" ref="pfRef">
        <el-form-item label="密码:" label-width="80" prop="password">
          <el-input v-model="pfm.password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pShow = false">取消</el-button>
          <el-button type="primary" @click="submitPassForm"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 新增对话框 -->
    <el-dialog
      v-model="show"
      :title="dialogTitle"
      width="640"
      draggable
      :close-on-click-modal="false"
      @closed="close"
    >
      <!-- 新增操作表单 -->
      <el-form :model="sfm" ref="sfRef">
        <el-row :gutter="20">
          <el-col :span="12"
            ><el-form-item label="手机号:" label-width="80" prop="phone">
              <el-input v-model="sfm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="昵称:" label-width="80" prop="nickname">
              <el-input
                v-model="sfm.nickname"
                placeholder="请输入昵称"
              /> </el-form-item
          ></el-col>
          <el-col :span="12">
            <el-form-item label="头像:" label-width="100" prop="icon">
              <el-upload
                class="photo"
                action="/api/uploads/qcos"
                :show-file-list="false"
                :on-success="photoSuccess"
              >
                <el-image
                  v-if="sfm.icon"
                  style="
                    width: 170px;
                    height: 170px;
                    background-position: center center;
                    background-repeat: no-repeat;
                    background-size: contain;
                  "
                  :src="sfm.icon"
                />
                <el-icon class="icon" v-else> <Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="信息:" label-width="60" prop="note">
          <el-input
            v-model="sfm.note"
            :rows="8"
            type="textarea"
            placeholder="请输入信息"
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
import { findAll as apiFindAll, deleteById as apiDeleteById, save as apiSave, update as apiUpdate, apiUpdatePass } from '@/api/UserApi'
import { nextTick, onMounted, ref, toRaw } from 'vue'
import { descriptionProps, ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(true)
//当组件加载完成后，自动调用findAll方法
onMounted(async () => {
  search()
  document.title = '用户管理'
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
  // console.log(sm.value)
  let params = sm.value
  // console.log(params)
  let resp = await apiFindAll(pi.value.pageNo, pi.value.pageSize, params)
  // console.log(resp.rows)
  tableData.value = resp.rows
  pi.value = resp.pi
}
function paginate () {
  // console.log(pi.value.pageNo, pi.value.pageSize)
  search()
}
//查询条件引用
const sm = ref({
  id: '',
  nickname: "",
  phone: "",
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
function submitPassForm () {
  let stu = toRaw(pfm.value)
  submitEditPass(stu)
}
function deleteRow (row) {
  console.log(row)
  let id = row.id
  // console.log(id)
  deleteById(id)
}
function deleteById (id) {
  ElMessageBox.confirm(
    '是否确认删除选中数据?',
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
const pShow = ref(false)//控制对话框是否显示
const show = ref(false)//控制对话框是否显示
function editPassRow (row) {
  pfm.value = row
  pShow.value = true
}
function editRow (row) {
  //克隆防止修改数据时影响原数据
  row = Object.assign({}, row)
  show.value = true
  dialogTitle.value = '修改信息'
  nextTick(() => {
    sfm.value = row
  })
}
function editStatus (row) {
  console.log(row)
  submitEdit(row)
}
async function submitEditPass (stu) {
  let resp = await apiUpdatePass(stu)
  if (resp.success) {
    ElMessage({
      type: 'success',
      message: '操作成功',
    })
    pShow.value = false
    search()
  }
  else {
    ElMessage({
      message: '操作失败',
      type: 'warning',
    })
  }
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
  sfm.value.id = ''
  show.value = true
  dialogTitle.value = '添加(密码默认123456)'
}
async function submitAdd (stu) {
  let resp = await apiSave(stu)
  // console.log(resp)
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
const patientFormModel = ref({
  nickname: "",
  phone: "",
  icon: "",
  note: "",
})
const sfm = patientFormModel
const passwordFormModel = ref({
  id: "",
  password: "",
})
const pfm = passwordFormModel

//新增修改表单实例
let pfRef
let sfRef
//重置表单
function closePass () {
  pfRef.value = { id: "", password: "" }
}
function close () {
  sfRef.resetFields()
}
let dialogTitle = ref()
function photoSuccess (resp) {
  // console.log(resp.url)
  if (resp.success) {
    sfm.value.icon = resp.info
  }
  else {
    ElMessage({
      message: '上传失败',
      type: 'warning',
    })
  }
}
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
<style scoped>
.photo {
  width: 170px;
  height: 170px;
  border: 1px solid #ccc;
}
.photo .icon {
  width: 170px;
  height: 170px;
}
</style>
<style>
.el-popper {
  max-width: 450px;
}
</style>

