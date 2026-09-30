import { create } from 'zustand';
import { User, AuthResponse } from '../types';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  setAuth: (authData: AuthResponse) => void;
  updateUser: (user: User) => void;
  logout: () => void;
}

const getStoredAuth = () => {
  try {
    const token = localStorage.getItem('access_token');
    const refreshToken = localStorage.getItem('refresh_token');
    const userJson = localStorage.getItem('user_profile');
    if (token && userJson) {
      return {
        accessToken: token,
        refreshToken: refreshToken,
        user: JSON.parse(userJson) as User,
        isAuthenticated: true,
      };
    }
  } catch (e) {
    console.error('Failed to load auth from storage', e);
  }
  return {
    accessToken: null,
    refreshToken: null,
    user: null,
    isAuthenticated: false,
  };
};

export const useAuthStore = create<AuthState>((set) => ({
  ...getStoredAuth(),

  setAuth: (authData: AuthResponse) => {
    localStorage.setItem('access_token', authData.accessToken);
    localStorage.setItem('refresh_token', authData.refreshToken);
    localStorage.setItem('user_profile', JSON.stringify(authData.user));
    set({
      accessToken: authData.accessToken,
      refreshToken: authData.refreshToken,
      user: authData.user,
      isAuthenticated: true,
    });
  },

  updateUser: (user: User) => {
    localStorage.setItem('user_profile', JSON.stringify(user));
    set({ user });
  },

  logout: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_profile');
    set({
      accessToken: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,
    });
  },
}));
