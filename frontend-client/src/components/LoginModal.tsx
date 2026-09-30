import React, { useState } from 'react';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../services/api';
import { Shield, Wrench, Home, X, KeyRound, Loader2, UserPlus, LogIn } from 'lucide-react';

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose }) => {
  const { setAuth } = useAuthStore();
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [blockName, setBlockName] = useState('A');
  const [flatNumber, setFlatNumber] = useState('101');
  const [role, setRole] = useState('ROLE_RESIDENT');
  const [department, setDepartment] = useState('PLUMBING');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleOAuthLogin = (provider: 'google' | 'github') => {
    // Redirect through API Gateway / proxy to Spring Security OAuth2 authorization endpoint
    window.location.href = `/oauth2/authorization/${provider}`;
  };

  const handleLogin = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const response = await authApi.login({ email, password });
      setAuth(response);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Invalid email or password. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const response = await authApi.register({
        email,
        password,
        fullName,
        societyId: 1,
        role,
        blockName: role === 'ROLE_RESIDENT' ? blockName : undefined,
        flatNumber: role === 'ROLE_RESIDENT' ? flatNumber : undefined,
        department: role === 'ROLE_STAFF' ? department : undefined,
      });
      setAuth(response);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Registration failed. Email may already be taken.');
    } finally {
      setLoading(false);
    }
  };

  const selectDemoPersona = async (demoEmail: string) => {
    setEmail(demoEmail);
    setPassword('Password@123');
    setLoading(true);
    setError(null);
    try {
      const response = await authApi.login({ email: demoEmail, password: 'Password@123' });
      setAuth(response);
      onClose();
    } catch (err: any) {
      setError('Unable to log in with persona. Ensure backend services are running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up">
        {/* Header */}
        <div className="p-6 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white">
              <KeyRound className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900">
                {isRegisterMode ? 'Create New Account' : 'Sign In to Smart Society'}
              </h3>
              <p className="text-xs text-slate-500">
                Enterprise complaint and SLA management portal
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 p-1 rounded-lg"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-5">
          {error && (
            <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700">
              {error}
            </div>
          )}

          {/* Social OAuth2 Logins (Google & GitHub) */}
          <div className="space-y-2.5">
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-500">
              Single Sign-On (SSO)
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
              <button
                type="button"
                onClick={() => handleOAuthLogin('google')}
                className="w-full inline-flex items-center justify-center px-4 py-2.5 border border-slate-200 rounded-xl bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold shadow-sm transition-colors space-x-2 group"
              >
                <svg className="w-4 h-4" viewBox="0 0 24 24">
                  <path
                    fill="#4285F4"
                    d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.82-2.4 3.68v3.05h3.88c2.27-2.09 3.66-5.17 3.66-9.17z"
                  />
                  <path
                    fill="#34A853"
                    d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.05c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.25v3.15C3.26 21.36 7.33 24 12 24z"
                  />
                  <path
                    fill="#FBBC05"
                    d="M5.28 14.27c-.25-.72-.38-1.49-.38-2.27s.13-1.55.38-2.27V6.58H1.25C.45 8.18 0 9.97 0 12s.45 3.82 1.25 5.42l4.03-3.15z"
                  />
                  <path
                    fill="#EA4335"
                    d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.33 0 3.26 2.64 1.25 6.58l4.03 3.15c.95-2.83 3.6-4.98 6.72-4.98z"
                  />
                </svg>
                <span>Continue with Google</span>
              </button>

              <button
                type="button"
                onClick={() => handleOAuthLogin('github')}
                className="w-full inline-flex items-center justify-center px-4 py-2.5 border border-slate-800 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold shadow-sm transition-colors space-x-2"
              >
                <svg className="w-4 h-4 fill-current" viewBox="0 0 24 24">
                  <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
                </svg>
                <span>Login with GitHub</span>
              </button>
            </div>
          </div>

          <div className="text-center relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-slate-200"></div>
            </div>
            <span className="relative px-3 bg-white text-[11px] text-slate-400 font-medium uppercase">
              Or test with Demo Personas
            </span>
          </div>

          {/* Quick Demo Persona Switcher (High priority for testing!) */}
          {!isRegisterMode && (
            <div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => selectDemoPersona('admin@smartsociety.com')}
                  disabled={loading}
                  className="flex items-center p-2.5 rounded-lg border border-purple-200 bg-purple-50/60 hover:bg-purple-100 text-left transition-all group"
                >
                  <Shield className="w-4 h-4 text-purple-600 mr-2 flex-shrink-0" />
                  <div>
                    <div className="text-xs font-semibold text-purple-900">Admin</div>
                    <div className="text-[10px] text-purple-600">Full SLA control & KPIs</div>
                  </div>
                </button>

                <button
                  type="button"
                  onClick={() => selectDemoPersona('plumber@smartsociety.com')}
                  disabled={loading}
                  className="flex items-center p-2.5 rounded-lg border border-amber-200 bg-amber-50/60 hover:bg-amber-100 text-left transition-all group"
                >
                  <Wrench className="w-4 h-4 text-amber-600 mr-2 flex-shrink-0" />
                  <div>
                    <div className="text-xs font-semibold text-amber-900">Staff (Plumber)</div>
                    <div className="text-[10px] text-amber-600">Accept & complete tickets</div>
                  </div>
                </button>

                <button
                  type="button"
                  onClick={() => selectDemoPersona('electrician@smartsociety.com')}
                  disabled={loading}
                  className="flex items-center p-2.5 rounded-lg border border-amber-200 bg-amber-50/60 hover:bg-amber-100 text-left transition-all group"
                >
                  <Wrench className="w-4 h-4 text-amber-600 mr-2 flex-shrink-0" />
                  <div>
                    <div className="text-xs font-semibold text-amber-900">Staff (Electrician)</div>
                    <div className="text-[10px] text-amber-600">Accept & complete tickets</div>
                  </div>
                </button>

                <button
                  type="button"
                  onClick={() => selectDemoPersona('resident1@smartsociety.com')}
                  disabled={loading}
                  className="flex items-center p-2.5 rounded-lg border border-emerald-200 bg-emerald-50/60 hover:bg-emerald-100 text-left transition-all group"
                >
                  <Home className="w-4 h-4 text-emerald-600 mr-2 flex-shrink-0" />
                  <div>
                    <div className="text-xs font-semibold text-emerald-900">Resident (Flat A-101)</div>
                    <div className="text-[10px] text-emerald-600">Raise issues & track SLA</div>
                  </div>
                </button>
              </div>
            </div>
          )}

          <div className="text-center relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-slate-200"></div>
            </div>
            <span className="relative px-3 bg-white text-[11px] text-slate-400 font-medium uppercase">
              Or sign in with custom credentials
            </span>
          </div>

          {/* Form */}
          <form onSubmit={isRegisterMode ? handleRegister : handleLogin} className="space-y-4">
            {isRegisterMode && (
              <>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">Full Name</label>
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="e.g. John Doe"
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">Account Role</label>
                    <select
                      value={role}
                      onChange={(e) => setRole(e.target.value)}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                    >
                      <option value="ROLE_RESIDENT">Resident</option>
                      <option value="ROLE_STAFF">Staff</option>
                      <option value="ROLE_ADMIN">Admin</option>
                    </select>
                  </div>
                  {role === 'ROLE_STAFF' ? (
                    <div>
                      <label className="block text-xs font-medium text-slate-700 mb-1">Department</label>
                      <select
                        value={department}
                        onChange={(e) => setDepartment(e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                      >
                        <option value="PLUMBING">Plumbing</option>
                        <option value="ELECTRICAL">Electrical</option>
                        <option value="CLEANING">Cleaning</option>
                        <option value="GENERAL">General</option>
                      </select>
                    </div>
                  ) : (
                    <div className="grid grid-cols-2 gap-1.5">
                      <div>
                        <label className="block text-xs font-medium text-slate-700 mb-1">Block</label>
                        <input
                          type="text"
                          value={blockName}
                          onChange={(e) => setBlockName(e.target.value)}
                          className="w-full px-2 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-medium text-slate-700 mb-1">Flat</label>
                        <input
                          type="text"
                          value={flatNumber}
                          onChange={(e) => setFlatNumber(e.target.value)}
                          className="w-full px-2 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                        />
                      </div>
                    </div>
                  )}
                </div>
              </>
            )}

            <div>
              <label className="block text-xs font-medium text-slate-700 mb-1">Email Address</label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@smartsociety.com"
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-700 mb-1">Password</label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-lg text-sm transition-colors shadow-sm flex items-center justify-center space-x-2 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Processing...</span>
                </>
              ) : isRegisterMode ? (
                <>
                  <UserPlus className="w-4 h-4" />
                  <span>Register Account</span>
                </>
              ) : (
                <>
                  <LogIn className="w-4 h-4" />
                  <span>Sign In</span>
                </>
              )}
            </button>
          </form>

          {/* Toggle Register / Login */}
          <div className="text-center pt-2">
            <button
              type="button"
              onClick={() => {
                setIsRegisterMode(!isRegisterMode);
                setError(null);
              }}
              className="text-xs text-emerald-700 hover:text-emerald-800 font-semibold"
            >
              {isRegisterMode
                ? 'Already have an account? Sign in here'
                : 'Need an account? Register as Resident or Staff'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
