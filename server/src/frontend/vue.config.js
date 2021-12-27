// vue.config.js
module.exports = {
    // https://cli.vuejs.org/config/#devserver-proxy
    devServer: {
        port: 8080,
        proxy: {
            '/api': {
                target: 'http://localhost:8082/api',
                ws: true,
                changeOrigin: true
            }
        }
    }
}