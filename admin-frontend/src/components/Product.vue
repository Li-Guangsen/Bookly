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
          <el-form-item label="名称" prop="name">
            <el-input placeholder="请输入图书名称" v-model="sm.name" />
          </el-form-item>
          <el-form-item label="图书分类" prop="categoryId">
            <el-tree-select
              v-model="sm.categoryId"
              :data="categoryNames"
              :render-after-expand="false"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input placeholder="请输入作者" v-model="sm.author" />
          </el-form-item>
          <el-form-item label="晚于日期筛选" prop="publishDate">
            <el-date-picker
              v-model="sm.publishDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请输入日期"
              :width="650"
            />
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
        <el-button type="primary" @click="updateStatus(1)" :icon="TopRight"
          >一键上架</el-button
        >
        <el-button type="danger" @click="updateStatus(0)" :icon="BottomRight"
          >一键下架</el-button
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
          @row-click="rowClick"
          ref="tableRef"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column
            prop="id"
            label="ID"
            width="75"
            sortable
            fixed="left"
            align="center"
          />
          <el-table-column
            prop="name"
            label="图书名称"
            width="140"
            fixed="left"
          />
          <el-table-column
            prop="mainImage"
            label="图片"
            width="120"
            align="center"
          >
            <template #default="scope">
              <div
                class="row-mainImage"
                :style="'background-image:url(' + scope.row.mainImage + ')'"
              ></div>
            </template>
          </el-table-column>
          <el-table-column prop="category.name" label="图书分类" width="120" />
          <el-table-column prop="subtitle" label="副标题" width="180" />
          <el-table-column prop="author" label="作者" width="100" />
          <el-table-column prop="publisher" label="出版社" width="120" />
          <el-table-column prop="publishDate" label="出版日期" width="110" />
          <el-table-column prop="price" label="价格" width="110" />
          <el-table-column prop="stock" label="库存" width="110" />
          <el-table-column prop="sale" label="销量" width="110" />
          <el-table-column label="是否上架" width="150" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="editStatus(scope.row)"
              />
            </template>
          </el-table-column>
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

    <!-- 新增对话框 -->
    <el-dialog
      v-model="show"
      :title="dialogTitle"
      width="640"
      draggable
      :close-on-click-modal="false"
      @closed="close"
    >
      <el-tabs v-model="currentTab">
        <el-tab-pane label="基本信息" name="baseTab"
          ><el-form :model="sfm" ref="sfRef">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="名称:" label-width="80" prop="name">
                  <el-input v-model="sfm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item label="作者:" label-width="80" prop="author">
                  <el-input v-model="sfm.author" placeholder="请输入作者" />
                </el-form-item>
                <el-form-item label="分类" label-width="80" prop="categoryId">
                  <el-tree-select
                    v-model="sfm.categoryId"
                    :data="categoryNames"
                    :render-after-expand="false"
                    style="width: 240px"
                  />
                </el-form-item>
                <el-form-item label="价格:" label-width="80" prop="price">
                  <el-input v-model="sfm.price" placeholder="请输入价格" />
                </el-form-item>
              </el-col>
              <el-col :span="12"
                ><el-form-item label="图片:" label-width="120" prop="icon">
                  <el-upload
                    class="photo"
                    action="/api/uploads/alioos"
                    :show-file-list="false"
                    :on-success="photoSuccess"
                  >
                    <el-image
                      v-if="sfm.mainImage"
                      style="
                        width: 170px;
                        height: 170px;
                        background-position: center center;
                        background-repeat: no-repeat;
                        background-size: contain;
                      "
                      :src="sfm.mainImage"
                    />
                    <el-icon class="icon" v-else> <Plus /></el-icon>
                  </el-upload> </el-form-item
              ></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12"
                ><el-form-item
                  label="出版社:"
                  label-width="80"
                  prop="publisher"
                >
                  <el-input
                    v-model="sfm.publisher"
                    placeholder="请输入出版社"
                  /> </el-form-item
              ></el-col>

              <el-col :span="12"
                ><el-form-item
                  label="出版日期:"
                  label-width="100"
                  prop="createTime"
                >
                  <el-date-picker
                    v-model="sfm.publishDate"
                    type="date"
                    placeholder="请选择出版日期"
                    value-format="YYYY-MM-DD"
                    :width="650"
                  /> </el-form-item
              ></el-col>
            </el-row>
            <el-row :gutter="20"
              ><el-col :span="12"
                ><el-form-item label="库存:" label-width="80" prop="stock">
                  <el-input
                    v-model="sfm.stock"
                    placeholder="请输入库存"
                  /> </el-form-item
              ></el-col>
              <el-col :span="12"
                ><el-form-item label="销量:" label-width="100" prop="sale">
                  <el-input
                    v-model="sfm.sale"
                    placeholder="请输入销量"
                  /> </el-form-item
              ></el-col>
            </el-row>
            <el-form-item label="副标题:" label-width="80" prop="subtitle">
              <el-input
                v-model="sfm.subtitle"
                placeholder="请输入副标题"
              /> </el-form-item></el-form
        ></el-tab-pane>
        <el-tab-pane label="详情" name="detailTab"
          ><div style="border: 1px solid #ccc">
            <!-- 富文本编辑器的工具栏 -->
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
            />
            <!-- 富文本编辑器 -->
            <Editor
              style="height: 400px; overflow-y: hidden"
              v-model="sfm.detail"
              :defaultConfig="editorConfig"
              @onCreated="handleCreated"
            /></div
        ></el-tab-pane>
      </el-tabs>
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
import { Plus, Delete, TopRight, BottomRight, Edit, Refresh, Search, Share, Upload, } from '@element-plus/icons-vue'
import { findAll as apiCategoryNames, update } from "../api/CategoryApi.js"
import { findAll as apiFindAll, deleteById as apiDeleteById, save as apiSave, update as apiUpdate, updateStatus as apiUpdateStatus } from "../api/ProductApi"
import { nextTick, onMounted, ref, toRaw, shallowRef, reactive, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
const loading = ref(true)
//当组件加载完成后，自动调用findAll方法
onMounted(async () => {
  search()
  document.title = '商品管理'
  buildcategoryNames()
  loading.value = false
})
let tableRef//表格实例引用
function rowClick (row) {
  // console.log(row)
  tableRef.toggleRowSelection(row)
}
async function updateStatus (status) {
  let rows = tableRef.getSelectionRows();
  // rows = toRaw(rows)
  rows = rows.map(row => row.id)
  if (rows.length == 0) {
    ElMessage({
      message: '请选择要修改的数据',
      type: 'warning',
    })
    return
  }
  // console.log(rows)
  apiUpdateStatus(rows, status).then(resp => {
    // console.log(resp)
    if (resp.success) {
      ElMessage({
        type: 'success',
        message: '操作成功',
      })
      search()
    }
    else {
      ElMessage({
        message: '操作失败',
        type: 'warning',
      })
    }
  })
}
function photoSuccess (resp) {
  // console.log(resp.url)
  if (resp.success) {
    sfm.value.mainImage = resp.info
  }
  else {
    ElMessage({
      message: '上传失败',
      type: 'warning',
    })
  }
}
//富文本编辑器的引用
const editorRef = shallowRef();

//工具栏的配置项
const toolbarConfig = {};

//富文本编辑器的配置项
const editorConfig = {
  placeholder: '请输入商品详情',
  MENU_CONF: {
    uploadImage: {
      server: "/api/uploads/editor",//必须项
      // form-data fieldName ，默认值 'wangeditor-uploaded-image'
      fieldName: 'file',
      // 自定义增加 http  header
      // headers: {
      //   "Authorization": getToken()
      // },

      // // 跨域是否传递 cookie ，默认为 false
      // withCredentials: true,

    }
  }
}

//组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

//当富文本编辑器创建之后，给引用赋值
const handleCreated = (editor) => {
  editorRef.value = editor;
}
const categoryNames = ref([])
let currentTab = ref("baseTab");
async function buildcategoryNames () {
  let resp = apiCategoryNames()
  // console.log(resp)
  resp.then(resp => {
    buildCategoryTree(resp);
    categoryNames.value = resp;
  });
}
function buildCategoryTree (nodeArr) {
  for (let node of nodeArr) {
    node.label = node.name;
    node.value = node.id;
    if (Array.isArray(node.children)) {
      buildCategoryTree(node.children);
    }
  }
}
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
  console.log(resp)
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
function deleteRow (row) {
  let id = row.id
  console.log(id)
  deleteById(id)
}
async function deleteById (id) {
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
const show = ref(false)//控制对话框是否显示
function editStatus (row) {
  // console.log(row)
  submitEdit(row)
}
function editRow (row) {
  //克隆防止修改数据时影响原数据
  row = Object.assign({}, row)
  currentTab = "baseTab";
  // console.log(row)
  show.value = true
  dialogTitle.value = '修改信息'
  nextTick(() => {
    sfm.value = row
  })
  // sfm.value = row

  // console.log(sfm.value)
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
  sfm.value = {}
  currentTab = "baseTab";
  // console.log(sfm.value)
  show.value = true
  dialogTitle.value = '添加'
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
const subjectFormModel = ref({
  categoryId: "",
  name: "",
  subtitle: "",
  mainImage: "",
  author: "",
  publisher: "",
  publishDate: "",
  price: "",
  stock: "",
  sale: "",
  detail: "",
  status: "",

})
const sfm = subjectFormModel

//新增修改表单实例
let sfRef
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
  max-width: 440px;
}
.photo {
  width: 170px;
  height: 170px;
  border: 1px solid #ccc;
}
.photo .icon {
  width: 170px;
  height: 170px;
}
.row-mainImage {
  width: 90px;
  height: 90px;
  background-position: center center;
  background-repeat: no-repeat;
  background-size: contain;
  margin: 0 auto;
}
</style>

