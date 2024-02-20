<script setup>

import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const coldTime = ref(0)
const formRef = ref()

const active = ref(0)

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '')
    callback(new Error('请重复输入密码'))
  else if (value !== form.password)
    callback(new Error('两次输入密码不一致'))
  else
    callback()
}

const rule = {
  password: [
    { required: true, message: '请输入密码', trigger: ['blur']},
    { min: 6, max: 20, message: '用户名长度必须在6-20个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change']}
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: ['blur']},
    { type: 'email', message:'请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: ['blur']},
  ]
}

function askCode(){
  if (isEmailValid){
    coldTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=reset`, () => {
      ElMessage.success(`验证码已发送到邮箱: ${form.email}, 请注意查收`)
      const intervalId = setInterval(() => {//设置计时器, 每秒-1
        if (coldTime.value > 0) {
          coldTime.value--;
        } else {
          clearInterval(intervalId);
        }
      }, 1000);
    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
    })
  }else {
    ElMessage.warning(`请输入正确的电子邮件地址!`)
  }
}

const isEmailValid = computed(() =>  /^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))

function confirmRest(){
  formRef.value.validate( valid => {
    if (valid) {
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, ()=> active.value++)
    }else {
      ElMessage.warning("请将表单内容填写完整")
    }
  })
}

function doRest(){
  formRef.value.validate((valid) => {
    if (valid){
      post('api/auth/reset-password', {...form}, () => {
        ElMessage.success('密码重置成功, 请重新登录')
        router.push('/')
      })
    }else {
      ElMessage.warning("请将表单内容填写完整")
    }
  })
}

</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 30px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件"/>
        <el-step title="重新设置密码"/>
      </el-steps>  <!-- 步骤条-->
    </div>
    <div style="margin: 0 20px" v-if="active === 0">
      <div style="margin-top: 80px">
        <div style="font-size: 25px; font-weight: bold">重置密码</div>
        <div style="font-size: 14px; color: grey">请输入需要重置密码的电子邮件地址</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item  prop="email">
            <el-input v-model="form.email" placeholder="电子邮件地址">
              <template #prepend><el-icon><Message/></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="17">
                <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                  <template #prepend><el-icon><EditPen/></el-icon></template>
                </el-input>
              </el-col>
              <el-col :span="5" >
                <el-button @click="askCode" :disabled="!isEmailValid || coldTime" type="success" >
                  {{ coldTime > 0 ? `请稍后 ${coldTime} 秒` : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="confirmRest" style="width: 250px" type="warning" plain>开始重置密码</el-button>
      </div>
    </div>
    <div style="margin: 0 20px" v-if="active === 1">
      <div style="margin-top: 80px">
        <div style="font-size: 25px; font-weight: bold">重置密码</div>
        <div style="font-size: 14px; color: grey">请输入你的新密码, 务必牢记, 防止丢失</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item prop="password">
            <el-input  v-model="form.password" maxlength="20" type="password" placeholder="密码">
              <template #prepend><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password_repeat">
            <el-input  v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
              <template #prepend><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="doRest" style="width: 250px" type="warning" plain>立即重置密码</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>