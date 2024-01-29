import {createRouter, createWebHistory} from "vue-router";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),//`history`选项用于配置路由的历史模式，这里使用`createWebHistory`表示使用HTML5 history模式
    routes: [//`routes`选项用于定义路由规则，包括路径、名称和对应的组件。
        {
            path:'/',
            name:'welcome',
            component: ()=> import ('@/views/WelcomeView.vue'),
            children:[//子页面
                {
                    path:'',
                    name:'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                }
            ]
        }
    ]
})

export default router //将创建的Vue Router实例导出，使其可以在其他地方被引入和使用