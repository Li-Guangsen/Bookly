<template>
  <div v-loading="loading">
    <div
      style="
        border: 1px solid #ebeef5; /* 添加边框样式 */
        padding: 10px; /* 可选：添加内边距 */
        border-radius: 5px;
      "
    >
      <div class="search">
        <el-form :inline="true" ref="searchFromRef" :model="sm">
          <!-- 表单项 -->
          <el-form-item label="名称" prop="name">
            <el-input placeholder="请输入类别名称" v-model="sm.name" />
          </el-form-item>
        </el-form>
      </div>
      <!-- 操作区 -->
      <div class="operate">
        <el-button type="primary" @click="addOne" :icon="Plus"
          >新增一级分类</el-button
        >
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
          :data="treeData"
          border
          style="width: 100%"
          :header-cell-style="headercellStyle"
          row-key="id"
        >
          <el-table-column prop="id" label="ID" width="200" sortable />
          <el-table-column prop="name" label="类别名称" width="200" />
          <el-table-column prop="description" label="备注" width="200" />
          <el-table-column prop="parentId" label="上级ID" width="200" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="primary" @click="add(scope.row)" :icon="Plus"
                >新增下级分类</el-button
              >
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
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <!-- 新增对话框 -->
    <el-dialog
      v-model="show"
      :title="dialogTitle"
      width="640"
      draggable
      :close-on-click-modal="false"
      @closed="close"
    >
      <!-- 新增表单 -->
      <el-form :model="sfm" ref="sfRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级类别:" label-width="80" prop="name">
              <el-input
                :value="parentName(sfm.parentId)"
                placeholder="请输入类别名称"
                disabled
              />
            </el-form-item>
            <el-form-item label="类别名称:" label-width="80" prop="name">
              <el-input v-model="sfm.name" placeholder="请输入类别名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注:" label-width="45" prop="description">
          <el-input
            v-model="sfm.description"
            :rows="8"
            type="textarea"
            placeholder="备注"
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
import { findAll as apiFindAll, deleteById as apiDeleteById, findCategoryNames, apiFindNameTree, save as apiSave, update as apiUpdate, findById as apifindById } from '../api/CategoryApi'
import { nextTick, onMounted, ref, toRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(true)
//当组件加载完成后，自动调用findAll方法
onMounted(async () => {
  search()
  categoryNames.value = await findCategoryNames()
  // console.log(categoryNames.value)
})
const categoryNames = ref([])
const treeData = ref()
// 前两项是双向数据绑定
async function search () {
  loading.value = true
  let resp = null
  if (sm.value.name == '' || sm.value.name == null)
    resp = await apiFindAll()
  else
    resp = await apiFindNameTree(sm.value)
  treeData.value = resp
  loading.value = false
}
function submitForm () {
  let stu = toRaw(sfm.value)
  if (stu.id && stu.id != '') {//修改
    submitEdit(stu)
  } else {//添加
    submitAdd(stu)
  }
}
function parentName (id) {
  let name = ''
  categoryNames.value.forEach(item => {
    if (item.id == id) {
      name = item.name
    }
  })
  return name
}
function deleteRow (row) {
  if (row.children && row.children.length > 0) {
    ElMessage({
      message: '请先删除下级类别',
      type: 'warning',
    })
    return
  }
  let id = row.id
  console.log(id)
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
        message: '分类中有商品，删除失败',
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
function editRow (row) {
  //克隆防止修改数据时影响原数据
  row = Object.assign({}, row)
  show.value = true
  dialogTitle.value = '修改信息'
  nextTick(() => {
    sfm.value = row
  })
  console.log(sfm.value)
}
//查询条件引用
const sm = ref({
  name: "",
})
//查询表单实例引用
let searchFromRef
function resetSearch () {
  searchFromRef.resetFields()
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

function add (row) {
  row = Object.assign({}, row)
  sfm.value = {}
  nextTick(() => {
    sfm.value.parentId = row.id
  })
  console.log(sfm.value)
  show.value = true
  dialogTitle.value = '添加'
}
function addOne () {
  sfm.value = {}
  sfm.value.id = ''
  sfm.value.parentId = 0
  console.log(sfm.value)
  show.value = true
  dialogTitle.value = '添加'
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
const doctorFormModel = ref({
  id: "",
  name: "",
  parentId: "",
  description: "",
})
const sfm = doctorFormModel

//新增修改表单实例
let sfRef
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
.catrgory-container {
  border: 1px solid #ebeef5; /* 添加边框样式 */
  padding: 10px; /* 可选：添加内边距 */
  border-radius: 2px;
}
</style>
<style>
.el-popper {
  max-width: 450px;
}
</style>

