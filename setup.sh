#!/bin/bash

# =============================================================================
# BuddyAI Setup Script
# Automated setup for local development environment
# =============================================================================

set -e

echo "🚀 BuddyAI - Local Development Setup"
echo "======================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo -e "\n${YELLOW}Checking prerequisites...${NC}"

if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    echo "Please install Java 17+ from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi
echo -e "${GREEN}✓ Java found$(java -version 2>&1 | grep version)${NC}"

if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed${NC}"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    exit 1
fi
echo -e "${GREEN}✓ Maven found$(mvn -v | head -n 1)${NC}"

if ! command -v psql &> /dev/null; then
    echo -e "${RED}❌ PostgreSQL client is not installed${NC}"
    echo "Please install PostgreSQL from: https://www.postgresql.org/download/"
    exit 1
fi
echo -e "${GREEN}✓ PostgreSQL found$(psql --version)${NC}"

if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed${NC}"
    echo "Please install Node.js from: https://nodejs.org/"
    exit 1
fi
echo -e "${GREEN}✓ Node.js found$(node -v)${NC}"

# Create .env files
echo -e "\n${YELLOW}Creating .env files...${NC}"

cd "$(dirname "$0")"

# Backend .env
cat > companion-backend/.env << 'EOF'
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/buddy_ai_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# OpenAI API (get from https://platform.openai.com/api-keys)
OPENAI_API_KEY=sk-your-api-key-here

# Firebase (optional for notifications)
FIREBASE_CONFIG_PATH=firebase-config.json

# Application
APP_ENV=development
APP_DEBUG=true
DB_URL=jdbc:postgresql://localhost:5432/buddy_ai_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
EOF

echo -e "${GREEN}✓ Created companion-backend/.env${NC}"

# Frontend .env
cat > companion-frontend/.env.local << 'EOF'
VITE_API_BASE_URL=http://localhost:8080/api
VITE_ENVIRONMENT=development
EOF

echo -e "${GREEN}✓ Created companion-frontend/.env.local${NC}"

# Create PostgreSQL database
echo -e "\n${YELLOW}Setting up PostgreSQL database...${NC}"

psql -U postgres << EOF
SELECT 1 FROM pg_database WHERE datname = 'buddy_ai_db';
EOF

if [ $? -ne 0 ]; then
    echo -e "${YELLOW}Creating database...${NC}"
    psql -U postgres << EOF
CREATE DATABASE buddy_ai_db;
CREATE USER buddy_user WITH PASSWORD 'secure_password_here';
ALTER ROLE buddy_user SET client_encoding TO 'utf8';
ALTER ROLE buddy_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE buddy_user SET default_transaction_deferrable TO on;
ALTER ROLE buddy_user SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE buddy_ai_db TO buddy_user;
\c buddy_ai_db
GRANT ALL PRIVILEGES ON SCHEMA public TO buddy_user;
EOF
    echo -e "${GREEN}✓ Database created${NC}"
else
    echo -e "${GREEN}✓ Database already exists${NC}"
fi

# Build backend
echo -e "\n${YELLOW}Building backend...${NC}"
cd companion-backend
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Backend built successfully${NC}"
else
    echo -e "${RED}❌ Backend build failed${NC}"
    exit 1
fi
cd ..

# Install frontend dependencies
echo -e "\n${YELLOW}Installing frontend dependencies...${NC}"
cd companion-frontend
npm install
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Frontend dependencies installed${NC}"
else
    echo -e "${RED}❌ Frontend setup failed${NC}"
    exit 1
fi
cd ..

echo -e "\n${GREEN}✅ Setup completed successfully!${NC}"
echo -e "\n${YELLOW}Next steps:${NC}"
echo "1. Update .env files with your API keys"
echo "2. Run backend:  cd companion-backend && java -jar target/companion-backend-1.0.0.jar"
echo "3. Run frontend: cd companion-frontend && npm run dev"
echo -e "\n${YELLOW}API will be available at:${NC} http://localhost:8080/api"
echo -e "${YELLOW}Frontend will be available at:${NC} http://localhost:5173"
