import Vue from 'vue'
import App from './App.vue'
import './index.css'
import store from './store';
import { router } from './router'
import axios from 'axios'
import VueAxios from 'vue-axios'
import * as VueGoogleMaps from 'vue2-google-maps'

Vue.use(VueAxios, axios)

Vue.config.productionTip = false

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyB76CFJDxTCbj_WFZiAwzWw8rEhhTPPAg0',
    libraries: 'places',
  }
});

new Vue({
  router: router,
  store,
  render: h => h(App),
}).$mount('#app')
