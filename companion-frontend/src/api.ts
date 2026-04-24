import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
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
