import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite' // Import the plugin

// https://vitejs.dev/config/
export default defineConfig({
  // Add the tailwindcss plugin here
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Your API Gateway URL
        changeOrigin: true,
      },
    },
  },
})