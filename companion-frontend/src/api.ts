import axios from 'axios';

// Use environment variable or default to Railway backend
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://buddyai-production-2255.railway.app/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// For MVP, we use a fixed user ID
const USER_ID = 1;

export const chatWithAi = async (message: string) => {
  const res = await api.post('/chat', { userId: USER_ID, message });
  return res.data;
};

export const fetchGoals = async () => {
  const res = await api.get(`/goals?userId=${USER_ID}`);
  return res.data;
};

export const createGoal = async (goal: any) => {
  const res = await api.post('/goals', { ...goal, userId: USER_ID });
  return res.data;
};

export const fetchReminders = async () => {
  const res = await api.get(`/reminders?userId=${USER_ID}`);
  return res.data;
};

export const createReminder = async (reminder: any) => {
  const res = await api.post('/reminders', { ...reminder, userId: USER_ID });
  return res.data;
};
