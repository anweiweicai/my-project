<script setup>
import {Lock, User} from "@element-plus/icons-vue"
import {reactive} from "vue";

const form = reactive({
  username: '',
  password: '',
  remember: false
})
</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 30px; font-weight: bold">登录</div>
      <div style="font-size: 14px; color: grey">在进入系统之前, 请先输入用户名和密码进行登录</div>
    </div>
    <div style="margin-top: 50px">
      <el-form v-model="form">
        <el-form-item>
          <el-input v-model="form.username" maxlength="15" type="text" placeholder="用户名或邮箱">
            <template #prepend><el-icon><User/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" maxlength="20" type="text" placeholder="密码">
            <template #prepend><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item>
              <el-checkbox v-model="form.remember" label="记住我" />
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right; margin-top: 4px">
              <el-link>忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 15px">
<!--      plain 鼠标未悬浮时是浅颜色    -->
      <el-button style="width: 250px" type="success" plain>立即登录</el-button>
    </div>
    <el-divider>
      <span style="font-size: 13px; color: grey">没有账号?</span>
    </el-divider>
    <div>
      <el-button style="width: 250px" type="warning" plain>立即注册</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>