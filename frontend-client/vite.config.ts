import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  define: {
    global: 'window',
  },
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/oauth2': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/login/oauth2': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/ws-notifications': {
        target: 'http://localhost:8085',
        ws: true,
        changeOrigin: true
      }
    }
  }
});
