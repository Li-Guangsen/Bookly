<template>
  <div>
    <!-- 检索区查询表单 -->
    <div class="search">
      <el-form :inline="true" ref="searchFromRef" :model="sm">
        <!-- 表单项 -->
        <el-form-item label="主键" prop="id">
          <el-input placeholder="请输入ID" v-model="sm.id" />
        </el-form-item>
        <el-form-item label="医生:" label-width="50" prop="doctorId">
          <el-select
            placeholder="请选择医生"
            style="width: 300px"
            v-model="sm.doctorId"
          >
            <el-option
              v-for="doctorItem in doctors"
              :key="doctorItem.id"
              :label="doctorItem.name"
              :value="doctorItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="患者姓名" prop="patientName">
          <el-input placeholder="请输入患者姓名" v-model="sm.patientName" />
        </el-form-item>
        <el-form-item label="预约日期:" prop="workTime">
          <el-date-picker
            v-model="sm.workTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择预约日期"
            :width="650"
          />
        </el-form-item>
        <el-form-item label="挂号状态:" label-width="80" prop="status">
          <el-select
            v-model="sm.status"
            placeholder="请选择挂号状态"
            style="width: 200px"
          >
            <el-option label="不限" value="" />
            <el-option label="待叫号" value="待叫号" />
            <el-option label="已叫号" value="已叫号" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 操作区 -->
    <div class="operate">
      <el-button type="primary" @click="search" :icon="Search">查询</el-button>
      <el-button type="primary" @click="resetSearch" :icon="Refresh"
        >重置</el-button
      >
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
          <el-table-column prop="patient.name" label="患者姓名" width="120" />
          <el-table-column
            prop="schedule.doctor.name"
            label="医生姓名"
            width="120"
          />
          <el-table-column
            prop="schedule.doctor.subject.name"
            label="科室"
            width="110"
          />
          <el-table-column
            prop="schedule.workTime"
            label="预约日期"
            sortable
            width="120"
          />
          <el-table-column label="挂号状态" width="300" prop="status">
          </el-table-column>
          <el-table-column
            prop="description"
            label="备注"
            width="300"
            show-overflow-tooltip
          />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="editRow(scope.row)">
                EditStatus
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
      title="修改挂号信息"
      width="500"
      draggable
      :close-on-click-modal="false"
      @closed="close"
    >
      <!-- 新增操作表单 -->
      <el-form :model="esfm" ref="esfRef">
        <el-form-item label="挂号状态" prop="status">
          <el-select
            v-model="esfm.status"
            placeholder="请选择挂号状态"
            style="width: 200px"
          >
            <el-option label="待叫号" value="待叫号" />
            <el-option label="已叫号" value="已叫号" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
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
import { findAll as apiFindAll, updateStatus as apiUpdateStatusEdit } from '@/api/OrderApi'
import { findSubNames } from '@/api/SubjectApi'
import { findDocNames } from '../api/DoctorApi'
import { nextTick, onMounted, ref, toRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

//当组件加载完成后，自动调用findAll方法
onMounted(async () => {
  search()
  departments.value = await findSubNames()
  doctors.value = await findDocNames()
  // console.log(doctors.value)
  // 拼接医生的name和subject.name
  doctors.value = doctors.value.map(doctor => {
    doctor.name = `${doctor.name} -- ${doctor.subject.name}`;
    return doctor;
  });
  document.title = '挂号列表'
})
const departments = ref([])
const doctors = ref([])
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
  // console.log(pi.value.pageNo, pi.value.pageSize)
  search()
}
//查询条件引用
const sm = ref({
  id: '',
  doctorId: '',
  patientName: '',
  workTime: "",
  status: "",
})
//查询表单实例引用
let searchFromRef
function resetSearch () {
  searchFromRef.resetFields()
}
function submitForm () {
  let stu = toRaw(esfm.value)
  submitEdit(stu)
}

const show = ref(false)//控制对话框是否显示
function editRow (row) {
  //克隆防止修改数据时影响原数据
  // console.log(row)
  esfm.value = row
  // console.log(esfm.value)
  show.value = true

}
async function submitEdit (stu) {
  let resp = await apiUpdateStatusEdit(stu)
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
  id: '',
  patId: '',
  schId: '',
  patient: {
    id: '',
    name: '',
  },
  schedule: {
    id: '',
    doctorId: '',
    doctor: {
      id: '',
      name: '',
      money: '',
      subId: '',
      subject: {
        id: '',
        name: '',
      },
    },
    workTime: '',
  },
  status: '',
  description: "",
})
const sfm = doctorFormModel
const editStatusFormModel = ref({
  id: '',
  status: '',
})
const esfm = editStatusFormModel
//新增修改表单实例
let esfRef
//重置表单
function close () {
  esfRef.resetFields()
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
  width: 100px;
  height: 100px;
  border: 1px solid #ccc;
}
.photo .icon {
  width: 100px;
  height: 100px;
}
</style>
<style>
.el-popper {
  max-width: 450px;
}
</style>

