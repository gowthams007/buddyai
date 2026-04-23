# 🔧 BuddyAI - Troubleshooting Guide

## 🚨 Common Issues & Solutions

---

## 🗄️ Database Issues

### Issue: "Connection refused - postgresql"

**Symptoms:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Solution:**

```bash
# macOS
brew services start postgresql
brew services list  # verify it's running

# Ubuntu/Linux
sudo systemctl start postgresql
sudo systemctl status postgresql

# Windows
# Use PostgreSQL installer or pgAdmin
```

### Issue: "FATAL: role 'buddy_user' does not exist"

**Solution:**
```bash
# Connect as postgres superuser
psql -U postgres

# Inside psql:
CREATE USER buddy_user WITH PASSWORD 'secure_password_here';
ALTER ROLE buddy_user CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE buddy_ai_db TO buddy_user;
\q
```

### Issue: Database migration fails

**Solution:**
```bash
# Check database exists
psql -U postgres -c "\l"

# If not, create it:
createdb -U postgres buddy_ai_db

# Run migration manually:
psql -U postgres -d buddy_ai_db < companion-backend/src/main/resources/db/migration/V1__initial_schema.sql
```

### Issue: "Database buddy_ai_db does not exist"

**Solution:**
```sql
-- Connect as postgres
psql -U postgres

-- Create database
CREATE DATABASE buddy_ai_db;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE buddy_ai_db TO buddy_user;
```

---

## 🔌 Backend Issues

### Issue: "Port 8080 already in use"

**Symptoms:**
```
Address already in use: /0.0.0.0:8080
```

**Solutions:**

Option 1 - Kill the process:
```bash
# Find what's using port 8080
lsof -i :8080

# Kill it
kill -9 <PID>
```

Option 2 - Use different port:
```bash
java -jar target/companion-backend-1.0.0.jar --server.port=8081
```

Option 3 - Find and stop previous process:
```bash
# Kill all Java processes
pkill -f java

# Or specifically stop Spring Boot
ps aux | grep java  # Find the process
kill <PID>
```

### Issue: "OpenAI API key is invalid"

**Symptoms:**
```
401 Unauthorized - Invalid Authentication
```

**Solution:**
1. Get key from https://platform.openai.com/api-keys
2. Key should start with `sk-`
3. Update `companion-backend/.env`:
   ```env
   OPENAI_API_KEY=sk-your-actual-key
   ```
4. Restart backend

### Issue: "Cannot build backend - Maven error"

**Symptoms:**
```
BUILD FAILURE
```

**Solutions:**

```bash
cd companion-backend

# Clear cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Try build again
mvn clean package -DskipTests

# If still fails, check Java version
java -version  # Should be 17+
```

### Issue: "Lombok annotation processing failed"

**Solution:**
```bash
# Ensure IDE recognizes Lombok
# VS Code: Install Lombok Annotations Support extension

# Or rebuild with annotations
mvn clean compile
```

### Issue: "Port 8080 returns 502 Bad Gateway"

**Solution:**
```bash
# Check if backend is actually running
curl http://localhost:8080/api/health

# Check backend logs
tail -f companion-backend/logs/app.log

# Restart backend
# Kill the process and restart
```

---

## 🎨 Frontend Issues

### Issue: "npm install fails"

**Symptoms:**
```
ERR! code ERESOLVE
ERR! ERESOLVE unable to resolve dependency tree
```

**Solution:**
```bash
cd companion-frontend

# Clear cache
rm -rf node_modules package-lock.json

# Install with legacy peer deps
npm install --legacy-peer-deps

# Or upgrade npm
npm install -g npm@latest
npm install
```

### Issue: "Vite dev server won't start"

**Solution:**
```bash
cd companion-frontend

# Port 5173 in use?
lsof -i :5173
kill -9 <PID>

# Clear cache
rm -rf node_modules
npm install

# Start again
npm run dev
```

### Issue: "Blank page / Frontend not loading"

**Solutions:**

1. Check browser console (F12) for errors
2. Verify Vite is running:
   ```bash
   npm run dev
   ```
3. Check API endpoint in `.env.local`:
   ```
   VITE_API_BASE_URL=http://localhost:8080/api
   ```
4. Verify backend is running:
   ```bash
   curl http://localhost:8080/api/chat
   ```

### Issue: "API calls failing - CORS error"

**Symptoms:**
```
Access to XMLHttpRequest blocked by CORS policy
```

**Solution:**

Update `CorsConfig.java`:
```java
registry.addMapping("/api/**")
        .allowedOrigins(
            "http://localhost:5173",  // Your frontend URL
            "http://localhost:3000"
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowCredentials(true);
```

Restart backend after changes.

### Issue: "API calls return 404"

**Symptoms:**
```
{
  "status": 404,
  "message": "Resource not found"
}
```

**Solution:**
1. Verify endpoint exists in controller
2. Check the API path matches controller mapping
3. Verify request method (GET/POST/PUT/DELETE)
4. Use Postman collection to test

---

## 🐳 Docker Issues

### Issue: "Docker daemon not running"

**Solution:**

macOS:
```bash
# Start Docker Desktop or use:
open /Applications/Docker.app
```

Linux:
```bash
sudo systemctl start docker
```

### Issue: "Container fails to start"

