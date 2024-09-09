import { createRouter, createWebHistory } from "vue-router";
const routes = [{
  name: "main",
  path: "/main",
  component: () => import("../components/Main.vue"),
  children: [{
    name: "category",//路由名称
    path: "/main/category",//路由路径
    component: () => import("../components/Category.vue")//路由组件
  }, {
    name: "Product",//路由名称
    path: "/main/product",//路由路径
    component: () => import("../components/Product.vue")//路由组件
  },
  {
    name: "User",//路由名称
    path: "/main/user",//路由路径
    component: () => import("../components/User.vue")//路由组件
  },
  {
    name: "Admin",//路由名称
    path: "/main/admin",//路由路径
    component: () => import("../components/Admin.vue")//路由组件
  },
  {
    name: "Schedule",//路由名称
    path: "/main/schedule",//路由路径
    component: () => import("../components/Schedule.vue")//路由组件
  },
  {
    name: "Book",//路由名称
    path: "/main/book",//路由路径
    component: () => import("../components/Book.vue")//路由组件
  },
  {
    name: "Order",//路由名称
    path: "/main/order",//路由路径
    component: () => import("../components/Order.vue")//路由组件
  },
  {
    name: "DashBoard",//路由名称
    path: "/main/dashboard",//路由路径
    component: () => import("../components/DashBoard.vue")//路由组件
  },
  ]
}, {
  name: "Login",
  path: "/login",
  component: () => import("../components/Login.vue")//路由组件
},
{
  name: "index",
  path: "",
  redirect: "/main"
}];
//创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;