<template>
  <div class="min-h-full flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full bg-white py-4 px-4 rounded shadow">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-12 w-auto" fill="none" viewBox="0 0 24 24"
             stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
        </svg>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          GPS Tracking Application
        </h2>
        <h3 class="mt-3 text-center text-xl font-bold text-gray-500">Register</h3>
      </div>
      <div class="mt-4 p-4 text-sm text-red-700 bg-red-100 rounded-lg dark:bg-red-200 dark:text-red-800"
           role="alert" v-if="msg && msg.status != 200">
        {{ msg.message }}
      </div>
      <form class="mt-1 space-y-6" @submit.prevent="handleRegister">
        <input type="hidden" name="remember" value="true">
        <div class="rounded-md shadow-sm space-y-2">
          <div>
            <label for="email-address" class="sr-only">Email address</label>
            <input id="email-address" name="email" type="email" autocomplete="email" required
                   class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-gray-500 focus:border-gray-500 focus:z-10 sm:text-sm"
                   placeholder="Email address"
                   v-model="user.email">
          </div>
          <div>
            <label for="password" class="sr-only">Password</label>
            <input id="password" name="password" type="password" required
                   class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-gray-500 focus:border-gray-500 focus:z-10 sm:text-sm"
                   placeholder="Password"
                   v-model="user.password">
          </div>
          <div>
            <label for="passwordConfirmation" class="sr-only">Password confirmation</label>
            <input id="passwordConfirmation" name="passwordConfirmation" type="password" required
                   class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-gray-500 focus:border-gray-500 focus:z-10 sm:text-sm"
                   placeholder="Password confirmation"
                   v-model="passwordConfirmation">
          </div>
        </div>

        <div>
          <button
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
          <span class="absolute left-0 inset-y-0 flex items-center pl-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24"
                 stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/>
            </svg>
          </span>
            Sign up
          </button>
        </div>
      </form>
      <button
          class="mt-2 group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-gray-600 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
          @click="backToLogin">
          <span class="absolute left-0 inset-y-0 flex items-center pl-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24"
                 stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12.066 11.2a1 1 0 000 1.6l5.334 4A1 1 0 0019 16V8a1 1 0 00-1.6-.8l-5.333 4zM4.066 11.2a1 1 0 000 1.6l5.334 4A1 1 0 0011 16V8a1 1 0 00-1.6-.8l-5.334 4z"/>
            </svg>
          </span>
        Back to Login
      </button>
    </div>
  </div>

</template>

<script>
import User from "@/models/User";

export default {
  name: 'Register',
  data() {
    return {
      user: new User('', ''),
      msg: '',
      passwordConfirmation: '',
      loading: false
    }
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    }
  },
  created() {
    if (this.loggedIn) {
      this.$router.push('/dashboard');
    }
  },
  methods: {
    handleRegister() {
      this.loading = true;

      if (this.user.email && this.user.password && this.passwordConfirmation) {
        if (this.user.password == this.passwordConfirmation) {
          this.$store.dispatch('auth/register', this.user).then(
              () => {
                this.$store.dispatch('auth/login', this.user).then(
                    () => {
                      this.$router.push('/dashboard');
                    },
                    error => {
                      this.loading = false;
                      this.msg =
                          (error.response && error.response.data) ||
                          error.message ||
                          error.toString();
                    }
                );
              },
              error => {
                this.loading = false;
                this.msg =
                    (error.response && error.response.data) ||
                    error.message ||
                    error.toString();
              }
          );
        } else {
          this.msg = {status: 400, message: "The passwords don't match!" }
        }
      } else {
        this.msg = {status: 400, message: "Please fill all the fields!" };
      }
    },
    backToLogin() {
      this.$router.push('/login');
    }
  }
}
</script>