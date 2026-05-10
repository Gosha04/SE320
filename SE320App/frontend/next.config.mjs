/** @type {import('next').NextConfig} */
const isStaticExport = process.env.NEXT_OUTPUT === 'export'

const nextConfig = {
  ...(process.env.NEXT_DIST_DIR ? { distDir: process.env.NEXT_DIST_DIR } : {}),
  ...(isStaticExport ? { output: 'export' } : {}),
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
  ...(isStaticExport
    ? {}
    : {
        async rewrites() {
          return [
            {
              source: '/auth/:path*',
              destination: 'http://localhost:8080/auth/:path*',
            },
            {
              source: '/sessions/:path*',
              destination: 'http://localhost:8080/sessions/:path*',
            },
            {
              source: '/diary/:path*',
              destination: 'http://localhost:8080/diary/:path*',
            },
            {
              source: '/progress/:path*',
              destination: 'http://localhost:8080/progress/:path*',
            },
            {
              source: '/crisis/:path*',
              destination: 'http://localhost:8080/crisis/:path*',
            },
          ]
        },
      }),
}

export default nextConfig
