import Vue from 'vue'
import Router from 'vue-router'

import Login from '@/components/Login.vue'
import Map from '@/components/Map.vue'
import Register from "@/components/Register";
import NotFound from "@/components/NotFound";
import Home from "@/components/Home";

Vue.use(Router)

export const router = new Router({
    mode: "history",
    routes: [
        { path: '/', mame: 'home', component: Home },
        { path: '/login', mame: 'login', component: Login },
        { path: '/register', name: 'register', component: Register },
        { path: '/dashboard', name: 'dashboard', component: Map },
        { path: '/*', name: '404', component: NotFound },
    ]
});

router.beforeEach((to, from, next) => {
    const publicPages = ['/login', '/register', '/'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = localStorage.getItem('user');

    if (authRequired && !loggedIn) {
        next('/login');
    } else {
        next();
    }
});
