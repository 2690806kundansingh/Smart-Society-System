import React from 'react';
import { NotificationItem } from '../types';
import { AlertTriangle, CheckCircle, Info, X } from 'lucide-react';

interface ToastProps {
  notifications: NotificationItem[];
  onDismiss: (id: string) => void;
}

export const NotificationToast: React.FC<ToastProps> = ({ notifications, onDismiss }) => {
  if (notifications.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col space-y-2 max-w-sm w-full">
      {notifications.map((item) => {
        const isUrgent = item.type === 'URGENT';
        const isSuccess = item.type === 'SUCCESS';
        const isWarning = item.type === 'WARNING';

        return (
          <div
            key={item.id}
            className={`flex items-start p-4 rounded-lg shadow-lg border transition-all duration-300 animate-slide-in ${
              isUrgent
                ? 'bg-red-50 border-red-200 text-red-900'
                : isSuccess
                ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
                : isWarning
                ? 'bg-amber-50 border-amber-200 text-amber-900'
                : 'bg-blue-50 border-blue-200 text-blue-900'
            }`}
          >
            <div className="flex-shrink-0 mr-3 mt-0.5">
              {isUrgent || isWarning ? (
                <AlertTriangle className={`w-5 h-5 ${isUrgent ? 'text-red-600' : 'text-amber-600'}`} />
              ) : isSuccess ? (
                <CheckCircle className="w-5 h-5 text-emerald-600" />
              ) : (
                <Info className="w-5 h-5 text-blue-600" />
              )}
            </div>
            <div className="flex-1 min-w-0">
              <h4 className="text-sm font-semibold">{item.title}</h4>
              <p className="text-xs mt-0.5 opacity-90 leading-relaxed">{item.message}</p>
              <span className="text-[10px] text-gray-500 mt-1 block">
                {new Date(item.timestamp).toLocaleTimeString()}
              </span>
            </div>
            <button
              onClick={() => onDismiss(item.id)}
              className="flex-shrink-0 ml-2 text-gray-400 hover:text-gray-700"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        );
      })}
    </div>
  );
};
