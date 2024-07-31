/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'export',
  basePath: '/stocks-stats',
  assetPrefix: '/stocks-stats/',
  images: {
    unoptimized: true,
  },
};

export default nextConfig;