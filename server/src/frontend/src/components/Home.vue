<template>
  <div class="min-h-full flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    Home
  </div>
</template>

<script>
export default {
  name: 'Home',
  created() {
    if (this.loggedIn) {
      this.$router.push('/dashboard');
    } else {
      this.$router.push('/login');
    }
  },
}
</script>