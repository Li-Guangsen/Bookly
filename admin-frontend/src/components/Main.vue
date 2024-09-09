<template>
  <el-container class="outer" v-loading="loading">
    <!-- 侧栏 -->
    <el-aside class="aside"
      ><el-menu
        active-text-color="#1890ff"
        background-color="#545c64"
        :default-active="pathname"
        text-color="#fff"
        style="border-right: none"
        :router="true"
      >
        <template v-for="menu in menus">
          <el-sub-menu
            v-if="menu.children"
            :index="menu.url"
            :key="'m-' + menu.url"
          >
            <template #title>
              <span
                ><el-icon
                  ><component
                    class="icons"
                    :is="menu.icon"
                    style="width: 1em; height: 1em; margin-right: 8px"
                  >
                  </component></el-icon
                >{{ menu.title }}</span
              >
            </template>
            <el-menu-item
              v-for="item in menu.children"
              :index="item.url"
              :key="item.url"
              ><el-icon
                ><component
                  class="icons"
                  :is="item.icon"
                  style="width: 1em; height: 1em; margin-right: 8px"
                >
                </component></el-icon
              >{{ item.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.url" :key="menu.url">
            <el-icon
              ><component
                class="icons"
                :is="menu.icon"
                style="width: 1em; height: 1em; margin-right: 8px"
              >
              </component> </el-icon
            >{{ menu.title }}
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <!-- 头部 -->
      <el-header class="header"
        ><div class="logo"></div>
        <div class="title"><h1>书城管理系统</h1></div>
        <div class="session">
          <el-menu
            :default-active="activeIndex"
            background-color="white"
            style="border-bottom: 1px solid #ebeef5"
            class="el-menu-demo"
            mode="horizontal"
            :ellipsis="false"
            @select="userMenuSelect"
          >
            <el-sub-menu index="uesr">
              <template #title>
                <el-icon><Avatar /></el-icon>
                {{ username }}
              </template>
              <el-menu-item index="2-1">个人信息</el-menu-item>
              <el-menu-item index="2-2">修改密码</el-menu-item>
              <el-menu-item index="logout">退出登录</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </div>
      </el-header>
      <!-- 主体 -->
      <el-main class="main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<style scoped>
.outer {
  height: 100%;
}
.aside {
  width: 200px;
  background-color: #545c64;
}
.header {
  height: 60px;
  background-color: white;
  /* background-color: #f3fcfe; */
  border-bottom: 1px solid #ebeef5;
  display: flex;
  flex-direction: row;
  padding-left: 6px;
  padding-right: 0px;
}
.header > .title {
  flex-grow: 1;
}
.header > .logo {
  width: 120px;
  flex-shrink: 0;
  box-sizing: border-box;
  padding: 5px;
  background: url("/logo-modified.png") no-repeat center center/contain;
}
.header > .session {
  height: 100%;
  width: 150px;
  flex-shrink: 0;
  z-index: 999;
}
.admin-img {
  top: -8%;
}
.main {
}
</style>
<script setup>
import { ref, onMounted } from 'vue'
import { Setting, AlarmClock, Menu, CollectionTag, Handbag, Memo, Grid, User, Avatar, Medal, List, TrendCharts, Notebook, Lock, OfficeBuilding, Finished } from '@element-plus/icons-vue'
import { getUsername } from '../api/AdminApi'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(true)
onMounted(async () => {
  username.value = "admin"
  // console.log(username)
  // console.log(new URL(window.location.href).pathname)
  loading.value = false
})
// 侧栏菜单数据
const menus = ref([
  { icon: TrendCharts, title: '数据看板', url: '/main/dashboard' },
  {
    icon: Handbag,
    title: '商品管理',
    url: '/main',
    children: [
      { icon: Menu, title: '商品分类', url: '/main/category' },
      { icon: Memo, title: '商品列表', url: '/main/product' },
    ]
  }, {
    icon: AlarmClock,
    title: '秒杀管理',
    url: '/main/doctor'
  }, {
    icon: List,
    title: '订单管理',
    url: '/main/order'
  },
  {
    icon: Grid,
    title: '人员管理',
    children: [
      { icon: User, title: '用户管理', url: '/main/user' },
      {
        icon: Lock, title: '管理员管理', url: '/main/admin'
      },
    ]
  }])
const router = useRouter()
const username = ref()
const pathname = ref(new URL(window.location.href).pathname)
function userMenuSelect (menu) {
  if (menu === 'logout') {
    // console.log('logout')
    removeJwt()
    router.push('/empty')
  }
  else {
    operateAdmin()
  }
}
function operateAdmin () {
  ElMessageBox.alert('您可以到管理员管理页面进行操作', '用户操作', {
    confirmButtonText: 'OK',
  })
}

</script>
