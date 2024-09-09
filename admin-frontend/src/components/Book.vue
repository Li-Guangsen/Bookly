<template>
  <div>
    <!-- 检索区查询表单 -->
    <div class="search">
      <el-form :inline="true" ref="searchFromRef" :model="sm">
        <!-- 表单项 -->
        <el-form-item label="科室" prop="subId">
          <el-select
            placeholder="请选择科室"
            style="width: 300px"
            v-model="sm.subId"
          >
            <el-option key="0" label="不限" value="" />
            <el-option
              v-for="department in departments"
              :key="department.id"
              :label="department.name"
              :value="department.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="医生姓名" prop="doctorName">
          <el-input placeholder="请输入医生姓名" v-model="sm.doctorName" />
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
      </el-form>
    </div>
    <!-- 操作区 -->
    <div class="operate">
      <el-button type="primary" @click="search" :icon="Search">查询</el-button>
      <el-button type="primary" @click="resetSearch" :icon="Refresh"
        >重置</el-button
      >
    </div>
    <div class="card-wrapper">
      <el-card
        v-for="item in tableData"
        :key="item.id"
        class="card-container"
        style="background-color: #eef8fd"
      >
        <!-- 卡片的头像和名称部分 -->
        <div class="card-header">
          <el-avatar :src="item.doctor.photo" :size="100" />
          <div style="margin: 10px 0"></div>
          <div class="card-info">
            <!-- 黑体粗体 -->
            <strong>{{ item.doctor.name }}</strong>
            ---
            <strong></strong>{{ item.doctor.subject.name }}
          </div>
          <div style="margin: 20px 0"></div>
          <strong>挂号日期；</strong>{{ item.workTime }}
        </div>

        <!-- 卡片的主要内容部分 -->
        <div class="card-body">
          <div class="amount">
            <strong>挂号费:</strong>
            <span style="color: red"
              >￥{{ item.doctor.money }}&nbsp;&nbsp;</span
            >
            <strong>剩余:</strong> {{ item.count }}
          </div>
          <p><strong>简介:&nbsp;&nbsp;&nbsp;</strong>{{ item.doctor.about }}</p>
        </div>

        <!-- 卡片的底部按钮 -->
        <div class="card-footer">
          <el-button type="primary" @click="operateBook">挂号</el-button>
        </div>
      </el-card>
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
          <el-table-column prop="doctor.name" label="医生姓名" width="120" />
          <el-table-column prop="doctor.photo" label="医生头像" width="100">
            <template #default="{ row }">
              <el-image
                style="width: 100%; height: 100%"
                :src="row.doctor.photo"
              />
            </template>
          </el-table-column>
          <el-table-column
            prop="doctor.subject.name"
            label="科室"
            width="110"
          />
          <el-table-column prop="workTime" label="预约日期" width="120" />
          <el-table-column prop="count" label="剩余个数" width="120" />
          <el-table-column prop="doctor.money" label="挂号费" width="100" />
          <el-table-column
            prop="doctor.about"
            label="医生简介"
            width="480"
            show-overflow-tooltip
          />
          <el-table-column
            prop="description"
            label="备注"
            width="300"
            show-overflow-tooltip
          />
          <el-table-column label="操作">
            <el-button type="primary" @click="operateBook"> 挂号 </el-button>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination" style="margin: 5px">
        <el-pagination
          v-model:current-page="pi.pageNo"
          v-model:page-size="pi.pageSize"
          :page-sizes="[5, 10]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pi.total"
          background
          @change="paginate"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import { Plus, Delete, Edit, Refresh, Search, Share, Upload, } from '@element-plus/icons-vue'
import { findAll as apiFindAll, addOrder as apiAddOrder } from '../api/ScheduleApi'
import { findSubNames } from '@/api/SubjectApi'
import { findDocNames } from '../api/DoctorApi'
import { nextTick, onMounted, ref, toRaw } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

const loading = ref(true)
const setLoading = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 2000)
}

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
  document.title = '患者挂号'
  loading.value = false
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
  doctorName: '',
  subId: '',
  workTime: "",
  count: "",
})
//查询表单实例引用
let searchFromRef
function resetSearch () {
  searchFromRef.resetFields()
}

const operateBook = () => {
  ElNotification({
    title: '非法操作',
    message: '只有患者可以挂号',
    type: 'warning',
  })
}

const doctorFormModel = ref({
  id: '',
  doctorId: '',
  doctor: {
    id: '',
    subId: '',
    name: '',
    about: '',
    subject: {
      id: "",
      name: "",
    },
    money: "",
  },
  workTime: "",
  description: "",
  count: "",
})
const sfm = doctorFormModel

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
.card-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 10px 0;
}

.card-container {
  flex: 1 1 calc(20% - 20px);
  box-sizing: border-box;
  max-width: calc(20% - 20px);
  min-width: 250px;
}

.card-header {
  text-align: center;
  align-items: center;
}

.card-body {
  margin-top: 20px;
  height: 250px;
  overflow: hidden;
}

.amount {
  text-align: center;
  font-size: 16px;
  margin-bottom: 10px;
}

.card-footer {
  text-align: right;
  margin-top: 20px;
}
</style>
<style>
.el-popper {
  max-width: 450px;
}
</style>

