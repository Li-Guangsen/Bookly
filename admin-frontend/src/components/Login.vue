<template>
  <div class="login-container">
    <el-card class="login-box" shadow="hover">
      <div class="logo"></div>
      <h2>汉东省人民医院</h2>
      <h2>Welcome back</h2>
      <div style="margin-bottom: 20px"></div>
      <el-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        label-width="0"
      >
        <el-form-item
          prop="username"
          label="用户名"
          :label-width="80"
          style="margin-left: 40px"
        >
          <el-input
            v-model="loginForm.username"
            placeholder="Username"
            :prefix-icon="User"
            style="width: 300px; margin: 0 120px 0 0"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="密码:"
          prop="password"
          :label-width="60"
          style="margin-left: 60px"
        >
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="Password"
            :prefix-icon="Key"
            style="width: 300px; margin: 0 120px 0 0"
            show-password
          ></el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item
              label="验证码:"
              prop="captcha"
              :label-width="80"
              style="margin-left: 40px"
            >
              <el-input
                placeholder="验证码"
                v-model="loginForm.captcha"
                maxlength="4"
                minLength="4"
                :prefix-icon="PictureRounded"
                style="width: 300px; margin: 0 0"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <img
              class="captcha"
              :src="captchaUrl"
              style="height: 30px"
              @click="refresh"
            />
          </el-col>
        </el-row>

        <el-form-item style="margin-left: 120px; margin-top: 10px">
          <el-button type="primary" @click="handleLogin" style="width: 220px"
            >Login</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Setting, User, PictureRounded, Lock, Key } from '@element-plus/icons-vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElRow, ElCol, ElMessage } from "element-plus";
import { login } from "../api/AdminApi";
import { save as saveToken } from "../api/JwtApi";
import { useRouter } from "vue-router";
onMounted(async () => {
  document.title = '登录'
})
const loginForm = ref({
  username: '',
  password: '',
  captcha: ''
})
let loginFormRef;
const rules = ref({
  username: [
    { required: true, message: "用户名不可为空" }
  ],
  password: [
    { required: true, message: "密码不可为空" }
  ],
  captcha: [
    { required: true, message: "验证码不可为空" },
    { min: 4, max: 4, message: "验证码只能是4位" }
  ]
});
const router = useRouter();
async function handleLogin () {
  loginFormRef.validate(async success => {
    if (success) {
      let resp = await login(loginForm.value);
      // console.log(resp);
      if (resp.success) {
        saveToken(resp.jwt);
        //转发到另一个路由
        router.push("/main/dashboard");
      } else {
        ElMessage({
          message: resp.error,
          type: "error"
        });
      }
    }
  });
}
const captchaUrl = ref("/api/users/captcha");
function refresh () {
  captchaUrl.value = "/api/users/captcha?id=" + Math.random();
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("/login_bg.jpg") no-repeat center 60% / cover;
}

.login-box {
  width: 500px;
  padding: 20px;
  border-radius: 10px;
}
.login-box h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
.logo {
  height: 100px;
  flex-shrink: 0;
  box-sizing: border-box;
  padding: 5px;
  background: url("/logo-modified.png") no-repeat center center/contain;
}
.captcha {
  width: 110px;
  cursor: pointer;
  /* margin-left: 130px; */
  vertical-align: middle;
}
</style>
