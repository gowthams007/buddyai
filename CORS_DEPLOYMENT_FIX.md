# CORS Deployment Fix Guide

## Problem
When deploying the backend to Railway and frontend to Vercel, CORS (Cross-Origin Resource Sharing) errors occur preventing the frontend from communicating with the backend.

**Error:** `Access-Control-Allow-Origin header has a value that is not equal to the supplied origin`

## Root Causes
1. The original CORS filter was setting `Access-Control-Allow-Credentials: true` with wildcard origins, which violates CORS spec
2. The frontend API was defaulting to `http://localhost:8080` instead of the Railway backend URL
3. Headers configuration was too permissive (`*`)

## Solution Applied

### 1. Backend CORS Configuration (SimpleCorsFilter.java)
Updated the CORS filter to:
- ✅ Whitelist specific allowed origins
- ✅ Support all `*.vercel.app` domains (covers all Vercel deployments)
- ✅ Support all `*.railway.app` domains
- ✅ Set `Access-Control-Allow-Credentials: false` (correct for public APIs)
- ✅ Specify exact headers instead of wildcard
- ✅ Allow local development URLs (localhost:3000, localhost:5173)

### 2. Frontend Environment Configuration
Created two environment files:

**`.env.development`** - for local development
```
VITE_API_BASE_URL=http://localhost:8080/api
```

**`.env.production`** - for Vercel deployment
```
VITE_API_BASE_URL=https://buddyai-production-2255.railway.app/api
```

### 3. Frontend API Configuration (api.ts)
- Updated to use environment variables
- Added explicit Content-Type headers
- Uses Railway backend URL in production

## Deployment Steps

### Backend on Railway
1. Make sure the updated `SimpleCorsFilter.java` is committed
2. Rebuild and redeploy to Railway
   ```bash
   git add -A
   git commit -m "Fix CORS configuration for Vercel frontend"
   git push  # This triggers Railway redeploy
   ```

### Frontend on Vercel
1. Set up environment variable in Vercel Dashboard:
   - Go to **Settings → Environment Variables**
   - Add: `VITE_API_BASE_URL = https://buddyai-production-2255.railway.app/api`
   
2. OR add to `vercel.json`:
   ```json
   {
     "env": {
       "VITE_API_BASE_URL": "@railway_api_url"
     }
   }
   ```

3. Redeploy: Push your changes to trigger automatic Vercel deployment
   ```bash
   git add -A
   git commit -m "Update CORS configuration and env files"
   git push
   ```

## Testing the Fix

1. Open your Vercel frontend URL in browser
2. Go to **DevTools → Console**
3. Try chatting with BuddyAI - you should NOT see CORS errors
4. Verify in **Network tab** that requests to the Railway backend succeed with status 200

## Troubleshooting

### Still seeing CORS errors?
- Check DevTools Console for exact error message
- Verify the frontend URL ends with `.vercel.app`
- Ensure Railway backend URL matches in environment variables
- Clear browser cache and hard refresh (Ctrl+Shift+R or Cmd+Shift+R)

### Backend responding with 401/403?
- Check if authentication is required
- Verify userId is being sent correctly in requests

### Railway backend URL changed?
- Update the Railway domain in both:
  - `companion-frontend/.env.production`
  - `SimpleCorsFilter.java` if using specific URL (currently supports any `*.railway.app`)

## Files Modified
1. `companion-backend/src/main/java/com/example/demo/config/SimpleCorsFilter.java` - Updated CORS whitelist logic
2. `companion-frontend/src/api.ts` - Added explicit base URL with env variable support
3. `companion-frontend/.env.production` - Production environment settings
4. `companion-frontend/.env.development` - Development environment settings
