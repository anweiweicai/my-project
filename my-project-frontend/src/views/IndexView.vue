<script setup>
import {ref} from "vue";
import {get, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {
  Bell,
  ChatDotSquare, Collection, DataLine,
  Document, Files,
  Location, Lock, Monitor,
  Notification, Operation,
  Position,
  School,
  Umbrella, User
} from "@element-plus/icons-vue";

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
          <div>
            <el-button @click="userLogout">退出登录</el-button>
          </div>
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu style="min-height: calc(100vh - 55px)" default-active="1-1">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="1-1">
                  <template #title>
                    <el-icon><chat-dot-square/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><notification/></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><school/></el-icon>
                    海文考研
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><position/></el-icon>
                  <span><b>探索与发现</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><document/></el-icon>
                    成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><files/></el-icon>
                    班级课程表
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><monitor/></el-icon>
                    教务通知
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><collection/></el-icon>
                    在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><data-line/></el-icon>
                    预约教室
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><operation/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><user/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
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