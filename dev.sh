#!/bin/bash

# =============================================================================
# BuddyAI Local Development Runner
# Starts both backend and frontend with proper configuration
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

cd "$(dirname "$0")"

echo -e "${BLUE}"
echo "╔════════════════════════════════════════╗"
echo "║       🚀 BuddyAI Local Development     ║"
echo "╚════════════════════════════════════════╝"
echo -e "${NC}"

# Check if .env files exist
if [ ! -f "companion-backend/.env" ]; then
    echo -e "${RED}❌ Backend .env file not found${NC}"
    echo "Run: bash setup.sh"
    exit 1
fi

if [ ! -f "companion-frontend/.env.local" ]; then
    echo -e "${RED}❌ Frontend .env.local file not found${NC}"
    echo "Run: bash setup.sh"
    exit 1
fi

# Load environment variables
export $(cat companion-backend/.env | grep -v '^#' | xargs)

# Start PostgreSQL (assuming it's running)
echo -e "${YELLOW}Checking PostgreSQL...${NC}"
psql -U postgres -d buddy_ai_db -c "SELECT 1" &>/dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ PostgreSQL is running${NC}"
else
    echo -e "${RED}❌ PostgreSQL is not running${NC}"
    echo "Start PostgreSQL first:"
    echo "  Mac: brew services start postgresql"
    echo "  Ubuntu: sudo systemctl start postgresql"
    exit 1
fi

# Start backend
echo -e "\n${YELLOW}Starting backend...${NC}"
cd companion-backend

if [ ! -f "target/companion-backend-1.0.0.jar" ]; then
    echo -e "${YELLOW}Building backend first...${NC}"
    mvn clean package -DskipTests
fi

echo -e "${GREEN}Backend starting...${NC}"
java -jar target/companion-backend-1.0.0.jar &
BACKEND_PID=$!
sleep 3

if kill -0 $BACKEND_PID 2>/dev/null; then
    echo -e "${GREEN}✓ Backend running (PID: $BACKEND_PID)${NC}"
else
    echo -e "${RED}❌ Failed to start backend${NC}"
    exit 1
fi

cd ..

# Start frontend
echo -e "\n${YELLOW}Starting frontend...${NC}"
cd companion-frontend
echo -e "${GREEN}Frontend starting on http://localhost:5173${NC}"
npm run dev &
FRONTEND_PID=$!

cd ..

echo -e "\n${GREEN}✅ Both services are running!${NC}"
echo -e "\n${BLUE}Service URLs:${NC}"
echo -e "  API:      http://localhost:8080/api"
echo -e "  Frontend: http://localhost:5173"
echo -e "\n${BLUE}Logs:${NC}"
echo -e "  Backend:  companion-backend/logs/app.log"
echo -e "\n${YELLOW}Press Ctrl+C to stop both services${NC}"

# Cleanup on exit
trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; echo -e '\n${GREEN}Services stopped${NC}'" EXIT

wait
