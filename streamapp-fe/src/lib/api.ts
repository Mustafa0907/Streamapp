import axios from 'axios';
import { useAuthStore } from '../store/authStore';

// Create an axios instance without a baseURL.
// All requests will use relative paths, which the Vite proxy will catch.
const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
});

// The interceptor remains the same and is still crucial.
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token && !config.url?.startsWith('/auth')) { // Don't add auth header to login/register
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;