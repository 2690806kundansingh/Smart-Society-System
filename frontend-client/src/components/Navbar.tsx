import React from 'react';
import { useAuthStore } from '../store/authStore';
import { Building2, User, LogOut, Shield, Wrench, Home, Users } from 'lucide-react';

interface NavbarProps {
  onOpenSwitchPersona: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenSwitchPersona }) => {
  const { user, logout } = useAuthStore();

  const getRoleBadge = () => {
    if (!user) return null;
    const isResident = user.roles.includes('ROLE_RESIDENT');
    const isStaff = user.roles.includes('ROLE_STAFF');
    const isAdmin = user.roles.includes('ROLE_ADMIN');

    if (isAdmin) {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
          <Shield className="w-3 h-3 mr-1" /> Admin
        </span>
      );
    }
    if (isStaff) {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800">
          <Wrench className="w-3 h-3 mr-1" /> Staff ({user.department || 'General'})
        </span>
      );
    }
    return (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
        <Home className="w-3 h-3 mr-1" /> Resident
      </span>
    );
  };

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-40 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-500 flex items-center justify-center text-white shadow-md">
            <Building2 className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-slate-900 tracking-tight leading-tight">
              Smart Society
            </h1>
            <p className="text-[11px] text-slate-500 font-medium">Complaint & SLA Maintenance</p>
          </div>
        </div>

        {/* Current User & Actions */}
        {user ? (
          <div className="flex items-center space-x-4">
            <div className="hidden sm:flex flex-col items-end text-right">
              <div className="flex items-center space-x-2">
                <span className="text-sm font-semibold text-slate-800">{user.fullName}</span>
                {getRoleBadge()}
              </div>
              <span className="text-xs text-slate-500">
                {user.apartment || user.email} • Society #{user.societyId}
              </span>
            </div>

            {/* Quick Demo Persona Switcher */}
            <button
              onClick={onOpenSwitchPersona}
              className="inline-flex items-center px-3 py-1.5 text-xs font-medium rounded-lg text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 transition-colors shadow-sm"
              title="Switch demo user"
            >
              <Users className="w-3.5 h-3.5 mr-1.5 text-slate-600" />
              Switch Persona
            </button>

            {/* Logout */}
            <button
              onClick={logout}
              className="p-2 text-slate-500 hover:text-red-600 rounded-lg hover:bg-slate-100 transition-colors"
              title="Sign out"
            >
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        ) : (
          <div className="flex items-center space-x-3">
            <button
              onClick={onOpenSwitchPersona}
              className="inline-flex items-center px-4 py-2 text-sm font-medium rounded-lg text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm transition-colors"
            >
              <User className="w-4 h-4 mr-2" />
              Sign In / Select Persona
            </button>
          </div>
        )}
      </div>
    </header>
  );
};
