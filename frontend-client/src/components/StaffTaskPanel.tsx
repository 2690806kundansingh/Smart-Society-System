import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { complaintApi } from '../services/api';
import { Complaint, ComplaintStatus } from '../types';
import { PhotoCaptureInput } from './PhotoCaptureInput';
import { 
  CheckCircle, 
  Clock, 
  PlayCircle, 
  AlertTriangle, 
  Wrench, 
  FileText, 
  Camera, 
  LayoutGrid, 
  ListFilter 
} from 'lucide-react';

interface StaffTaskPanelProps {
  staffId: number;
  societyId: number;
}

export const StaffTaskPanel: React.FC<StaffTaskPanelProps> = ({ staffId, societyId }) => {
  const queryClient = useQueryClient();
  const [viewMode, setViewMode] = useState<'kanban' | 'list'>('kanban');

  // Complete / Resolve Modal State
  const [resolvingComplaint, setResolvingComplaint] = useState<Complaint | null>(null);
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [resolutionPhotoUrl, setResolutionPhotoUrl] = useState('');

  // Fetch assigned complaints
  const { data: tickets = [], isLoading } = useQuery({
    queryKey: ['staff-complaints', staffId],
    queryFn: () => complaintApi.getComplaints({ staffId }),
    refetchInterval: 5000,
  });

  // Mutation to update status
  const updateStatusMutation = useMutation({
    mutationFn: ({
      id,
      status,
      notes,
      photoUrl,
    }: {
      id: number;
      status: string;
      notes?: string;
      photoUrl?: string;
    }) =>
      complaintApi.updateStatus(id, {
        status,
        notes,
        resolutionPhotoUrl: photoUrl,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['staff-complaints', staffId] });
      setResolvingComplaint(null);
      setResolutionNotes('');
      setResolutionPhotoUrl('');
    },
  });

  const handleAccept = (ticket: Complaint) => {
    updateStatusMutation.mutate({
      id: ticket.id,
      status: 'IN_PROGRESS',
      notes: 'Staff accepted ticket and commenced maintenance work.',
    });
  };

  const handleResolveSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!resolvingComplaint) return;
    updateStatusMutation.mutate({
      id: resolvingComplaint.id,
      status: 'RESOLVED',
      notes: resolutionNotes,
      photoUrl: resolutionPhotoUrl || undefined,
    });
  };

  const getSlaBadge = (ticket: Complaint) => {
    if (ticket.status === 'RESOLVED' || ticket.status === 'CLOSED') {
      return (
        <span className="text-[11px] px-2 py-0.5 rounded font-bold bg-emerald-100 text-emerald-800">
          Resolved
        </span>
      );
    }
    if (ticket.slaBreached) {
      return (
        <span className="text-[11px] px-2 py-0.5 rounded font-bold bg-red-100 text-red-800 animate-pulse">
          SLA Breached!
        </span>
      );
    }
    const diff = new Date(ticket.slaDeadline).getTime() - new Date().getTime();
    if (diff <= 0) {
      return (
        <span className="text-[11px] px-2 py-0.5 rounded font-bold bg-red-100 text-red-800">
          Breached
        </span>
      );
    }
    const h = Math.floor(diff / (1000 * 60 * 60));
    const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    return (
      <span className="text-[11px] px-2 py-0.5 rounded font-medium bg-blue-100 text-blue-800 flex items-center">
        <Clock className="w-3 h-3 mr-1" /> {h}h {m}m left
      </span>
    );
  };

  // Group tickets for Kanban
  const kanbanColumns: { title: string; status: ComplaintStatus; color: string }[] = [
    { title: 'Assigned / Pending', status: 'ASSIGNED', color: 'border-amber-400 bg-amber-50/30' },
    { title: 'In Progress', status: 'IN_PROGRESS', color: 'border-blue-400 bg-blue-50/30' },
    { title: 'Resolved', status: 'RESOLVED', color: 'border-emerald-400 bg-emerald-50/30' },
  ];

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center">
            <Wrench className="w-5 h-5 mr-2 text-amber-600" /> Staff Maintenance Task Panel
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Accept assigned work orders, update task progress, and upload resolution proof.
          </p>
        </div>

        {/* View Switcher */}
        <div className="flex items-center space-x-2 bg-slate-100 p-1 rounded-lg">
          <button
            onClick={() => setViewMode('kanban')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-md flex items-center transition-all ${
              viewMode === 'kanban'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <LayoutGrid className="w-3.5 h-3.5 mr-1.5" /> Kanban
          </button>
          <button
            onClick={() => setViewMode('list')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-md flex items-center transition-all ${
              viewMode === 'list'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <ListFilter className="w-3.5 h-3.5 mr-1.5" /> List
          </button>
        </div>
      </div>

      {isLoading ? (
        <div className="p-12 text-center text-slate-400 bg-white rounded-xl">Loading tasks...</div>
      ) : tickets.length === 0 ? (
        <div className="bg-white rounded-xl p-12 text-center border border-slate-200">
          <CheckCircle className="w-12 h-12 text-emerald-500 mx-auto mb-3" />
          <h4 className="text-base font-bold text-slate-800">All clear! No pending tasks</h4>
          <p className="text-xs text-slate-500 mt-1">
            When new tickets are assigned to you by the society administrator, they will appear here.
          </p>
        </div>
      ) : viewMode === 'kanban' ? (
        /* KANBAN VIEW */
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {kanbanColumns.map((col) => {
            const colTickets = tickets.filter(
              (t) => t.status === col.status || (col.status === 'ASSIGNED' && t.status === 'OPEN')
            );

            return (
              <div
                key={col.status}
                className={`rounded-xl border-t-4 p-4 shadow-sm bg-white border border-slate-200 ${col.color}`}
              >
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-sm font-bold text-slate-800">{col.title}</h3>
                  <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-slate-200 text-slate-700">
                    {colTickets.length}
                  </span>
                </div>

                <div className="space-y-3">
                  {colTickets.length === 0 ? (
                    <div className="p-6 text-center text-xs text-slate-400 border border-dashed border-slate-200 rounded-lg">
                      No tasks in this lane
                    </div>
                  ) : (
                    colTickets.map((ticket) => (
                      <div
                        key={ticket.id}
                        className="bg-white rounded-lg border border-slate-200 p-4 shadow-xs hover:shadow-md transition-shadow space-y-3"
                      >
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-mono font-bold text-slate-500">
                            #{ticket.id}
                          </span>
                          {getSlaBadge(ticket)}
                        </div>

                        <div>
                          <h4 className="text-sm font-bold text-slate-900 leading-snug">
                            {ticket.title}
                          </h4>
                          <p className="text-xs text-slate-600 line-clamp-2 mt-1">
                            {ticket.description}
                          </p>

                          {/* Reported Problem Image on Staff Card */}
                          {ticket.photoUrl && (
                            <div className="mt-2.5 rounded-lg overflow-hidden border border-slate-200 bg-slate-50 p-1.5 flex items-center space-x-2">
                              <img
                                src={ticket.photoUrl}
                                alt="Reported Problem"
                                className="w-14 h-14 object-cover rounded-md border border-slate-300 flex-shrink-0"
                              />
                              <div className="text-[11px] space-y-0.5">
                                <span className="font-bold text-slate-800 flex items-center">
                                  <Camera className="w-3 h-3 text-slate-500 mr-1" /> Reported Problem Image
                                </span>
                                <a
                                  href={ticket.photoUrl}
                                  target="_blank"
                                  rel="noreferrer"
                                  className="text-blue-600 hover:underline font-semibold block text-[10px]"
                                >
                                  View Full Photo ↗
                                </a>
                              </div>
                            </div>
                          )}

                          {ticket.locationDetails && (
                            <p className="text-[11px] text-emerald-800 font-medium bg-emerald-50 p-1.5 rounded border border-emerald-200 mt-2">
                              {ticket.locationDetails}
                            </p>
                          )}
                        </div>

                        <div className="flex items-center justify-between pt-2 border-t border-slate-100">
                          <span
                            className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                              ticket.priority === 'HIGH'
                                ? 'bg-red-100 text-red-800'
                                : ticket.priority === 'MEDIUM'
                                ? 'bg-amber-100 text-amber-800'
                                : 'bg-slate-100 text-slate-700'
                            }`}
                          >
                            {ticket.priority} Priority
                          </span>

                          {/* Action Buttons */}
                          {ticket.status === 'ASSIGNED' || ticket.status === 'OPEN' ? (
                            <button
                              onClick={() => handleAccept(ticket)}
                              disabled={updateStatusMutation.isPending}
                              className="inline-flex items-center px-2.5 py-1 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded shadow-xs"
                            >
                              <PlayCircle className="w-3.5 h-3.5 mr-1" /> Accept
                            </button>
                          ) : ticket.status === 'IN_PROGRESS' ? (
                            <button
                              onClick={() => setResolvingComplaint(ticket)}
                              className="inline-flex items-center px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded shadow-xs"
                            >
                              <CheckCircle className="w-3.5 h-3.5 mr-1" /> Complete
                            </button>
                          ) : (
                            <span className="text-xs font-semibold text-emerald-600">✓ Done</span>
                          )}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        /* LIST VIEW */
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden divide-y divide-slate-200">
          {tickets.map((ticket) => (
            <div key={ticket.id} className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center space-x-2">
                  <span className="text-xs font-mono font-bold text-slate-500">#{ticket.id}</span>
                  <h4 className="text-sm font-bold text-slate-900">{ticket.title}</h4>
                  <span className="text-xs px-2 py-0.5 rounded bg-slate-100 text-slate-700 font-semibold">
                    {ticket.status}
                  </span>
                </div>
                <p className="text-xs text-slate-600 max-w-2xl">{ticket.description}</p>
                {ticket.locationDetails && (
                  <span className="text-xs text-slate-400">Location: {ticket.locationDetails}</span>
                )}
              </div>

              <div className="flex items-center space-x-3">
                {getSlaBadge(ticket)}
                {ticket.status === 'ASSIGNED' || ticket.status === 'OPEN' ? (
                  <button
                    onClick={() => handleAccept(ticket)}
                    disabled={updateStatusMutation.isPending}
                    className="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded"
                  >
                    Accept Ticket
                  </button>
                ) : ticket.status === 'IN_PROGRESS' ? (
                  <button
                    onClick={() => setResolvingComplaint(ticket)}
                    className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded"
                  >
                    Complete Ticket
                  </button>
                ) : (
                  <span className="text-xs font-bold text-emerald-600">Completed</span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* RESOLUTION MODAL */}
      {resolvingComplaint && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up">
            <div className="p-6 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-900">Complete Maintenance Task</h3>
                <p className="text-xs text-slate-500">Ticket #{resolvingComplaint.id}</p>
              </div>
              <button
                onClick={() => setResolvingComplaint(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-semibold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleResolveSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Resolution Notes & Work Summary *
                </label>
                <textarea
                  required
                  rows={3}
                  value={resolutionNotes}
                  onChange={(e) => setResolutionNotes(e.target.value)}
                  placeholder="Describe what was repaired or replaced (e.g. Replaced faulty washer and cleared blockage in bathroom drain pipe)..."
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <PhotoCaptureInput
                value={resolutionPhotoUrl}
                onChange={setResolutionPhotoUrl}
                label="Repair Completion Photo (Direct Camera or File Upload)"
              />

              <div className="pt-2 flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setResolvingComplaint(null)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 text-sm font-medium rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={updateStatusMutation.isPending || !resolutionNotes}
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-lg shadow-sm disabled:opacity-50"
                >
                  {updateStatusMutation.isPending ? 'Completing...' : 'Mark as Resolved'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
