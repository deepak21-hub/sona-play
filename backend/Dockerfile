FROM node:20-slim

WORKDIR /usr/src/app

# Install ffmpeg and python for audio stream transcoding
RUN apt-get update && apt-get install -y ffmpeg python3 python3-pip && rm -rf /var/lib/apt/lists/*

COPY package*.json ./
RUN npm ci --only=production

COPY . .

EXPOSE 3000

CMD ["node", "index.js"]
