/** @type {import('next').NextConfig} */
const isProd = process.env.NODE_ENV === 'production';
const CDN_PREFIX = process.env.NEXT_PUBLIC_CDN_URL || '';

const nextConfig = {
  reactStrictMode: true,
  output: 'standalone',
  assetPrefix: CDN_PREFIX || undefined,
  compiler: {
    // Strip console.* in production to reduce bundle size
    removeConsole: isProd ? { exclude: ['error', 'warn'] } : false,
  },
  modularizeImports: {
    '@mui/material': {
      transform: '@mui/material/{{member}}',
      skipDefaultConversion: true,
    },
    '@mui/icons-material': {
      transform: '@mui/icons-material/{{member}}',
      skipDefaultConversion: true,
    },
  },
  images: {
    formats: ['image/avif', 'image/webp'],
    minimumCacheTTL: 60,
    remotePatterns: [
      { protocol: 'https', hostname: '**' },
      { protocol: 'http', hostname: 'localhost' },
    ],
    deviceSizes: [360, 420, 768, 1024, 1280, 1536, 1920],
  },
  async headers() {
    return [
      {
        source: '/_next/static/:path*',
        headers: [
          { key: 'Cache-Control', value: 'public, max-age=31536000, immutable' },
        ],
      },
      {
        source: '/images/:path*',
        headers: [
          { key: 'Cache-Control', value: 'public, max-age=604800, stale-while-revalidate=86400' },
        ],
      },
    ];
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*',
      },
    ];
  },
};

module.exports = nextConfig;
