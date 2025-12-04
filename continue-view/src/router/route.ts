import type {RouteRecordRaw} from 'vue-router'

const layout = ()=>import('@/layout/index.vue')
export const systemRoutes: RouteRecordRaw[] =  [
    {
        path:'/login',
        name:'Login',
        component:()=>import('@/view/login/index.vue'),
        meta: { title: '登录', hidden: true },
    },
    {
        path:'/',
        name:'Dashboard',
        component:layout,
        redirect:'/dashboard/workplace',
        meta: { title: '仪表盘', icon: 'dashboard', hidden: false },
        children:[{
            path:'/dashboard/workplace',
            name:'Workplace',
            component:()=>import("@/view/dashboard/workplace/index.vue"),
            meta: { title: '工作台', icon: 'desktop', hidden: false, affix: true },
        },
    {
        path:'/dashboard/analysis',
        name:'Analysis',
        component:()=>import("@/view/dashboard/analysis/index.vue"),
        meta: { title: '分析页', icon: 'insert-chart', hidden: false },
    }]
    },
  
    {
        path:'/user',
        name:'User',
        component:layout,
        redirect:'/user/profile',
        meta: { hidden: true },
        children:[  {
            path: '/user/profile',
            name: 'UserProfile',
            component: () => import('@/view/user/profile/index.vue'),
            meta: { title: '个人中心', showInTabs: false },
          },
          {
            path: '/user/message',
            name: 'UserMessage',
            component: () => import('@/view/user/message/index.vue'),
            meta: { title: '消息中心', showInTabs: false },
          },
          {
            path: '/user/notice',
            name: 'UserNotice',
            component: () => import('@/view/user/message/components/view/index.vue'),
            meta: { title: '查看公告' },
          },]
    },
    {
      path: '/social/callback',
      component: () => import('@/view/login/social/index.vue'),
      meta: { hidden: true },
    },
    {
      path: '/pwdExpired',
      component: () => import('@/view/login/pwdExpired/index.vue'),
      meta: { hidden: true },
    },
    {
        path: '/about',
        name: 'About',
        component: layout,
        meta: { title: '关于项目', icon: 'apps', hidden: false, sort: 999 },
        redirect: '/about/document/api',
        children: [
          {
            path: '/about/document/api',
            component: () => import('@/view/about/document/api/index.vue'),
            meta: { title: '接口文档', icon: 'swagger', hidden: false, keepAlive: true },
          },
          {
            path: '/about/document/changelog',
            component: () => import('@/view/about/document/changelog/index.vue'),
            meta: { title: '更新日志', icon: 'continew', hidden: false, keepAlive: true },
          },
        //   {
        //     path: 'https://arco.design/vue/component/button',
        //     meta: { title: 'Arco Design文档', icon: 'arco', hidden: false },
        //   },
        //   {
        //     path: '/about/source',
        //     name: 'AboutSource',
        //     meta: { title: '开源地址', icon: 'github', hidden: false },
        //     children: [
        //       {
        //         path: 'https://gitee.com/continew/continew-admin',
        //         meta: { title: 'Gitee', icon: 'gitee', hidden: false },
        //       },
        //       {
        //         path: 'https://gitcode.com/continew/continew-admin',
        //         meta: { title: 'GitCode', icon: 'gitcode', hidden: false },
        //       },
        //       {
        //         path: 'https://github.com/continew-org/continew-admin',
        //         meta: { title: 'GitHub', icon: 'github', hidden: false },
        //       },
        //     ],
        //   },
        ],
      },
]
// 固定路由（默认路由）
export const constantRoutes: RouteRecordRaw[] = [
    {
      path: '/redirect',
      component: layout,
      meta: { hidden: true },
      children: [
        {
          path: '/redirect/:path(.*)',
          component: () => import('@/view/default/redirect/index.vue'),
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('@/view/default/error/404.vue'),
      meta: { hidden: true },
    },
    {
      path: '/403',
      component: () => import('@/view/default/error/403.vue'),
      meta: { hidden: true },
    },
  ]
  
