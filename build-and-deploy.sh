#!/bin/bash

# =============================================================================
# BuddyAI Production Build & Deploy Script
# For CI/CD pipelines and automated deployments
# =============================================================================

set -e

echo "🚀 BuddyAI - Production Build & Deploy"
echo "========================================"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
BUILD_DIR="/tmp/buddy_build_$$"
REGISTRY="${DOCKER_REGISTRY:-docker.io}"
IMAGE_NAME="${IMAGE_NAME:-buddy-ai}"
VERSION="${VERSION:-latest}"

# Create build directory
mkdir -p $BUILD_DIR
cd $BUILD_DIR

echo -e "${BLUE}Build Configuration:${NC}"
echo "  Registry: $REGISTRY"
echo "  Image: $IMAGE_NAME"
echo "  Version: $VERSION"

# =============================================================================
# Build Backend
# =============================================================================
echo -e "\n${YELLOW}Building Backend...${NC}"

cp -r /path/to/companion-backend .

cd companion-backend

# Run tests
echo -e "${YELLOW}Running tests...${NC}"
mvn clean test || {
    echo -e "${RED}Tests failed${NC}"
    exit 1
}

# Build JAR
echo -e "${YELLOW}Building JAR...${NC}"
mvn clean package -DskipTests || {
    echo -e "${RED}Build failed${NC}"
    exit 1
}

# Build Docker image
echo -e "${YELLOW}Building Docker image...${NC}"
docker build -t $REGISTRY/$IMAGE_NAME-backend:$VERSION . || {
    echo -e "${RED}Docker build failed${NC}"
    exit 1
}

echo -e "${GREEN}✓ Backend built${NC}"
cd ..

# =============================================================================
# Build Frontend
# =============================================================================
echo -e "\n${YELLOW}Building Frontend...${NC}"

cp -r /path/to/companion-frontend .

cd companion-frontend

# Install dependencies
echo -e "${YELLOW}Installing dependencies...${NC}"
npm ci || {
    echo -e "${RED}Dependency installation failed${NC}"
    exit 1
}

# Run linter
echo -e "${YELLOW}Running linter...${NC}"
npm run lint || echo "⚠️  Linting warnings (non-blocking)"

# Build
echo -e "${YELLOW}Building...${NC}"
npm run build || {
    echo -e "${RED}Build failed${NC}"
    exit 1
}

# Build Docker image
echo -e "${YELLOW}Building Docker image...${NC}"
docker build -t $REGISTRY/$IMAGE_NAME-frontend:$VERSION . || {
    echo -e "${RED}Docker build failed${NC}"
    exit 1
}

echo -e "${GREEN}✓ Frontend built${NC}"
cd ..

# =============================================================================
# Push to Registry
# =============================================================================
if [ -n "$DOCKER_USERNAME" ] && [ -n "$DOCKER_PASSWORD" ]; then
    echo -e "\n${YELLOW}Pushing to Docker Registry...${NC}"
    
    echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin $REGISTRY
    
    docker push $REGISTRY/$IMAGE_NAME-backend:$VERSION
    docker push $REGISTRY/$IMAGE_NAME-frontend:$VERSION
    
    echo -e "${GREEN}✓ Images pushed to registry${NC}"
fi

# =============================================================================
# Summary
# =============================================================================
echo -e "\n${GREEN}✅ Build completed successfully!${NC}"
echo -e "\n${BLUE}Docker Images:${NC}"
echo "  Backend:  $REGISTRY/$IMAGE_NAME-backend:$VERSION"
echo "  Frontend: $REGISTRY/$IMAGE_NAME-frontend:$VERSION"

echo -e "\n${BLUE}Next Steps:${NC}"
echo "  1. Run: docker-compose up -d"
echo "  2. Check logs: docker-compose logs -f backend"
echo "  3. Test API: curl http://localhost:8080/api/health"

# Cleanup
rm -rf $BUILD_DIR

exit 0
