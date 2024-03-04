<script setup xmlns="http://www.w3.org/1999/html">

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {get} from "@/net/index.js";

const form = reactive({
  password: '',
  new_password: '',
  repeat_new_password: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '')
    callback(new Error('请重复输入新密码'))
  else if (value !== form.new_password)
    callback(new Error('两次输入密码不一致'))
  else
    callback()
}

const rule = {
  password: [
    {required: true, message: '请输入旧密码', trigger: 'blur'}
  ],
  new_password: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: ['blur', 'change']}
  ],
  repeat_new_password: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    { validator: validatePassword, trigger: ['blur', 'change']}
  ]
}
const formRef = ref();
const valid = ref(false)
const onValidate = (prop, isValid) => {
  valid.value = isValid
}

function resetPassword(){
  formRef.value.validate(valid => {
    if (valid){
      post('/api/user/change-password',form, () => {
        ElMessage.success('修改密码成功!')
        formRef.value.resetFields();
        //是用于重置表单字段的操作。
        // 通常在密码修改成功后，为了清空当前表单中的密码输入框，以便用户输入新的密码，需要调用该方法将表单字段重置为空。
      })
    }
  })
}
const saving = ref(true)
const privacy = reactive({
  phone: false,
  email: false,
  qq: false,
  wx: false,
  gender: false
})

get('api/user/privacy', data => {
  privacy.phone = data.phone
  privacy.email = data.email
  privacy.qq = data.qq
  privacy.wx = data.wx
  privacy.gender = data.gender
  saving.value = false
})

function savePrivacy(type, status){
  saving.value = true
  post('api/user/save-privacy', {
    type: type,
    status: status
  }, () => {
    ElMessage.success('隐私设置成功!')
    saving.value = false;
  })
}
</script>

<template>
  <div style="margin: auto; max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里设置哪些内容可以被其他人看到, 请各位小伙伴注重自己的隐私" v-loading="saving">
        <div class="checkbox-list" style="width: 200px;">
          <el-checkbox @change="savePrivacy('phone', privacy.phone)" v-model="privacy.phone">公开展示我的手机号</el-checkbox>
          <el-checkbox @change="savePrivacy('email', privacy.email)" v-model="privacy.email">公开展示我的电子邮件地址</el-checkbox>
          <el-checkbox @change="savePrivacy('qq', privacy.qq)" v-model="privacy.qq">公开展示我的QQ号</el-checkbox>
          <el-checkbox @change="savePrivacy('wx', privacy.wx)" v-model="privacy.wx">公开展示我的微信号</el-checkbox>
          <el-checkbox @change="savePrivacy('gender', privacy.gender)" v-model="privacy.gender">公开展示我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0;" :icon="Setting" title="密码修改" desc="修改密码请在这里进行操作, 请务必牢记您的密码">
        <el-form label-width="100px" :model="form" :rules="rule" ref="formRef" @validate="onValidate">
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.password" placeholder="当前密码" maxlength="20"/>
          </el-form-item >
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password" placeholder="新密码" maxlength="20"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="repeat_new_password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.repeat_new_password" placeholder="重复输入新密码" maxlength="20"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button @click="resetPassword" :icon="Switch" type="success">立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list{
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>