# =============================================================================
# BuddyAI Production Deployment Guide
# =============================================================================

## Prerequisites

- Docker & Docker Compose installed
- PostgreSQL 15+
- OpenAI API key
- Firebase credentials (for notifications)
- SSL certificate for HTTPS

---

## Step 1: Prepare Production Environment

### 1.1 Create Production .env

```bash
# companion-backend/.env.prod

# Database (use managed service like AWS RDS)
SPRING_DATASOURCE_URL=jdbc:postgresql://rds-instance.amazonaws.com:5432/buddy_ai_prod
SPRING_DATASOURCE_USERNAME=buddy_prod_user
SPRING_DATASOURCE_PASSWORD=${SECURE_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=validate

# OpenAI
OPENAI_API_KEY=${YOUR_OPENAI_KEY}

# Firebase
FIREBASE_CONFIG_PATH=firebase-config-prod.json

# Application
APP_ENV=production
APP_DEBUG=false
SERVER_PORT=8080

# Logging
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_EXAMPLE_DEMO=INFO
```

### 1.2 Create Docker Compose for Production

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: buddy_ai_prod
      POSTGRES_USER: buddy_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_prod_data:/var/lib/postgresql/data
      - ./backup:/backup
    networks:
      - buddy_network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U buddy_user"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./companion-backend
      dockerfile: Dockerfile
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/buddy_ai_prod
      SPRING_DATASOURCE_USERNAME: buddy_user
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      APP_ENV: production
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - buddy_network
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./companion-frontend
      dockerfile: Dockerfile
    environment:
      VITE_API_BASE_URL: https://api.buddyai.com/api
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - backend
    networks:
      - buddy_network
    restart: always

  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./ssl/cert.pem:/etc/nginx/ssl/cert.pem
      - ./ssl/key.pem:/etc/nginx/ssl/key.pem
    depends_on:
      - frontend
      - backend
    networks:
      - buddy_network
    restart: always

volumes:
  postgres_prod_data:

networks:
  buddy_network:
    driver: bridge
```

---

## Step 2: Set Up SSL/TLS

### Using Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Get certificate
sudo certbot certonly --standalone -d api.buddyai.com -d www.buddyai.com

# Renew automatically (cron job)
0 0 1 * * certbot renew --quiet
```

### Self-signed Certificate (Testing)

```bash
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365
```

---

## Step 3: Configure Reverse Proxy (Nginx)

### nginx.conf

```nginx
upstream backend {
    server backend:8080;
}

upstream frontend {
    server frontend:80;
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name api.buddyai.com www.buddyai.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS Server
server {
    listen 443 ssl http2;
    server_name api.buddyai.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Backend API
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_buffering off;
        proxy_request_buffering off;
    }

    # Health check
    location /health {
        proxy_pass http://backend;
        access_log off;
    }
}

# Frontend
server {
    listen 443 ssl http2;
    server_name www.buddyai.com buddyai.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
    }
}
```

---

## Step 4: Database Backups

### Automated Backup Script

```bash
#!/bin/bash
# backup-db.sh

BACKUP_DIR="/backup"
DB_NAME="buddy_ai_prod"
DB_USER="buddy_user"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Full backup
pg_dump -h postgres -U $DB_USER -d $DB_NAME | gzip > $BACKUP_DIR/buddyai_$DATE.sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "buddyai_*.sql.gz" -mtime +30 -delete

# Upload to S3 (optional)
aws s3 cp $BACKUP_DIR/buddyai_$DATE.sql.gz s3://your-bucket/backups/
```

### Schedule with Cron

```bash
# Daily backup at 2 AM
0 2 * * * /path/to/backup-db.sh
```

---

## Step 5: Monitoring & Alerting

### Install Prometheus & Grafana

```bash
docker pull prom/prometheus
docker pull grafana/grafana

# Add to docker-compose.yml
```

### Application Metrics

Add to `pom.xml`:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Add to `application.properties`:
```properties
management.endpoints.web.exposure.include=prometheus,health,metrics
management.metrics.enable.jvm=true
management.metrics.enable.process=true
```

---

## Step 6: Deployment Steps

### 1. Prepare Server

```bash
# SSH into production server
ssh user@production-server

# Clone repository
git clone https://github.com/yourusername/buddy-ai.git
cd buddy-ai

# Create secrets file
touch .env.prod
# Add all required environment variables
```

### 2. Build & Deploy

```bash
# Build images
docker-compose -f docker-compose.prod.yml build

# Pull latest code
git pull origin main

# Start services
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f backend
```

### 3. Verify Deployment

```bash
# Check service health
curl -k https://api.buddyai.com/api/health

# Check database
docker exec buddy-postgres psql -U buddy_user -d buddy_ai_prod -c "SELECT COUNT(*) FROM users;"

# Check logs
docker-compose logs --tail 100 backend
```

---

## Step 7: CI/CD Pipeline (GitHub Actions)

### .github/workflows/deploy-prod.yml

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Build and Test
        run: |
          cd companion-backend
          mvn clean test
          mvn clean package -DskipTests

      - name: Build Docker Images
        run: |
          docker build -t buddy-backend:${{ github.sha }} companion-backend/
          docker build -t buddy-frontend:${{ github.sha }} companion-frontend/

      - name: Push to Registry
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker tag buddy-backend:${{ github.sha }} ${{ secrets.DOCKER_USERNAME }}/buddy-backend:latest
          docker push ${{ secrets.DOCKER_USERNAME }}/buddy-backend:latest

      - name: Deploy to Server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.PROD_HOST }}
          username: ${{ secrets.PROD_USER }}
          key: ${{ secrets.PROD_SSH_KEY }}
          script: |
            cd ~/buddy-ai
            git pull origin main
            docker-compose -f docker-compose.prod.yml pull
            docker-compose -f docker-compose.prod.yml up -d
            docker-compose logs backend
```

---

## Step 8: Monitoring & Health Checks

### Set Up Alerts

```bash
# Using Sentry for error tracking
# Sign up: https://sentry.io/

# Add to application.properties
sentry.dsn=${SENTRY_DSN}
sentry.environment=production
```

### Database Monitoring

```bash
# Monitor slow queries
EXPLAIN ANALYZE SELECT ...

# Check indexes
SELECT * FROM pg_stat_user_indexes;
```

---

## Rollback Procedure

If deployment fails:

```bash
# View previous image
docker images | grep buddy-backend

# Rollback to previous version
docker-compose -f docker-compose.prod.yml down
docker tag buddy-backend:previous-sha buddy-backend:latest
docker-compose -f docker-compose.prod.yml up -d

# Restore database from backup
psql -h localhost -U buddy_user -d buddy_ai_prod < /backup/buddyai_latest.sql
```

---

## Checklist Before Going Live

- [ ] SSL/TLS certificates configured
- [ ] Database backups automated
- [ ] Monitoring & alerting set up
- [ ] Environment variables secured
- [ ] Load testing completed
- [ ] Disaster recovery plan documented
- [ ] Team trained on deployment
- [ ] Runbooks created for common issues

---

**Stay Safe & Happy Deploying! 🚀**
