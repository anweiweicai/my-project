import {createApp} from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8080'

const app = createApp(App)//创建了一个Vue应用实例`app`,在这个实例上面对应用进行配置和添加插件

app.use(router)//安装Vue Router插件, 以便在应用中进行页面路由和导航的功能

app.mount('#app')//创建并挂载Vue应用实例到HTML页面中, 使用`mount`方法将该应用实例挂载到具有`id`为`app`的HTML元素上。这样，根组件`App`会被渲染并挂载到页面中。
