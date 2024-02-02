<script setup>
import {reactive} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const form = reactive ({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const rule = {

}

</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px; color: grey">欢迎注册本网站, 请在下方填写相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" rules="rule">
        <el-form-item>
          <el-input v-model="form.username" maxlength="15" type="text" placeholder="用户名">
            <template #prepend><el-icon><User/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input  v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prepend><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input  v-model="form.password" maxlength="20" type="password" placeholder="重复密码">
            <template #prepend><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" maxlength="15" type="text" placeholder="电子邮件地址">
            <template #prepend><el-icon><Message/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                <template #prepend><el-icon><EditPen/></el-icon></template>
              </el-input>
            </el-col>
            <el-col :span="5" >
              <el-button type="success" >获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 30px">
      <el-button type="warning" style="width: 250px" plain>立即注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px; line-height: 15px; color: grey">已有账号? </span>
      <el-link style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>