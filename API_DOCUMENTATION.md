# 📚 BuddyAI - API Documentation

## Base URL

```
Development:  http://localhost:8080/api
Production:   https://api.buddyai.com/api
```

---

## 🔐 Authentication

Currently, the API is open. In production, implement JWT:

```java
// TODO: Add JWT authentication
Authorization: Bearer <jwt-token>
```

---

## 📤 Response Format

All responses follow this format:

### Success Response (200)
```json
{
  "success": true,
  "data": { /* response data */ },
  "timestamp": 1682000000000
}
```

### Error Response (4xx, 5xx)
```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": 1682000000000
}
```

---

## 💬 Chat Endpoints

### POST /chat
Process a user message and get AI response.

**Request:**
```json
{
  "userId": 1,
  "message": "What should I do today?"
}
```

**Response (200):**
```json
{
  "response": "Based on your goals, I suggest...",
  "timestamp": 1682000000000
}
```

**Errors:**
- 400: Invalid request (empty message, missing userId)
- 404: User not found
- 500: Internal server error

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "message": "Hello"}'
```

---

### GET /chat/history/:userId
Get conversation history for a user.

**Response (200):**
```json
[
  {
    "id": 1,
    "userMessage": "What are my goals?",
    "aiResponse": "You have 3 goals...",
    "createdAt": "2024-04-23T10:30:00"
  }
]
```

---

## 🎯 Goal Endpoints

### POST /goal
Create a new goal.

**Request:**
```json
{
  "userId": 1,
  "title": "Learn React",
  "description": "Complete a React course",
  "priority": "HIGH",
  "dueDate": "2024-06-30"
}
```

**Response (201):**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Learn React",
  "status": "ACTIVE",
  "createdAt": "2024-04-23T10:30:00"
}
```

---

### GET /goal/:id
Get a specific goal.

**Response (200):**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Learn React",
  "description": "Complete a React course",
  "status": "ACTIVE",
  "priority": "HIGH",
  "dueDate": "2024-06-30",
  "createdAt": "2024-04-23T10:30:00",
  "updatedAt": "2024-04-23T10:30:00"
}
```

---

### GET /goal/user/:userId
Get all goals for a user.

**Response (200):**
```json
[
  {
    "id": 1,
    "title": "Learn React",
    "status": "ACTIVE",
    "priority": "HIGH"
  },
  {
    "id": 2,
    "title": "Run a marathon",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM"
  }
]
```

---

### PUT /goal/:id
Update a goal.

**Request:**
```json
{
  "title": "Learn React & Vue",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM"
}
```

**Response (200):** Updated goal object

---

### DELETE /goal/:id
Delete a goal.

**Response (204):** No content

---

## 🔔 Reminder Endpoints

### POST /reminder
Create a reminder.

**Request:**
```json
{
  "userId": 1,
  "title": "Team meeting",
  "description": "Weekly sync with team",
  "reminderTime": "2024-04-24T10:00:00",
  "frequency": "WEEKLY"
}
```

**Response (201):**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Team meeting",
  "reminderTime": "2024-04-24T10:00:00",
  "frequency": "WEEKLY",
  "isSent": false,
  "createdAt": "2024-04-23T10:30:00"
}
```

---

### GET /reminder/user/:userId
Get all reminders for a user.

**Response (200):**
```json
[
  {
    "id": 1,
    "title": "Team meeting",
    "reminderTime": "2024-04-24T10:00:00",
    "frequency": "WEEKLY",
    "isSent": false
  }
]
```

---

### PUT /reminder/:id
Update a reminder.

**Response (200):** Updated reminder object

---

### DELETE /reminder/:id
Delete a reminder.

**Response (204):** No content

---

## 📊 Dashboard Endpoints

### GET /dashboard/:userId
Get dashboard data (goals + reminders summary).

**Response (200):**
```json
{
  "userId": 1,
  "goalsCount": 5,
  "completedGoalsCount": 2,
  "activeReminders": 3,
  "goals": [
    {
      "id": 1,
      "title": "Learn React",
      "status": "ACTIVE",
      "priority": "HIGH"
    }
  ],
  "upcomingReminders": [
    {
      "id": 1,
      "title": "Team meeting",
      "reminderTime": "2024-04-24T10:00:00"
    }
  ]
}
```

---

## 🧠 Memory/Embedding Endpoints (Advanced)

### POST /memory
Store a memory.

**Request:**
```json
{
  "userId": 1,
  "content": "User prefers morning meetings"
}
```

**Response (201):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "User prefers morning meetings",
  "embedding": [0.1, 0.2, 0.3, ...],
  "createdAt": "2024-04-23T10:30:00"
}
```

---

### POST /memory/search
Search memories by similarity.

**Request:**
```json
{
  "userId": 1,
  "query": "meeting preferences",
  "topK": 5
}
```

**Response (200):**
```json
[
  {
    "id": 1,
    "content": "User prefers morning meetings",
    "similarity": 0.92
  },
  {
    "id": 2,
    "content": "User likes video calls",
    "similarity": 0.78
  }
]
```

---

## ❌ Error Codes

| Code | Meaning | Solution |
|------|---------|----------|
| 400 | Bad Request | Check request format, validate all required fields |
| 404 | Not Found | Verify ID exists, check resource path |
| 422 | Unprocessable Entity | Validation error - check field constraints |
| 500 | Internal Server Error | Check server logs, contact support |
| 503 | Service Unavailable | Server is down, try again later |

---

## 🔄 Request Examples (Postman/cURL)

### Create Goal
```bash
curl -X POST http://localhost:8080/api/goal \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Learn Python",
    "description": "Complete Python course",
    "priority": "HIGH",
    "dueDate": "2024-06-30"
  }'
```

### Chat with AI
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "What are my goals for this month?"
  }'
```

### Get All Goals
```bash
curl -X GET http://localhost:8080/api/goal/user/1
```

### Create Reminder
```bash
curl -X POST http://localhost:8080/api/reminder \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Buy groceries",
    "reminderTime": "2024-04-25T18:00:00",
    "frequency": "ONCE"
  }'
```

---

## 📝 Rate Limiting (Future)

When rate limiting is implemented:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1682086400
```

---

## 🔄 Pagination (Future)

When pagination is added:
```
GET /goals/user/1?page=1&size=10&sort=createdAt,desc
```

Response:
```json
{
  "content": [...],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 1,
  "pageSize": 10
}
```

---

## 📞 Support

For issues or questions:
- GitHub Issues: https://github.com/yourusername/buddy-ai/issues
- Email: support@buddyai.com
- Discord: [Join our community](#)

---

**Last Updated:** April 23, 2024
**API Version:** 1.0.0
