import React, { useState } from 'react';
import { 
  X, 
  BookOpen, 
  KeyRound, 
  PlusCircle, 
  Camera, 
  Clock, 
  ShieldCheck, 
  Wrench, 
  CheckCircle2, 
  Zap, 
  Mail, 
  Phone, 
  Sparkles,
  ArrowRight,
  Shield,
  Home
} from 'lucide-react';

interface GuideModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenLogin: () => void;
}

export const GuideModal: React.FC<GuideModalProps> = ({ isOpen, onClose, onOpenLogin }) => {
  const [activeTab, setActiveTab] = useState<'OVERVIEW' | 'LOGIN' | 'RAISE' | 'STAFF_ADMIN'>('OVERVIEW');

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-3xl w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up my-8">
        
        {/* Header */}
        <div className="p-6 bg-gradient-to-r from-emerald-900 via-slate-900 to-emerald-950 text-white flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-400/30 flex items-center justify-center text-emerald-400">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-bold">Smart Society System — User Guide & Instructions</h2>
              <p className="text-xs text-emerald-300">How this project works & complete walkthrough for all users</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b border-slate-200 bg-slate-50 px-6 pt-3 space-x-2 overflow-x-auto">
          <button
            type="button"
            onClick={() => setActiveTab('OVERVIEW')}
            className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'OVERVIEW'
                ? 'border-emerald-600 text-emerald-700'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>1. What is this App?</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('LOGIN')}
            className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'LOGIN'
                ? 'border-emerald-600 text-emerald-700'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <KeyRound className="w-3.5 h-3.5" />
            <span>2. How to Log In (5 Ways)</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('RAISE')}
            className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'RAISE'
                ? 'border-emerald-600 text-emerald-700'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Camera className="w-3.5 h-3.5" />
            <span>3. Raise Issue & Photo Capture</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('STAFF_ADMIN')}
            className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'STAFF_ADMIN'
                ? 'border-emerald-600 text-emerald-700'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>4. Staff & Admin Controls</span>
          </button>
        </div>

        {/* Tab Body */}
        <div className="p-6 max-h-[60vh] overflow-y-auto space-y-6">

          {/* TAB 1: OVERVIEW */}
          {activeTab === 'OVERVIEW' && (
            <div className="space-y-4">
              <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-950 space-y-2">
                <h3 className="text-sm font-bold flex items-center">
                  <Zap className="w-4 h-4 text-emerald-600 mr-1.5" /> Enterprise Society Complaint & SLA Management System
                </h3>
                <p className="text-xs text-emerald-800 leading-relaxed">
                  This system is an automated, real-time maintenance portal for residential societies and commercial complexes. 
                  It allows residents to report issues, automatically predicts SLA repair deadlines using AI keyword analysis, notifies staff, and alerts administrators if repair deadlines are breached.
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-2 shadow-xs">
                  <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold text-xs">
                    1
                  </div>
                  <h4 className="text-xs font-bold text-slate-900">Resident Experience</h4>
                  <p className="text-[11px] text-slate-500 leading-relaxed">
                    Submit maintenance complaints, snap photos directly from camera, track repair progress with live countdown timers.
                  </p>
                </div>

                <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-2 shadow-xs">
                  <div className="w-8 h-8 rounded-lg bg-amber-100 text-amber-700 flex items-center justify-center font-bold text-xs">
                    2
                  </div>
                  <h4 className="text-xs font-bold text-slate-900">Staff Workstation</h4>
                  <p className="text-[11px] text-slate-500 leading-relaxed">
                    Technicians (plumbers, electricians) accept assigned tickets, view issue location, and upload repair proof photos.
                  </p>
                </div>

                <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-2 shadow-xs">
                  <div className="w-8 h-8 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center font-bold text-xs">
                    3
                  </div>
                  <h4 className="text-xs font-bold text-slate-900">Admin KPI Dashboard</h4>
                  <p className="text-[11px] text-slate-500 leading-relaxed">
                    Society management views total complaints, resolution rates, re-assigns staff, and gets email alerts on SLA breaches.
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: LOGIN WAYS */}
          {activeTab === 'LOGIN' && (
            <div className="space-y-4">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                5 Simple Ways to Sign In
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div className="p-3.5 rounded-xl border border-purple-200 bg-purple-50/50 space-y-1">
                  <div className="flex items-center text-xs font-bold text-purple-900">
                    <Shield className="w-4 h-4 text-purple-600 mr-1.5" /> 1-Click Demo Personas (Fastest)
                  </div>
                  <p className="text-[11px] text-purple-700">
                    Click <strong>Admin</strong> or <strong>Resident</strong> on the sign-in modal to instantly test the portal without typing passwords.
                  </p>
                </div>

                <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 space-y-1">
                  <div className="flex items-center text-xs font-bold text-slate-900">
                    <Mail className="w-4 h-4 text-emerald-600 mr-1.5" /> Gmail OTP (Email Code)
                  </div>
                  <p className="text-[11px] text-slate-600">
                    Enter any Gmail address to receive a 6-digit OTP code for instant passwordless sign-in and auto-registration.
                  </p>
                </div>

                <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 space-y-1">
                  <div className="flex items-center text-xs font-bold text-slate-900">
                    <Phone className="w-4 h-4 text-blue-600 mr-1.5" /> Phone Number OTP
                  </div>
                  <p className="text-[11px] text-slate-600">
                    Enter your mobile number to receive a 6-digit OTP verification code.
                  </p>
                </div>

                <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 space-y-1">
                  <div className="flex items-center text-xs font-bold text-slate-900">
                    <KeyRound className="w-4 h-4 text-amber-600 mr-1.5" /> Google / GitHub SSO
                  </div>
                  <p className="text-[11px] text-slate-600">
                    1-Click Social OAuth sign in with your Google or GitHub account.
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* TAB 3: RAISE ISSUE & PHOTO CAPTURE */}
          {activeTab === 'RAISE' && (
            <div className="space-y-4">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                How Residents Raise Issues & Attach Photos
              </h3>

              <div className="p-4 rounded-xl border border-emerald-200 bg-emerald-50/60 space-y-3">
                <div className="flex items-center text-xs font-bold text-emerald-950 space-x-2">
                  <Camera className="w-4 h-4 text-emerald-600" />
                  <span>Dual Photo Attachment Options</span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                  <div className="bg-white p-3 rounded-lg border border-emerald-200 space-y-1">
                    <span className="font-bold text-emerald-800">📷 Option 1: Direct Camera</span>
                    <p className="text-[11px] text-slate-600">
                      Click "Direct Camera" to snap a photo immediately using your phone camera or laptop webcam feed.
                    </p>
                  </div>
                  <div className="bg-white p-3 rounded-lg border border-emerald-200 space-y-1">
                    <span className="font-bold text-emerald-800">🖼️ Option 2: Phone Gallery / Files</span>
                    <p className="text-[11px] text-slate-600">
                      Click "Gallery / File" to select any existing photo saved on your mobile device or computer.
                    </p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <h4 className="text-xs font-bold text-slate-800">AI Priority & SLA Countdown:</h4>
                <p className="text-xs text-slate-600 leading-relaxed">
                  As you type emergency words like <em>"flood", "spark", "smoke", "leakage", "stuck"</em>, the system automatically elevates priority to <strong>HIGH</strong> and assigns a strict 4-hour SLA deadline!
                </p>
              </div>
            </div>
          )}

          {/* TAB 4: STAFF & ADMIN CONTROLS */}
          {activeTab === 'STAFF_ADMIN' && (
            <div className="space-y-4">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Staff & Administrator Workflows
              </h3>

              <div className="space-y-3 text-xs text-slate-700">
                <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 flex items-start space-x-3">
                  <Wrench className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <span className="font-bold text-slate-900">Staff Workstation:</span>
                    <p className="text-[11px] text-slate-600 mt-0.5">
                      Technicians open the <strong>Staff Task Panel</strong> to view incoming tickets assigned to their department (Plumbing, Electrical, Elevator). They accept tickets, fix issues, and upload a repair completion photo.
                    </p>
                  </div>
                </div>

                <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 flex items-start space-x-3">
                  <ShieldCheck className="w-5 h-5 text-purple-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <span className="font-bold text-slate-900">Admin SLA Monitoring:</span>
                    <p className="text-[11px] text-slate-600 mt-0.5">
                      Admins use the <strong>Admin Dashboard</strong> to monitor real-time SLA compliance, view resolution rates, create new categories, and re-assign unassigned tickets.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}

        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-slate-50 border-t border-slate-200 flex items-center justify-between">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 border border-slate-300 text-slate-700 text-xs font-semibold rounded-xl hover:bg-slate-100"
          >
            Close Guide
          </button>

          <button
            type="button"
            onClick={() => {
              onClose();
              onOpenLogin();
            }}
            className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-md flex items-center space-x-1.5"
          >
            <span>Open Login / Demo Switcher</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

      </div>
    </div>
  );
};
