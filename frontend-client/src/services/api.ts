import axios, { InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../store/authStore';
import { 
  AuthResponse, 
  Complaint, 
  Category, 
  DashboardStats, 
  FeedbackRating,
  User 
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || '';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT Bearer token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().accessToken;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token refresh on 401
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      const refreshToken = useAuthStore.getState().refreshToken;

      if (!refreshToken || originalRequest.url?.includes('/api/v1/auth/')) {
        useAuthStore.getState().logout();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const response = await axios.post<AuthResponse>(`${API_BASE_URL}/api/v1/auth/refresh`, {
          refreshToken,
        });

        const newAuth = response.data;
        useAuthStore.getState().setAuth(newAuth);
        processQueue(null, newAuth.accessToken);

        originalRequest.headers.Authorization = `Bearer ${newAuth.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        useAuthStore.getState().logout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

// API Service Endpoints
export const authApi = {
  login: async (credentials: { email: string; password: string }) => {
    const res = await api.post<AuthResponse>('/api/v1/auth/login', credentials);
    return res.data;
  },
  register: async (data: Record<string, unknown>) => {
    const res = await api.post<AuthResponse>('/api/v1/auth/register', data);
    return res.data;
  },
  sendOtp: async (data: { identifier: string; type?: string }) => {
    const res = await api.post<{ message: string; identifier: string; success: boolean; debugOtp?: string }>('/api/v1/auth/otp/send', data);
    return res.data;
  },
  verifyOtp: async (data: { identifier: string; otp: string }) => {
    const res = await api.post<AuthResponse>('/api/v1/auth/otp/verify', data);
    return res.data;
  },
};

export const complaintApi = {
  getCategories: async (): Promise<Category[]> => {
    const res = await api.get<Category[]>('/api/v1/categories');
    return res.data;
  },
  getComplaints: async (params?: { societyId?: number; residentId?: number; staffId?: number }): Promise<Complaint[]> => {
    const res = await api.get<Complaint[]>('/api/v1/complaints', { params });
    return res.data;
  },
  getComplaint: async (id: number): Promise<Complaint> => {
    const res = await api.get<Complaint>(`/api/v1/complaints/${id}`);
    return res.data;
  },
  createComplaint: async (data: {
    categoryId: number;
    title: string;
    description: string;
    locationDetails?: string;
    photoUrl?: string;
  }): Promise<Complaint> => {
    const res = await api.post<Complaint>('/api/v1/complaints', data);
    return res.data;
  },
  assignStaff: async (id: number, data: { staffId: number; notes?: string }): Promise<Complaint> => {
    const res = await api.post<Complaint>(`/api/v1/complaints/${id}/assign`, data);
    return res.data;
  },
  updateStatus: async (id: number, data: { status: string; notes?: string; resolutionPhotoUrl?: string }): Promise<Complaint> => {
    const res = await api.patch<Complaint>(`/api/v1/complaints/${id}/status`, data);
    return res.data;
  },
  submitFeedback: async (id: number, data: { rating: number; review?: string }): Promise<FeedbackRating> => {
    const res = await api.post<FeedbackRating>(`/api/v1/complaints/${id}/feedback`, data);
    return res.data;
  },
  predictPriority: async (data: { title: string; description: string; categoryId?: number }) => {
    const res = await api.post<{ priority: string; slaHours: number; reason: string }>(
      '/api/v1/complaints/predict-priority',
      data
    );
    return res.data;
  },
  getEscalated: async (societyId: number): Promise<Complaint[]> => {
    const res = await api.get<Complaint[]>(`/api/v1/complaints/society/${societyId}/escalated`);
    return res.data;
  },
  getStats: async (societyId: number): Promise<DashboardStats> => {
    const res = await api.get<DashboardStats>(`/api/v1/complaints/society/${societyId}/stats`);
    return res.data;
  },
};

export const userApi = {
  getStaff: async (societyId: number, department?: string): Promise<User[]> => {
    const res = await api.get<User[]>('/api/v1/users/staff', {
      params: { societyId, department },
    });
    return res.data;
  },
  getResidents: async (societyId: number): Promise<User[]> => {
    const res = await api.get<User[]>(`/api/v1/users/society/${societyId}/residents`);
    return res.data;
  },
  getCurrentUser: async (): Promise<User> => {
    const res = await api.get<User>('/api/v1/users/me');
    return res.data;
  },
};
