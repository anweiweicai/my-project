<script setup xmlns="http://www.w3.org/1999/html">

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
</script>

<template>
  <div style="margin: auto; max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里设置哪些内容可以被其他人看到, 请各位小伙伴注重自己的隐私">
        <div class="checkbox-list">
          <el-checkbox>公开展示我的手机号</el-checkbox>
          <el-checkbox>公开展示我的电子邮件地址</el-checkbox>
          <el-checkbox>公开展示我的QQ号</el-checkbox>
          <el-checkbox>公开展示我的微信号</el-checkbox>
          <el-checkbox>公开展示我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0;" :icon="Setting" title="密码修改" desc="修改密码请在这里进行操作, 请务必牢记您的密码">
        <el-form label-width="100px">
          <el-form-item label="当前密码">
            <el-input :prefix-icon="Lock" placeholder="当前密码" maxlength="20"/>
          </el-form-item >
          <el-form-item label="新密码">
            <el-input :prefix-icon="Lock" placeholder="新密码" maxlength="20"/>
          </el-form-item>
          <el-form-item label="重复新密码">
            <el-input :prefix-icon="Lock" placeholder="重新输入新密码" maxlength="20"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" type="success">立即重置密码</el-button>
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