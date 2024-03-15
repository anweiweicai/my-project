<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const weatherInfo = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const store = useStore()

const editor = ref(false)
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false, // 是否到底
  top: []
})

const collects = ref(false)

watch(() => topics.type, () => {
  resetList()
}, {immediate: true})

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

/**
 * 获取置顶
 */
get('/api/forum/top-topic', data => {
  topics.top = data
})

/**
 * 获取论坛列表
 */
function updateList(){
  if (topics.end) return
  get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
    if(data){
      data.forEach(d => topics.list.push(d))
      topics.page++
    }
    if (!data || data.length < 10) topics.end = true
  })
}

/**
 * 主题创建后, 重置列表
 */
function onTopicCreated(){
  editor.value = false
  resetList()
}

/**
 * 重置列表状态(属性)
 */
function resetList(){
  topics.page = 0
  topics.end = false
  topics.list = []
  updateList()
}

// 获取当前位置
navigator.geolocation.getCurrentPosition((position) => {
  const {latitude, longitude} = position.coords
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weatherInfo, data) // 将data对象中的所有属性复制到weather对象中
    weatherInfo.success = true
  }, error => {
    console.info(error)
    ElMessage.warning("位置信息获取超时, 请检查您的网络或重试")
    // 获取天气信息失败时, 给出默认位置的天气信息
    get('api/forum/weather?longitude=116.397428&latitude=39.90923', data => {
      Object.assign(weatherInfo, data) // 将data对象中的所有属性复制到weather对象中
      weatherInfo.success = true
    })
  }, {
    // 设置超时
    timeout: 3000,
    enableHighAccuracy: true //表示浏览器将尽可能地提供高精度的位置信息，即在尽量减小定位误差的情况下获取设备的位置信息
  })
})

</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon>
          点击发表主题...
        </div>
        <div style="margin-top: 10px; display: flex; gap: 13px; font-size: 18px; color: grey">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </light-card>
      <light-card style="margin-top: 10px; display: flex; flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic"
             @click="router.push('/index/topic-detail/' + item.id)" style="">
          <el-tag type="info" size="small" style="color: white; background-color: rgba(185,23,2,1.02); font-weight: bold">置顶</el-tag>
          <div>{{ item.title }}</div>
          <div>{{ new Date(item.time).toLocaleDateString() }}</div>
        </div>
      </light-card>
      <light-card style="margin-top: 10px; display: flex; gap: 8px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types" @click="topics.type = item.id">
          <color-dot :color="item.color"></color-dot>
          <span style="margin-left: 11px">{{ item.name }}</span>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list?.length">
          <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px"
               v-infinite-scroll="updateList">
            <light-card  v-for="item in topics.list" class="topic-card"
                         @click="router.push('/index/topic-detail/' + item.id)">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                </div>
                <div style="margin-left: 7px; ">
                  <div style="font-size: 13px; font-weight: bold">{{item.username}}</div>
                  <div style="font-size: 12px; color: grey">
                    <el-icon><Clock/></el-icon>
                    <div style="margin-left: 2px; display: inline-block; transform: translateY(-2px)">{{new Date(item.time).toLocaleString()}}</div>
                  </div>
                </div>
              </div>
              <div  v-if="store.findTypeById(item.type)" style="margin-top: 5px">
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold; margin-left: 7px">{{ item.title }}</span>
              </div>
              <div class="topic-content">{{ item.text }}</div>
              <div style="display: grid; grid-template-columns: repeat(3, 1fr); grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.image" :src="`${img}`" ></el-image>
              </div>
              <div style="display: flex; gap: 20px; font-size: 13px; margin-top: 10px; opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle"><CircleCheck/></el-icon>{{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star/></el-icon>{{item.collect}}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon> 查看我的收藏</span>
            <el-icon style="transform: translateY(4px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>
        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px; margin: 10px; color: grey">
            a';lsijfoiasdjf;lasdjfkals;flk
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <weather :data="weatherInfo"/>
        </light-card>
        <light-card>
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px; margin-top: 10px; color: grey">
          <el-icon><Link /></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid; grid-template-columns: repeat(2, 1fr); grid-gap: 10px; margin-top: 10px">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://www.itbaima.cn/image/space/default-banner.jpeg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://www.itbaima.cn/image/space/background/light/3.webp"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://www.itbaima.cn/image/space/background/light/6.jpeg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://www.itbaima.cn/image/space/background/light/7.jpeg"/>
          </div>
        </div>
      </div>
    </div>
    <topic-editor :show="editor" @success="onTopicCreated"  @close="editor = false"/>
    <topic-collect-list :show="collects" @close="collects = false"></topic-collect-list>
  </div>

</template>

<style lang="less" scoped>
.top-topic{
  display: flex;
  transition: scale .3s;

  div:first-of-type{
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
  }

  div:nth-of-type(2){
    flex:1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }
  &:hover{
    cursor: pointer;
    scale: 1.02;
  }
}

.type-select-card{
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 5px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active{
    border: solid 1px #ead4c4
  }
  &:hover{
    cursor: pointer;
    background-color: #dadada;
  }
}

.create-topic{
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover{
    cursor: pointer;
  }
}
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover{
    cursor: pointer;
    opacity: 0.6;
  }
}
.topic-card{

  padding: 15px;
  transition: .3s;

  &:hover{
    scale: 1.015;
    background-color: #efefef;
    cursor: pointer;
  }
  .topic-content{
    font-size: 13px;
    color: grey;
    margin: 10px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image{
    width: 100%;
    height: 100%;
    max-height: 120px;
    border-radius: 10px;
  }
}

.info-text{
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.friend-link{
  border-radius: 5px;
  overflow: hidden;
}

.dark {
  .create-topic{
    background-color: #232323;
  }
  .type-select-card{
    background-color: #282828;

    &.active{
      border: solid 1px #64594b
    }
    &:hover{
      background-color: #5e5e5e;
    }
  }
  .topic-card{
    &:hover{
      background-color: #403e3e;
    }
  }
}



</style>