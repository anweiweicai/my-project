<script setup>
import {reactive, ref} from "vue";
import {get, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {
  Back,
  Bell,
  ChatDotSquare, Check, Collection, DataLine,
  Document, Files,
  Location, Lock, Message, Monitor,
  Notification, Operation,
  Position,
  School, Search,
  Umbrella, User
} from "@element-plus/icons-vue";
import LightCard from "@/components/LightCard.vue";
import {ElNotification} from "element-plus";

const store = useStore()
const loading = ref(true)
const searchInput = reactive({
  type: '1',
  text: ''
})

get('/api/user/info', (data) => {
  store.user = data
  loading.value = false
})

function userLogout(){
  logout(() => {router.push('/')},)
}

const notification = ref([])
const loadNotification = () => {
  get('/api/notification/list', data => {
    notification.value = data
    if (data.length > 0){
      for (let i = 0; i < (data.length < 5 ? data.length : 5); i++){
        setTimeout(() => {
          ElNotification({
            title: notification.value[i].title,
            message: notification.value[i].content,
            type: notification.value[i].type,
            offset: 50,
            duration: 4000,
            onClick: () => {
              confirmNotification(notification.value[i].id, notification.value[i].url)
            }
          })
        },100 * i);
      }
    }
  })
}
loadNotification()

function confirmNotification(id, url){
  get('/api/notification/delete?id='+id, () => {
    loadNotification()
    window.open(url)
  })
}

function deleteAllNotification(){
  get('api/notification/delete-all', loadNotification)
}

function notificationBox(){
  ElNotification({
    title: notification.value[i].title,
    message: notification.value[i].content,
    type: notification.value[i].type,
    offset: 50,
    duration: 4000,
    onClick: () => {
      confirmNotification(notification.value[i].id, notification.value[i].url)
    }
  })
}
</script>

<template>

  <div class="main-content" v-loading="loading" element-loading-text="正在进入, 请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex: 1; padding: 0 20px; text-align: center">
          <el-input v-model="searchInput.text" style="width: 100%; max-width: 500px" placeholder="搜索论坛相关内容...">
            <template #prefix>
              <el-icon><search/></el-icon>
            </template>
            <template #append>
              <el-select style="width: 120px" v-model="searchInput.type" >
                <el-option value="1" label="帖子广场"/>
                <el-option value="2" label="校园活动"/>
                <el-option value="3" label="表白墙"/>
                <el-option value="4" label="教务通知"/>
              </el-select>
            </template>
          </el-input>
        </div>
        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge is-dot style="margin-right: 15px" :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 10px">通知</div>
                </div>
              </el-badge>
            </template>
              <el-empty :image-size="80" description="暂时没有未读消息哦~" v-if="!notification.length"/>
              <el-scrollbar :max-height="500" v-else>
                <light-card v-for="item in notification" class="notification-item"
                            @click="confirmNotification(item.id, item.url)">
                  <div>
                    <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                    <span style="font-weight: bold">{{item.title}}</span>
                  </div>
                  <el-divider style="margin: 7px 0 3px 0"/>
                  <div style="font-size: 13px; color: grey">
                    {{item.content}}
                  </div>
                </light-card>
                <div style="margin-top: 10px;">
                  <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                             style="width: 100%" plain>清除全部未读消息</el-button>
                </div>
              </el-scrollbar>
          </el-popover>
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-item @click="router.push('/index/user-setting')">
                <el-icon><operation/></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><message/></el-icon>
                消息列表
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon><back/></el-icon>
                退出登录
              </el-dropdown-item>
            </template>

          </el-dropdown>

        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu router style="min-height: calc(100vh - 55px)" :default-active="$route.path" :default-openeds="['1','2','3']">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index">
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
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><user/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting" >
                  <template #title>
                    <el-icon><lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <router-view v-slot="{Component}">
              <transition name="el-fade-in-linear" mode="out-in">
                <component :is="Component" style="height: 100%"/>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>

:deep(.el-notification.right){
  float: right;
  transition: 1s;
}

.main-content{
  height: 100vh;
  width: 100vw;
}

.notification{
  font-size: 22px;
  line-height: 14px;
  text-align: center;

  &:hover{
    color: grey;
    cursor: pointer;
  }
}
.notification-item{
  transition: .3s;
  &:hover{
    opacity: 0.8;
    cursor: pointer;
    scale: 1.02;
  }
}

.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}
.dark .main-content-page{
  background-color: #242628;
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
    .el-avatar:hover {
      cursor: pointer;
    }

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