/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
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
}

export default nextConfig
