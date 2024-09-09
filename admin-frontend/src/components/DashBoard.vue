<template>
  <div class="outer" v-loading="loading">
    <el-row>
      <el-col :span="12" class="left">
        <div id="c1" class="card">
          <h2>总数据统计</h2>
          <div>
            <div class="item">
              <h3>患者数</h3>
              <div>
                {{ patient_count }}
              </div>
            </div>
            <div class="item">
              <h3>医生数</h3>
              <div>
                {{ doctor_cout }}
              </div>
            </div>
            <div class="item">
              <h3>科室数</h3>
              <div>
                {{ subject_count }}
              </div>
            </div>
            <div class="item">
              <h3>挂号数</h3>
              <div>
                {{ order_count }}
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12" class="right">
        <div id="c2" class="card">
          <h2>本周工作医生占比</h2>
          <div class="charts" ref="c2Ref"></div>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12" class="bottom">
        <div id="c4" class="card">
          <div class="charts" ref="c4Ref"></div>
        </div>
      </el-col>
      <el-col :span="12" class="bottom">
        <div id="c3" class="card">
          <h2>本周预约数趋势</h2>
          <div class="charts" ref="c3Ref"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { OrderCount, PatientCount, SubjectCount, DoctorCount, doctorOrderCount, subjectOrderCount, workDateOrderCount } from '../api/DashBoardApi';
import * as echarts from 'echarts';
const patient_count = ref(0)
const doctor_cout = ref(0)
const subject_count = ref(0)
const order_count = ref(0)
const loading = ref(true)
//第1步：获取要显示图表的dom
let c2Ref
let c3Ref
let c4Ref
onMounted(async () => {
  document.title = '数据看板'
  patient_count.value = await PatientCount()
  doctor_cout.value = await DoctorCount()
  subject_count.value = await SubjectCount()
  order_count.value = await OrderCount()
  //第2步：创建echarts实例
  let chart2 = echarts.init(c2Ref)
  let chart3 = echarts.init(c3Ref)
  let chart4 = echarts.init(c4Ref)
  await doctorOrder()
  await workDateOrder()
  await subjectOrder()
  //第4步：设置图表配置项
  chart2.setOption(c2Opts.value)
  chart3.setOption(c3Opts.value)
  chart4.setOption(c4Opts.value)
  loading.value = false
});
//第3步：编写配置项
let c2Opts = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    left: 'center'
  },
  series: [
    {
      name: '医生挂号数',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      padAngle: 5,
      itemStyle: {
        borderRadius: 10
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: []
    }
  ]
});
let c3Opts = ref({
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [],
      type: 'bar'
    }
  ]
})
let c4Opts = ref({
  title: {
    text: '科室挂号总数',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '科室挂号数',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

async function doctorOrder () {
  let resp = await doctorOrderCount()
  // console.log(resp)
  c2Opts.value.series[0].data = resp.data
}
async function subjectOrder () {
  let resp = await subjectOrderCount()
  c4Opts.value.series[0].data = resp.data
}
async function workDateOrder () {
  let resp = await workDateOrderCount()
  c3Opts.value.series[0].data = resp.data

}
</script>
<style scoped>
.outer {
  height: 100%;
  /* background-color: aliceblue; */
}
.left,
.right,
.bottom {
  box-sizing: border-box;
}
.card {
  background-color: white;
}

.card > h2 {
  font-size: 20px;
  font-weight: normal;
  margin: 0;
  box-sizing: border-box;
  padding: 10px;
}
#c1,
#c2,
#c3,
#c4 {
  height: 400px;
  background-color: white;
  margin: 5px 20px;
  /* border: 1px solid #ccc; */
}
#c4 {
  box-sizing: border-box;
  padding-top: 50px;
}
#c1 > div {
  display: flex;
  justify-content: space-around;
  height: 120px;
  margin-top: 20px;
}

#c1 > div > .item > h3 {
  margin: 0;
  font-size: 20px;
  text-align: center;
  margin-bottom: 10px;
  font-weight: normal;
}

#c1 > div > .item > div {
  width: 90px;
  height: 90px;
  border: 20px solid aquamarine;
  border-radius: 90px;
  text-align: center;
  line-height: 90px;
  font-size: 30px;
}
.charts {
  /* background-color: pink; */
  height: 350px;
}
</style>
