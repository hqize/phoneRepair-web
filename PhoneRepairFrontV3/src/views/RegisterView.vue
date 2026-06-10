<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCaptcha } from '@/composables/useCaptcha'
import * as userApi from '@/api/user'

const router = useRouter()

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captchaInput: '',
})
const canvasEl = ref<HTMLCanvasElement | null>(null)
const { captchaText, refresh } = useCaptcha(canvasEl)

async function handleRegister() {
  const { username, email, password, confirmPassword, captchaInput } = form.value

  if (!username || !email || !password || !confirmPassword) {
    ElMessage.error('用户名、邮箱、密码、确认密码不能为空！')
    return
  }
  if (!captchaInput) {
    ElMessage.error('验证码不能为空！')
    return
  }
  if (username.length < 4 || username.length > 30) {
    ElMessage.error('用户名长度必须在4到30个字符之间！')
    return
  }
  if (password.length < 6 || password.length > 30 || /\s|=/.test(password)) {
    ElMessage.error('密码长度必须在6到30个字符之间！或者包含特殊字符')
    return
  }
  if (password !== confirmPassword) {
    ElMessage.error('确认密码和密码不一致！')
    return
  }
  if (captchaInput.toLowerCase() !== captchaText.value.toLowerCase()) {
    ElMessage.error('验证码错误，请重新输入！')
    refresh()
    return
  }
  try {
    await userApi.register({
      userName: username,
      userEmail: email,
      userPasswordHash: password,
    })
    ElMessage.success('注册成功！')
    router.push('/login')
  } catch {
    // handled by interceptor
  }
}
</script>

<template>
  <div class="register-container">
    <div class="register-bg"></div>
    <div class="register-form-wrap">
      <img src="/static/img/logo.jpg" alt="Logo" class="register-logo" />
      <h2>注册账户</h2>
      <el-form @keyup.enter="handleRegister">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" />
        </el-form-item>
        <el-form-item>
          <div class="captcha-row">
            <el-input v-model="form.captchaInput" placeholder="请输入验证码" style="flex: 1" />
            <canvas ref="canvasEl" @click="refresh" class="captcha-canvas" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <p class="agree-text">注册即代表你同意了我们的<a href="#">《用户服务规范》</a>、<a href="#">《用户服务隐私规范》</a></p>
        </el-form-item>
        <el-form-item>
          <p class="footer-text">已有账号？<router-link to="/login">立即登录</router-link></p>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  min-height: 100vh;
}
.register-bg {
  flex: 1;
  min-height: 100vh;
  background-image: url('/static/img/background_login.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.register-form-wrap {
  width: 400px;
  padding: 30px;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin: auto 20px;
  text-align: center;
}
.register-logo { width: 100px; height: auto; margin-bottom: 20px; }
h2 { color: #333; margin-bottom: 20px; }
.register-form-wrap :deep(.el-button) { width: 100%; }
.register-form-wrap :deep(.el-form-item) { text-align: center; margin-bottom: 18px; }
.register-form-wrap :deep(.el-form-item__content) { display: block; }
.agree-text { margin-top: 0; font-size: 12px; color: #666; }
.footer-text { margin-top: 20px; font-size: 12px; color: #666; }
.captcha-row { display: flex; align-items: center; gap: 10px; }
.captcha-canvas {
  cursor: pointer;
  user-select: none;
  border-radius: 4px;
  border: 1px solid #ddd;
  width: 130px;
  height: 45px;
}
</style>
