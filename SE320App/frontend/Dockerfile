ARG TARGETPLATFORM=linux/amd64

FROM --platform=$TARGETPLATFORM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN NEXT_OUTPUT=export npm run build

FROM --platform=$TARGETPLATFORM nginx:1.27-alpine AS runtime

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/out /usr/share/nginx/html

EXPOSE 3000