**Solution:**
```bash
# Check logs
docker-compose logs backend
docker-compose logs postgres

# Rebuild containers
docker-compose down
docker-compose up --build

# Force rebuild
docker-compose down
docker volume rm buddy_postgres_data
docker-compose up -d
```

### Issue: "Container can't connect to database"

**Solution:**
```bash
# Verify services are connected
docker-compose ps

# Check network
docker network ls
docker network inspect buddy_network

# Restart specific service
docker-compose restart postgres
docker-compose restart backend
```

### Issue: "Docker volume permission denied"

**Solution:**
```bash
# Check volume ownership
docker volume inspect buddy_postgres_data

# Fix permissions
sudo chown $USER:$USER /var/lib/docker/volumes/

# Or run with proper permissions
docker-compose exec postgres chmod 777 /var/lib/postgresql/data
```

---

## 📊 API Issues

### Issue: "Chat endpoint returns 500 error"

**Symptoms:**
```json
{
  "status": 500,
  "message": "An unexpected error occurred"
}
```

**Solution:**
1. Check backend logs:
   ```bash
   tail -f companion-backend/logs/app.log
   ```
2. Common causes:
   - OpenAI API key invalid
   - Database connection failed
   - Missing required fields
3. Check request format:
   ```bash
   curl -X POST http://localhost:8080/api/chat \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "message": "Hello"}'
   ```

### Issue: "Validation error - 422 Unprocessable Entity"

**Symptoms:**
```json
{
  "status": 422,
  "message": "Validation failed",
  "errors": {
    "message": "Message cannot be empty"
  }
}
```

**Solution:**
- Ensure all required fields are provided
- Check field constraints:
  - `message`: not empty, 1-5000 characters
  - `userId`: not null, must exist
  - `title`: not empty, 3-255 characters

### Issue: "Rate limiting error"

**Solution:**
```bash
# Check OpenAI rate limits
# See: https://platform.openai.com/account/rate-limits

# If hitting limits:
# 1. Upgrade your OpenAI plan
# 2. Reduce request frequency
# 3. Cache responses
```

---

## 🔍 Debugging Tips

### Enable Debug Logging

Update `application.properties`:
```properties
logging.level.com.example.demo=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### View Database Queries

```bash
# Connect to PostgreSQL
psql -U buddy_user -d buddy_ai_db

# Check tables
\dt

# View data
SELECT * FROM users;
SELECT * FROM conversations;

# Exit
\q
```

### Test API with cURL

```bash
# Test backend health
curl -v http://localhost:8080/api/chat

# Test with data
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -v \
  -d '{"userId": 1, "message": "test"}'
```

### Check Process Status

```bash
# List running Java processes
jps -l

# List running Node processes
ps aux | grep node

# Check port usage
lsof -i :8080
lsof -i :5173
lsof -i :5432
```

### Monitor Logs in Real-Time

```bash
# Backend logs
tail -f companion-backend/logs/app.log

# Docker logs
docker-compose logs -f backend
docker-compose logs -f postgres

# Follow and filter
docker-compose logs -f backend | grep ERROR
```

---

## 🆘 Still Having Issues?

### Diagnostic Steps

1. **Gather Information**
   ```bash
   java -version
   mvn -v
   npm -v
   psql --version
   docker -v
   ```

2. **Check All Services**
   ```bash
   # PostgreSQL
   psql -U postgres -c "SELECT 1"
   
   # Backend
   curl http://localhost:8080/api/chat
   
   # Frontend
   curl http://localhost:5173
   ```

3. **Review Logs**
   ```bash
   # Backend
   tail -100 companion-backend/logs/app.log
   
   # Check for errors
   grep ERROR companion-backend/logs/app.log
   ```

4. **Reset Everything**
   ```bash
   bash clean.sh  # If you create this
   bash setup.sh
   bash dev.sh
   ```

### Get Help

1. **GitHub Issues**: Create an issue with:
   - Error message
   - System info (OS, Java version, etc.)
   - Steps to reproduce
   - Logs

2. **Check Documentation**:
   - QUICK_START.md
   - API_DOCUMENTATION.md
   - README.md

3. **Stack Overflow**: Tag with `spring-boot`, `react`, `postgresql`

---

## 🔧 Advanced Debugging

### Enable Remote Debugging (Backend)

```bash
# Start with debug port 5005
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
  -jar target/companion-backend-1.0.0.jar
```

Connect your IDE to `localhost:5005`.

### Profile Frontend Performance

1. Open DevTools (F12)
2. Go to Performance tab
3. Record session
4. Check for slow operations

### Check Database Performance

```sql
-- Slow query log
EXPLAIN ANALYZE SELECT * FROM conversations WHERE user_id = 1;

-- Check indexes
SELECT * FROM pg_stat_user_indexes;

-- Find missing indexes
SELECT schemaname, tablename FROM pg_tables 
WHERE tablename LIKE 'conversations';
```

---

## 📋 Quick Reference

| Command | Purpose |
|---------|---------|
| `bash setup.sh` | Auto setup all |
| `bash dev.sh` | Start dev environment |
| `mvn clean test` | Run backend tests |
| `npm run lint` | Check frontend code |
| `docker-compose up` | Start with Docker |
| `docker-compose logs` | View logs |
| `curl localhost:8080/api/chat` | Test API |
| `tail -f logs/app.log` | Follow logs |

---

**Last Updated:** April 23, 2024  
**For More Help:** Check DELIVERY_SUMMARY.md for all resources
