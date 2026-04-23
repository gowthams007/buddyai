# BuddyAI - Personal AI Companion

BuddyAI is a monolithic Spring Boot backend and React PWA frontend that acts like a smart life assistant, helping you manage daily tasks, goals, reminders, and providing smart conversational AI capabilities with memory.

## Tech Stack
- **Backend:** Java 17 + Spring Boot 3.4.0
- **Database:** PostgreSQL
- **AI:** OpenAI API (GPT-3.5-turbo & text-embedding-ada-002)
- **Frontend:** React + TypeScript + Vanilla CSS (Mobile-first, Premium Glassmorphism UI)
- **Notifications:** Firebase Cloud Messaging (FCM)

## Project Structure
- `companion-backend/`: The Spring Boot API server with Postgres DB, REST controllers, and OpenAI/Firebase integration.
- `companion-frontend/`: The React app for chat interface, goal tracking, and reminder dashboard.

---

## 🚀 Setup Instructions (Local Development)

### 1. Database Setup
Ensure you have PostgreSQL running locally on port 5432.
```sql
CREATE DATABASE companion_db;
```
*(No need to create tables manually; Spring Data JPA will auto-update the schema via `spring.jpa.hibernate.ddl-auto=update`)*

### 2. Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd companion-backend
   ```
2. Configure Environment Variables in `application.yml` or export them:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key_here
   export FIREBASE_KEY_PATH=classpath:firebase-service-account.json
   ```
3. Run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```
*(The backend will start on `http://localhost:8080`)*

### 3. Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd companion-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
*(The frontend will start on `http://localhost:5173` or similar. Open it in your browser!)*

---

## Architecture Flow
- **User Messages:** The React app sends `/api/chat` requests to the Backend.
- **Orchestrator:** The `AgentOrchestrator` determines if a message should trigger a tool or just converse. It fetches historical conversation context + vectorized memory context.
- **Memories:** User memories are stored in Postgres as `double[]` arrays via JSON parsing. Cosine similarity matches relevant memories to inject into the LLM context.
- **Scheduler:** `NotificationScheduler` runs every minute checking due reminders and dispatching FCM notifications.

## Premium UI
The frontend uses custom vanilla CSS tokens (`var(--primary)`, `var(--bg)`, etc.) with a dark glassmorphism theme, smooth animations, and a responsive grid dashboard. It looks exactly like a high-end mobile-first life companion app!
