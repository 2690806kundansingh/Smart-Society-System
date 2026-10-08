import React, { useState } from 'react';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../services/api';
import { Shield, Wrench, Home, X, KeyRound, Loader2, UserPlus, LogIn, Mail, Phone, Send, CheckCircle2 } from 'lucide-react';

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}

type AuthMethod = 'PASSWORD' | 'GMAIL_OTP' | 'PHONE_OTP';

export const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose }) => {
  const { setAuth } = useAuthStore();
  const [authMethod, setAuthMethod] = useState<AuthMethod>('PASSWORD');
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  
  // Standard Password Auth fields
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [blockName, setBlockName] = useState('A');
  const [flatNumber, setFlatNumber] = useState('101');
  const [role, setRole] = useState('ROLE_RESIDENT');
  const [department, setDepartment] = useState('PLUMBING');

  // OTP Auth fields
  const [otpIdentifier, setOtpIdentifier] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [otpMessage, setOtpMessage] = useState<string | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleOAuthLogin = (provider: 'google' | 'github') => {
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

  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!otpIdentifier) {
      setError(authMethod === 'GMAIL_OTP' ? 'Please enter a valid Gmail / Email address' : 'Please enter a valid Phone Number');
      return;
    }
    setLoading(true);
    setError(null);
    setOtpMessage(null);
    try {
      const res = await authApi.sendOtp({
        identifier: otpIdentifier,
        type: authMethod === 'GMAIL_OTP' ? 'EMAIL' : 'PHONE'
      });
      setOtpSent(true);
      setOtpMessage(res.message);
      if (res.debugOtp) {
        setOtpCode(res.debugOtp);
      }
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Failed to send OTP verification code.');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!otpCode || otpCode.length < 6) {
      setError('Please enter the 6-digit verification code.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await authApi.verifyOtp({
        identifier: otpIdentifier,
        otp: otpCode
      });
      setAuth(response);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Invalid or expired OTP code.');
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

          {/* Authentication Method Selector Tabs */}
          {!isRegisterMode && (
            <div className="flex bg-slate-100 p-1 rounded-xl">
              <button
                type="button"
                onClick={() => { setAuthMethod('PASSWORD'); setError(null); }}
                className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                  authMethod === 'PASSWORD' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'
                }`}
              >
                <KeyRound className="w-3.5 h-3.5" />
                <span>Password</span>
              </button>
              <button
                type="button"
                onClick={() => { 
                  setAuthMethod('GMAIL_OTP'); 
                  setError(null); 
                  setOtpSent(false); 
                  setOtpMessage(null);
                  setOtpIdentifier('chandansingh2690806@gmail.com');
                }}
                className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                  authMethod === 'GMAIL_OTP' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'
                }`}
              >
                <Mail className="w-3.5 h-3.5" />
                <span>Gmail OTP</span>
              </button>
              <button
                type="button"
                onClick={() => { 
                  setAuthMethod('PHONE_OTP'); 
                  setError(null); 
                  setOtpSent(false); 
                  setOtpMessage(null);
                  setOtpIdentifier('+919876543210');
                }}
                className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                  authMethod === 'PHONE_OTP' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'
                }`}
              >
                <Phone className="w-3.5 h-3.5" />
                <span>Phone OTP</span>
              </button>
            </div>
          )}

          {/* Social OAuth2 Logins (Google & GitHub) */}
          {!isRegisterMode && authMethod === 'PASSWORD' && (
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
                  <span>Continue with GitHub</span>
                </button>
              </div>
            </div>
          )}

          {/* Quick Demo Persona Switcher */}
          {!isRegisterMode && authMethod === 'PASSWORD' && (
            <div>
              <div className="text-center relative mb-2.5">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-200"></div>
                </div>
                <span className="relative px-3 bg-white text-[11px] text-slate-400 font-medium uppercase">
                  Or test with Demo Personas
                </span>
              </div>
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

          {/* GMAIL OTP OR PHONE OTP AUTHENTICATION FORM */}
          {!isRegisterMode && (authMethod === 'GMAIL_OTP' || authMethod === 'PHONE_OTP') && (
            <div className="space-y-4">
              {otpMessage && (
                <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-xs text-emerald-800 flex items-start space-x-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <div className="font-semibold">{otpMessage}</div>
                    <div className="text-[11px] text-emerald-600 mt-0.5">
                      Enter the 6-digit verification code to complete sign-in.
                    </div>
                  </div>
                </div>
              )}

              {!otpSent ? (
                <form onSubmit={handleSendOtp} className="space-y-4">
                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      {authMethod === 'GMAIL_OTP' ? 'Gmail / Email Address' : 'Mobile Phone Number'}
                    </label>
                    <div className="relative">
                      {authMethod === 'GMAIL_OTP' ? (
                        <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                      ) : (
                        <Phone className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                      )}
                      <input
                        type={authMethod === 'GMAIL_OTP' ? 'email' : 'tel'}
                        required
                        value={otpIdentifier}
                        onChange={(e) => setOtpIdentifier(e.target.value)}
                        placeholder={authMethod === 'GMAIL_OTP' ? 'your-name@gmail.com' : '+91-9876543210'}
                        className="w-full pl-9 pr-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-2.5 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-lg text-sm transition-colors shadow-sm flex items-center justify-center space-x-2 disabled:opacity-50"
                  >
                    {loading ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Sending Code...</span>
                      </>
                    ) : (
                      <>
                        <Send className="w-4 h-4" />
                        <span>Send 6-Digit OTP</span>
                      </>
                    )}
                  </button>
                </form>
              ) : (
                <form onSubmit={handleVerifyOtp} className="space-y-4">
                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      Enter 6-Digit OTP Code
                    </label>
                    <input
                      type="text"
                      required
                      maxLength={6}
                      value={otpCode}
                      onChange={(e) => setOtpCode(e.target.value)}
                      placeholder="123456"
                      className="w-full px-3 py-2.5 border border-slate-300 rounded-lg text-lg font-mono tracking-widest text-center focus:ring-2 focus:ring-emerald-500 focus:outline-none"
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
                        <span>Verifying...</span>
                      </>
                    ) : (
                      <>
                        <CheckCircle2 className="w-4 h-4" />
                        <span>Verify & Sign In</span>
                      </>
                    )}
                  </button>

                  <div className="text-center">
                    <button
                      type="button"
                      onClick={() => setOtpSent(false)}
                      className="text-xs text-slate-500 hover:text-slate-800 underline"
                    >
                      Change {authMethod === 'GMAIL_OTP' ? 'Email' : 'Phone Number'} or Resend OTP
                    </button>
                  </div>
                </form>
              )}
            </div>
          )}

          {/* STANDARD PASSWORD LOGIN / REGISTER FORM */}
          {(isRegisterMode || authMethod === 'PASSWORD') && (
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
          )}

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
