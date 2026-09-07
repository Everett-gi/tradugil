import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.svg'],
      manifest: {
        name: 'Tradugil: entenda as gírias da internet',
        short_name: 'Tradugil',
        description:
          'Descubra o que significam as gírias e abreviações que aparecem nas suas conversas.',
        lang: 'pt-BR',
        start_url: '/',
        display: 'standalone',
        // Acompanham a paleta da folha de estilo. O background_color e o
        // que o Android pinta na tela de abertura antes de o site carregar:
        // deixar branco aqui daria um lampejo branco antes do papel verde.
        background_color: '#eef1ec',
        theme_color: '#0e6b4c',
        icons: [
          { src: 'icone-192.png', sizes: '192x192', type: 'image/png' },
          { src: 'icone-512.png', sizes: '512x512', type: 'image/png' },
          {
            src: 'icone-512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'maskable',
          },
        ],
        // Recebe texto compartilhado de outros aplicativos. É o que faz o
        // PWA instalado no Android aparecer na folha de compartilhamento,
        // sem precisar do aplicativo nativo (RF20).
        share_target: {
          action: '/compartilhado',
          method: 'GET',
          params: { title: 'title', text: 'text', url: 'url' },
        },
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,svg,png,woff2}'],
        runtimeCaching: [
          {
            // O verbete é cacheado para sobreviver à falta de conexão, mas a
            // rede vem primeiro: a curadoria corrige explicações, e servir
            // uma versão velha de um termo de risco seria pior que esperar.
            urlPattern: /\/api\/v1\/girias\//,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'verbetes',
              networkTimeoutSeconds: 5,
              expiration: { maxEntries: 500, maxAgeSeconds: 60 * 60 * 24 * 30 },
            },
          },
        ],
        // POST não é cacheável pelo service worker. O /traduzir offline é
        // resolvido pelo dicionário local (Dexie), não por aqui.
        navigateFallbackDenylist: [/^\/api\//],
      },
    }),
  ],
  server: {
    port: 5173,
    proxy: {
      // Evita CORS em desenvolvimento e faz o front usar caminho relativo,
      // igual ao que acontece em produção atrás do Caddy.
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
});
