# Sona Stream Extractor Backend

Dedicated Node.js / Express microservice for Sona Android Music Player.

## Features
- URL stream extraction & audio demuxing for video URLs and audio feeds
- Direct lossless FLAC / WAV / AAC stream handling
- Ready for 1-click deployment on **Render**, **Railway**, or **Fly.io**

## Quick Start
```bash
cd backend
npm install
npm start
```

## Endpoints
- `GET /health` - Service health status
- `GET /api/extract?url=<url>` - Extract audio stream & metadata
- `GET /api/search?q=<query>` - Search City Pop tracks
