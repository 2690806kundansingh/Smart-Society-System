import React, { useState, useEffect } from 'react';
import { useAuthStore } from './store/authStore';
import { wsService } from './services/websocket';
import { NotificationItem } from './types';
import { Navbar } from './components/Navbar';
import { LoginModal } from './components/LoginModal';
import { GuideModal } from './components/GuideModal';
import { NotificationToast } from './components/NotificationToast';
import { ResidentPortal } from './components/ResidentPortal';
import { StaffTaskPanel } from './components/StaffTaskPanel';
import { AdminDashboard } from './components/AdminDashboard';
import { userApi } from './services/api';
import { 
  Building2, 
  ShieldCheck, 
  Wrench, 
  Home, 
  ArrowRight, 
  CheckCircle2, 
  Zap, 
  Bell,
  BookOpen
} from 'lucide-react';

export const App: React.FC = () => {
  const { user, isAuthenticated, setAuth } = useAuthStore();
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const [isGuideModalOpen, setIsGuideModalOpen] = useState(false);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [activeTab, setActiveTab] = useState<'resident' | 'staff' | 'admin'>('resident');

  // Handle OAuth2 Redirect callback (e.g. from Google or GitHub login)
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const oauth2Token = urlParams.get('oauth2_token');
    const refreshToken = urlParams.get('refreshToken');
    const oauth2Error = urlParams.get('oauth2_error');

    if (oauth2Error) {
      alert(`OAuth2 Sign-In notice: ${oauth2Error}`);
      window.history.replaceState({}, document.title, window.location.pathname);
      return;
    }

    if (oauth2Token) {
      // Temporarily store token so userApi.getCurrentUser() can authenticate
      localStorage.setItem('access_token', oauth2Token);
      if (refreshToken) {
        localStorage.setItem('refresh_token', refreshToken);
      }

      userApi.getCurrentUser()
        .then((currentUser) => {
          setAuth({
            accessToken: oauth2Token,
            refreshToken: refreshToken || '',
            tokenType: 'Bearer',
            expiresIn: 86400000,
            user: currentUser,
          });
        })
        .catch((err) => {
          console.error('Failed to load user profile after OAuth2 login:', err);
        })
        .finally(() => {
          window.history.replaceState({}, document.title, window.location.pathname);
        });
    }
  }, [setAuth]);

  // Sync active view tab with user's primary role on login
  useEffect(() => {
    if (user) {
      if (user.roles.includes('ROLE_ADMIN')) {
        setActiveTab('admin');
      } else if (user.roles.includes('ROLE_STAFF')) {
        setActiveTab('staff');
      } else {
        setActiveTab('resident');
      }

      // Connect STOMP WebSocket
      wsService.connect(user, (notification) => {
        setNotifications((prev) => [notification, ...prev]);
      });
    } else {
      wsService.disconnect();
    }

    return () => {
      wsService.disconnect();
    };
  }, [user]);

  const handleDismissNotification = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-100/70 text-slate-900">
      {/* Navbar */}
      <Navbar 
        onOpenSwitchPersona={() => setIsLoginModalOpen(true)} 
        onOpenGuide={() => setIsGuideModalOpen(true)}
      />

      {/* Main Content */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {!isAuthenticated || !user ? (
          /* HERO / LANDING PAGE WHEN NOT LOGGED IN */
          <div className="space-y-12 py-10">
            <div className="text-center max-w-3xl mx-auto space-y-4">
              <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
                <Zap className="w-3.5 h-3.5 mr-1" /> Enterprise Handover Grade Architecture
              </span>
              <h2 className="text-4xl sm:text-5xl font-extrabold text-slate-900 tracking-tight">
                Smart Society Complaint & Maintenance System
              </h2>
              <p className="text-base text-slate-600 leading-relaxed">
                Distributed microservices architecture powered by Spring Boot 3.3, Netflix Eureka,
                Spring Cloud Gateway, Apache Kafka event streams, Redis cache-aside, ShedLock SLA
                escalations, and React TypeScript.
              </p>
              <div className="pt-2 flex flex-wrap items-center justify-center gap-3">
                <button
                  onClick={() => setIsLoginModalOpen(true)}
                  className="inline-flex items-center px-6 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-lg hover:shadow-xl transition-all"
                >
                  Launch Interactive Demo <ArrowRight className="w-4 h-4 ml-2" />
                </button>

                <button
                  onClick={() => setIsGuideModalOpen(true)}
                  className="inline-flex items-center px-5 py-3 rounded-xl bg-white hover:bg-slate-50 text-slate-800 font-bold text-sm border border-slate-300 shadow-sm transition-all"
                >
                  <BookOpen className="w-4 h-4 mr-2 text-emerald-600" /> System Guide & Instructions
                </button>
              </div>
            </div>

            {/* Persona Cards for Quick Evaluation */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl mx-auto">
              <div
                onClick={() => setIsLoginModalOpen(true)}
                className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer space-y-3 group"
              >
                <div className="w-12 h-12 rounded-xl bg-purple-100 text-purple-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                  <ShieldCheck className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-bold text-slate-900">Admin Portal</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Real-time KPI metrics, category breakdowns, staff re-assignment, and immediate alerts on SLA breaches.
                </p>
                <div className="text-xs font-bold text-purple-600 flex items-center pt-2">
                  Test as Administrator →
                </div>
              </div>

              <div
                onClick={() => setIsLoginModalOpen(true)}
                className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer space-y-3 group"
              >
                <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                  <Wrench className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-bold text-slate-900">Staff Workstation</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Kanban task flow for assigned electrical and plumbing tickets. Accept tasks, monitor SLAs, and upload resolution proof.
                </p>
                <div className="text-xs font-bold text-amber-600 flex items-center pt-2">
                  Test as Maintenance Staff →
                </div>
              </div>

              <div
                onClick={() => setIsLoginModalOpen(true)}
                className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer space-y-3 group"
              >
                <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                  <Home className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-bold text-slate-900">Resident Experience</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  AI-driven priority prediction, real-time SLA countdown timers, live status steppers, and post-resolution 5-star ratings.
                </p>
                <div className="text-xs font-bold text-emerald-600 flex items-center pt-2">
                  Test as Resident →
                </div>
              </div>
            </div>
          </div>
        ) : (
          /* AUTHENTICATED USER PORTALS */
          <div className="space-y-6">
            {/* View Switcher Tabs (Allows exploring portals seamlessly) */}
            <div className="flex items-center space-x-2 border-b border-slate-200 pb-2">
              <button
                onClick={() => setActiveTab('resident')}
                className={`flex items-center px-4 py-2 text-xs font-bold rounded-lg transition-colors ${
                  activeTab === 'resident'
                    ? 'bg-emerald-600 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-200/60'
                }`}
              >
                <Home className="w-3.5 h-3.5 mr-1.5" /> Resident Portal
              </button>

              <button
                onClick={() => setActiveTab('staff')}
                className={`flex items-center px-4 py-2 text-xs font-bold rounded-lg transition-colors ${
                  activeTab === 'staff'
                    ? 'bg-amber-600 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-200/60'
                }`}
              >
                <Wrench className="w-3.5 h-3.5 mr-1.5" /> Staff Task Panel
              </button>

              <button
                onClick={() => setActiveTab('admin')}
                className={`flex items-center px-4 py-2 text-xs font-bold rounded-lg transition-colors ${
                  activeTab === 'admin'
                    ? 'bg-purple-600 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-200/60'
                }`}
              >
                <ShieldCheck className="w-3.5 h-3.5 mr-1.5" /> Admin Hub & KPIs
              </button>
            </div>

            {/* Active Portal Component */}
            {activeTab === 'resident' && (
              <ResidentPortal residentId={user.id} societyId={user.societyId} />
            )}

            {activeTab === 'staff' && (
              <StaffTaskPanel staffId={user.id} societyId={user.societyId} />
            )}

            {activeTab === 'admin' && (
              <AdminDashboard societyId={user.societyId} />
            )}
          </div>
        )}
      </main>

      {/* Floating Real-time STOMP Notification Toasts */}
      <NotificationToast
        notifications={notifications}
        onDismiss={handleDismissNotification}
      />

      {/* Quick Login / Persona Switcher Modal */}
      <LoginModal
        isOpen={isLoginModalOpen}
        onClose={() => setIsLoginModalOpen(false)}
      />

      {/* System Instructions & User Guide Modal */}
      <GuideModal
        isOpen={isGuideModalOpen}
        onClose={() => setIsGuideModalOpen(false)}
        onOpenLogin={() => setIsLoginModalOpen(true)}
      />

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500">
        <p>Smart Society Complaint & Maintenance System • Enterprise Handover Grade Architecture</p>
      </footer>
    </div>
  );
};
