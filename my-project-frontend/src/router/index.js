import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "@/net/index.js";

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
                }, {
                    path:'register',
                    name:'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                }
            ]
        },{
            path:'/index',
            name:'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})
//配置路由守卫
router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    if (to.name.startsWith('welcome-') && !isUnauthorized) {//若用户要去welcome开头的界面, 但又通过了验证, 则直接跳转到index界面
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized){//若用户要去index界面, 但未通过了验证, 则跳转到欢迎界面
        next('/')
    } else{//其他情况, 正常流程即可
        next()
    }

})

export default router //将创建的Vue Router实例导出，使其可以在其他地方被引入和使用