<script setup>
import 'qweather-icons/font/qweather-icons.css' // 引入和风天气图标

defineProps({
  data: Object
})
</script>

<template>
  <div style="height: 160px" v-loading="!data.success"
       element-loading-text="正在获取天气信息..." v-if="data.success">
    <div style="display: flex; justify-content: space-between; margin: 10px 20px">
      <div style="font-size: 45px">
        <i :class="`qi-${data.now.icon}`"></i>
      </div>
      <div style="font-weight: bold; text-align: center">
        <div style="font-size: 25px">{{ data.now.temp }}℃</div>
        <div style="font-size: 15px;">{{data.now.text}}</div>
      </div>
      <div style="margin: 12px">
        <div style="font-size: 15px">{{ `${data.location.country} ${data.location.adm1}` }}</div>
        <div style="font-size: 14px; color: grey">{{ `${data.location.adm2} ${data.location.name}区`}} </div>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <!--grid-template-columns: repeat(5, 1fr);：该属性定义了网格布局的列模板，其中repeat(5, 1fr)指定了创建5个等宽的列，每个列的宽度为网格容器的可用空间的1/5。-->
    <div style="display: grid; grid-template-columns: repeat(5, 1fr); text-align: center">
      <div v-for="item in data.hourly">
        <div style="font-size: 13px">{{new Date(item.fxTime).getHours()}}时</div>
        <div style="font-size: 23px">
          <i :class="`qi-${item.icon}-fill`"></i>
        </div>
        <div style="font-size: 12px">{{ item.temp }}℃</div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>