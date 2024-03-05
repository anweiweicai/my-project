<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed} from "vue";

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth()} 月 ${date.getDay()} 日`
})

</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic">
          <el-icon><EditPen/></el-icon>
          点击发表主题...
        </div>
      </light-card>
      <light-card style="margin-top: 10px; height: 30px">

      </light-card>
      <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px">
        <light-card style="height: 150px" v-for="item in 10">

        </light-card>
      </div>
    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
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
          <weather/>
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
  </div>

</template>

<style lang="less" scoped>
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
</style>