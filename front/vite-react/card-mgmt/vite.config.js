import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/',
  plugins: [react()],
  server: {
    host: true,          // 외부 접속 허용
    port: 5173,
    cors: true,          // CORS 허용 (필요 시)
    allowedHosts: ['l.0neteam.co.kr'], // ✅ 이 도메인을 허용
    // proxy: {
    //   '/api': {
    //     target: 'http://l.0neteam.co.kr:9000', // Spring Boot 백엔드 서버
    //     changeOrigin: true,
    //     secure: false,
    //   },
    // },
  },
})