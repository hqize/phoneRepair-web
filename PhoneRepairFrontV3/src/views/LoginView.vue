<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { useCaptcha } from '@/composables/useCaptcha'
import http from '@/api/request'
import type { Result } from '@/types/api'
import type { LoginUser } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  usernameOrEmail: '',
  password: '',
  captchaInput: '',
})
const canvasEl = ref<HTMLCanvasElement | null>(null)
const { captchaText, refresh } = useCaptcha(canvasEl)

async function handleLogin() {
  const { usernameOrEmail, password, captchaInput } = form.value
  if (!usernameOrEmail || !password || !captchaInput) {
    ElMessage.error('用户的输入框必填项不能为空')
    return
  }
  if (usernameOrEmail.length > 40 || password.length >= 20 || captchaInput.length > 6) {
    ElMessage.error('数据长度过长不符合使用规范')
    return
  }
  if (password.length < 6 || /\s|=/.test(password)) {
    ElMessage.error('数据长度过长不符合使用规范 或包含了特殊字符')
    return
  }
  if (captchaInput.toLowerCase() !== captchaText.value.toLowerCase()) {
    ElMessage.error('验证码错误，请重新输入！')
    refresh()
    return
  }
  try {
    const res = await http.post<Result<LoginUser>>('/user/login', null, {
      params: { usernameOrEmail, password },
    })
    userStore.setUser(res.data.data)
    ElMessage.success('登录成功！')
    router.push('/reception')
  } catch {
    // handled by interceptor
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-form-wrap">
      <img src="/static/img/logo.jpg" alt="Logo" class="login-logo" />
      <h2>登录到您的账户</h2>
      <el-form @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.usernameOrEmail" placeholder="请输入用户名或邮箱" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <div class="captcha-row">
            <el-input v-model="form.captchaInput" placeholder="请输入验证码" style="flex: 1" />
            <canvas ref="canvasEl" @click="refresh" class="captcha-canvas" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <p class="agree-text">登录即代表你同意了我们的<a href="#">《用户服务规范》</a></p>
        </el-form-item>
        <el-form-item>
          <p class="footer-text">
            没有账号？<router-link to="/register">立即注册</router-link>
          </p>
          <p class="forgot-text">忘记密码？<a href="#">找回密码</a></p>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
}
.login-bg {
  flex: 1;
  min-height: 100vh;
  background-image: url('/static/img/background_login.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.login-form-wrap {
  width: 400px;
  padding: 30px;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin: auto 20px;
  text-align: center;
}
.login-logo { width: 100px; height: auto; margin-bottom: 20px; }
h2 { color: #333; margin-bottom: 20px; }
.login-form-wrap :deep(.el-button) { width: 100%; }
.login-form-wrap :deep(.el-form-item) { text-align: center; margin-bottom: 18px; }
.login-form-wrap :deep(.el-form-item__content) { display: block; }
.agree-text { margin-top: 0; font-size: 12px; color: #666; }
.footer-text { margin-top: 20px; font-size: 12px; color: #666; }
.forgot-text { margin-top: -25px; font-size: 12px; color: #666; }
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
