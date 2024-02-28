<script setup>
import {ref} from "vue";
import {get, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";

const store = useStore()
const loading = ref(true)

get('/api/user/info', (data) => {
  store.user = data
  loading.value = false
})

function userLogout(){
  logout(() => {router.push('/')},)
}
</script>

<template>

  <div class="main-content" v-loading="loading" element-loading-text="正在进入, 请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex: 1" class="user-info">
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
    <el-button @click="userLogout">退出登录</el-button>
  </div>
</template>

<style scoped>
.main-content{
  height: 100vh;
  width: 100vw;
}

.main-content-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;/* 上下居中 */
  box-sizing: border-box;
  /* 元素的宽度和高度会包括内容、内边距和边框，而不会增加到额外的值。 */
  /* 这样设置可以更方便地控制元素的尺寸，避免因为内边距和边框的增加而导致元素溢出或布局混乱。 */
  .logo{
    height: 32px;
  }

  .user-info{
    display: flex;
    justify-content: flex-end;
    align-items: center;
    .profile{
      text-align: right;
      margin-right: 20px;

      :first-child{
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      :last-child{
        font-size: 13px;
        color: grey;
      }
    }
  }
}

</style>