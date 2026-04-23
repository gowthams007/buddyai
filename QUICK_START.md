# 🎯 BuddyAI - Quick Start Guide

## Prerequisites

- **Java 17+** (download from [Oracle](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+** (download from [Maven](https://maven.apache.org/download.cgi))
- **PostgreSQL 15+** (download from [PostgreSQL](https://www.postgresql.org/download/))
- **Node.js 18+** (download from [Node.js](https://nodejs.org/))
- **OpenAI API Key** (get from [OpenAI Platform](https://platform.openai.com/api-keys))

---

## 🚀 Quick Setup (5 minutes)

### Option 1: Automated Setup (Recommended)

```bash
# Navigate to project root
cd ~/Desktop/BuddyAI

# Make setup script executable
chmod +x setup.sh

# Run setup
bash setup.sh
```

This will:
- ✅ Check all prerequisites
- ✅ Create `.env` files
- ✅ Set up PostgreSQL database
- ✅ Build backend with Maven
- ✅ Install frontend dependencies

### Option 2: Manual Setup

#### Step 1: Create Database
```bash
psql -U postgres

-- Inside psql:
CREATE DATABASE buddy_ai_db;
CREATE USER buddy_user WITH PASSWORD 'secure_password_here';
ALTER ROLE buddy_user SET client_encoding TO 'utf8';
GRANT ALL PRIVILEGES ON DATABASE buddy_ai_db TO buddy_user;
\c buddy_ai_db
GRANT ALL PRIVILEGES ON SCHEMA public TO buddy_user;
\q
```

#### Step 2: Create Backend .env
```bash
cd companion-backend
cat > .env << 'EOF'
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/buddy_ai_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
OPENAI_API_KEY=sk-your-api-key-here
APP_ENV=development
APP_DEBUG=true
EOF
```

#### Step 3: Create Frontend .env
```bash
cd ../companion-frontend
cat > .env.local << 'EOF'
VITE_API_BASE_URL=http://localhost:8080/api
VITE_ENVIRONMENT=development
EOF
```

#### Step 4: Build Backend
```bash
cd ../companion-backend
mvn clean package -DskipTests
```

#### Step 5: Install Frontend
```bash
cd ../companion-frontend
npm install
```

---

## 🏃 Running Locally

### Option 1: Using Dev Script (Easiest)

```bash
cd ~/Desktop/BuddyAI
chmod +x dev.sh
bash dev.sh
```

This starts:
- Backend on `http://localhost:8080/api`
- Frontend on `http://localhost:5173`

### Option 2: Manual Start

**Terminal 1 - Backend:**
```bash
cd companion-backend
java -jar target/companion-backend-1.0.0.jar
```

**Terminal 2 - Frontend:**
```bash
cd companion-frontend
npm run dev
```

---

## 🌐 Access the Application

Once running:

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:5173 | Web UI |
| Backend API | http://localhost:8080/api | REST API |
| API Docs | http://localhost:8080/api/chat | Example endpoint |

---

## ✅ First Test

### Test the Chat API
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "message": "Hello, what can you help me with?"}'
```

Expected Response:
```json
{
  "response": "Hi! I'm your personal AI companion...",
  "timestamp": 1682000000000
}
```

### Test Frontend
Open http://localhost:5173 in your browser. You should see the BuddyAI chat interface.

---

## 📁 Project Structure

```
BuddyAI/
├── companion-backend/          # Java Spring Boot API
│   ├── src/main/java/
│   │   └── com/example/demo/
│   │       ├── controller/      # REST endpoints
│   │       ├── service/         # Business logic
│   │       ├── repository/      # Database access
│   │       ├── entity/          # Database models
│   │       ├── dto/             # Data transfer objects
│   │       ├── exception/       # Error handling
│   │       └── config/          # Configuration
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml                 # Maven dependencies
│   └── Dockerfile
│
├── companion-frontend/          # React PWA
│   ├── src/
│   │   ├── components/          # React components
│   │   ├── api.ts              # API client
│   │   ├── App.tsx             # Main app
│   │   └── main.tsx            # Entry point
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
│
├── IMPROVEMENT_AND_DEPLOYMENT_GUIDE.md
├── PRODUCTION_DEPLOYMENT_GUIDE.md
├── API_DOCUMENTATION.md
├── setup.sh                     # Automated setup
├── dev.sh                       # Local development runner
└── README.md
```

---

## 🔧 Common Issues & Solutions

### Issue: Port 8080 already in use
```bash
# Kill process using port 8080
lsof -ti:8080 | xargs kill -9

# Or use different port
java -jar target/companion-backend-1.0.0.jar --server.port=8081
```

### Issue: Database connection failed
```bash
# Check if PostgreSQL is running
psql -U postgres -c "SELECT 1"

# Start PostgreSQL:
# macOS: brew services start postgresql
# Ubuntu: sudo systemctl start postgresql
# Windows: Use PostgreSQL installer
```

### Issue: npm dependencies error
```bash
# Clear cache and reinstall
cd companion-frontend
rm -rf node_modules package-lock.json
npm install
```

### Issue: OpenAI API key error
1. Go to https://platform.openai.com/api-keys
2. Create a new API key
3. Copy it to `companion-backend/.env`
4. Restart backend

---

## 📚 Next Steps

1. **Explore the API**
   - See [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
   - Test endpoints with Postman or cURL

2. **Understand the Code**
   - Review `AgentOrchestrator.java` - the brain of the app
   - Check `ChatService.java` - OpenAI integration
   - Examine `Memory Service.java` - embedding storage

3. **Customize AI Behavior**
   - Edit system prompts in `AgentOrchestrator.java`
   - Adjust LLM model settings in `application.properties`
   - Add custom prompts for specific domains

4. **Implement Features**
   - Add user authentication (JWT)
   - Implement goal tracking
   - Add Firebase notifications
   - Create daily summary generation

5. **Deploy to Production**
   - Follow [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)
   - Set up Docker & Docker Compose
   - Configure SSL/TLS certificates
   - Enable database backups

---

## 🐛 Debugging

### Backend Logs
```bash
tail -f companion-backend/logs/app.log
```

### Frontend Browser Console
Open DevTools (F12) → Console tab

### Check Running Processes
```bash
# See all Java processes
jps -l

# See all Node processes
ps aux | grep node
```

### Database Queries
```bash
psql -U postgres -d buddy_ai_db

-- Check tables:
\dt

-- Query users:
SELECT * FROM users;

-- Exit:
\q
```

---

## 📞 Help & Support

- **GitHub Issues**: Report bugs or request features
- **API Documentation**: See [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **Improvement Guide**: See [IMPROVEMENT_AND_DEPLOYMENT_GUIDE.md](IMPROVEMENT_AND_DEPLOYMENT_GUIDE.md)
- **Production Guide**: See [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)

---

## 🎉 You're All Set!

Start developing with:
```bash
bash dev.sh
```

Happy coding! 🚀
