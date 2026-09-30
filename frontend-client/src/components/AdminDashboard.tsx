import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { complaintApi, userApi } from '../services/api';
import { Complaint, User } from '../types';
import { 
  BarChart, 
  Bar, 
  PieChart, 
  Pie, 
  Cell, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer 
} from 'recharts';
import { 
  CheckCircle2, 
  Clock, 
  AlertTriangle, 
  TrendingUp, 
  UserCheck, 
  Flame, 
  Building, 
  Search, 
  ShieldCheck 
} from 'lucide-react';

interface AdminDashboardProps {
  societyId: number;
}

const COLORS = ['#059669', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export const AdminDashboard: React.FC<AdminDashboardProps> = ({ societyId }) => {
  const queryClient = useQueryClient();

  // Selected Ticket for Staff Reassignment
  const [selectedTicketForAssign, setSelectedTicketForAssign] = useState<Complaint | null>(null);
  const [selectedStaffId, setSelectedStaffId] = useState<number | ''>('');
  const [assignNotes, setAssignNotes] = useState('');

  // Fetch KPI Stats
  const { data: stats, isLoading: statsLoading } = useQuery({
    queryKey: ['society-stats', societyId],
    queryFn: () => complaintApi.getStats(societyId),
    refetchInterval: 5000,
  });

  // Fetch all complaints
  const { data: allComplaints = [], isLoading: complaintsLoading } = useQuery({
    queryKey: ['admin-complaints', societyId],
    queryFn: () => complaintApi.getComplaints({ societyId }),
    refetchInterval: 5000,
  });

  // Fetch Escalated complaints
  const { data: escalatedComplaints = [] } = useQuery({
    queryKey: ['escalated-complaints', societyId],
    queryFn: () => complaintApi.getEscalated(societyId),
    refetchInterval: 5000,
  });

  // Fetch Staff list for society
  const { data: staffMembers = [] } = useQuery({
    queryKey: ['society-staff', societyId],
    queryFn: () => userApi.getStaff(societyId),
  });

  // Re-assign Mutation
  const assignMutation = useMutation({
    mutationFn: ({ id, staffId, notes }: { id: number; staffId: number; notes?: string }) =>
      complaintApi.assignStaff(id, { staffId, notes }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-complaints', societyId] });
      queryClient.invalidateQueries({ queryKey: ['escalated-complaints', societyId] });
      queryClient.invalidateQueries({ queryKey: ['society-stats', societyId] });
      setSelectedTicketForAssign(null);
      setSelectedStaffId('');
      setAssignNotes('');
    },
  });

  const handleAssignSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTicketForAssign || !selectedStaffId) return;
    assignMutation.mutate({
      id: selectedTicketForAssign.id,
      staffId: Number(selectedStaffId),
      notes: assignNotes || undefined,
    });
  };

  // Prepare chart data
  const categoryChartData = stats?.categoryDistribution
    ? Object.entries(stats.categoryDistribution).map(([name, value]) => ({ name, value }))
    : [];

  const statusChartData = stats?.statusDistribution
    ? Object.entries(stats.statusDistribution).map(([name, count]) => ({
        name: name.replace('_', ' '),
        count,
      }))
    : [];

  return (
    <div className="space-y-8">
      {/* Top Banner */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center">
            <ShieldCheck className="w-6 h-6 mr-2 text-purple-600" /> Executive Admin & Analytics Hub
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Real-time SLA monitoring, KPI metrics, workload distribution, and automated escalation management.
          </p>
        </div>
        <div className="flex items-center space-x-2 text-xs font-semibold px-3 py-1.5 bg-slate-100 rounded-lg text-slate-700">
          <Building className="w-4 h-4 mr-1 text-slate-500" /> Society ID #{societyId}
        </div>
      </div>

      {/* KPI METRIC CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Total Tickets</span>
            <div className="p-2 rounded-lg bg-blue-50 text-blue-600">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black text-slate-900 mt-2">
            {statsLoading ? '...' : stats?.totalComplaints ?? 0}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">All registered complaints</span>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Resolved</span>
            <div className="p-2 rounded-lg bg-emerald-50 text-emerald-600">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black text-emerald-600 mt-2">
            {statsLoading ? '...' : stats?.resolvedCount ?? 0}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">Successfully closed</span>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Pending</span>
            <div className="p-2 rounded-lg bg-amber-50 text-amber-600">
              <Clock className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black text-amber-600 mt-2">
            {statsLoading ? '...' : stats?.pendingCount ?? 0}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">Open or in-progress</span>
        </div>

        <div className="bg-white p-5 rounded-xl border border-red-200 shadow-xs bg-red-50/20">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase text-red-600">SLA Breached</span>
            <div className="p-2 rounded-lg bg-red-100 text-red-600 animate-pulse">
              <AlertTriangle className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black text-red-600 mt-2">
            {statsLoading ? '...' : `${stats?.slaBreachedPercentage ?? 0}%`}
          </div>
          <span className="text-[11px] text-red-500 font-semibold mt-1 block">
            {stats?.slaBreachedCount ?? 0} tickets breached
          </span>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Avg Resolution</span>
            <div className="p-2 rounded-lg bg-purple-50 text-purple-600">
              <Clock className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-black text-purple-600 mt-2">
            {statsLoading ? '...' : `${stats?.averageResolutionHours ?? 0}h`}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">Hours to resolve</span>
        </div>
      </div>

      {/* VISUAL CHARTS (RECHARTS) */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chart 1: Doughnut Chart */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider mb-4">
            Complaints by Category (Breakdown)
          </h3>
          <div className="h-64">
            {categoryChartData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No category data available yet
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryChartData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={85}
                    paddingAngle={3}
                  >
                    {categoryChartData.map((_, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      borderRadius: '8px',
                      border: '1px solid #e2e8f0',
                      fontSize: '12px',
                    }}
                  />
                  <Legend wrapperStyle={{ fontSize: '11px', paddingTop: '10px' }} />
                </PieChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Chart 2: Bar Chart */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider mb-4">
            Complaints by Status Distribution
          </h3>
          <div className="h-64">
            {statusChartData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No status data available yet
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={statusChartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis dataKey="name" stroke="#94a3b8" fontSize={11} tickLine={false} />
                  <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} allowDecimals={false} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      borderRadius: '8px',
                      border: '1px solid #e2e8f0',
                      fontSize: '12px',
                    }}
                  />
                  <Bar dataKey="count" fill="#3b82f6" radius={[4, 4, 0, 0]} barSize={36} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>
      </div>

      {/* ESCALATED COMPLAINTS PRIORITY ALERT TABLE */}
      <div className="bg-white rounded-xl shadow-sm border border-red-200 overflow-hidden">
        <div className="p-4 bg-red-50 border-b border-red-200 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Flame className="w-5 h-5 text-red-600 animate-bounce" />
            <h3 className="text-sm font-bold text-red-950 uppercase tracking-wider">
              Urgent Escalated & SLA-Breached Complaints ({escalatedComplaints.length})
            </h3>
          </div>
          <span className="text-xs text-red-700 font-semibold">Immediate attention needed</span>
        </div>

        {escalatedComplaints.length === 0 ? (
          <div className="p-8 text-center text-xs text-slate-500">
            🎉 Great job! No complaints have breached SLA thresholds.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="p-3">Ticket ID</th>
                  <th className="p-3">Title & Issue</th>
                  <th className="p-3">Category</th>
                  <th className="p-3">SLA Deadline</th>
                  <th className="p-3">Assigned Staff</th>
                  <th className="p-3">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {escalatedComplaints.map((ticket) => (
                  <tr key={ticket.id} className="hover:bg-red-50/40 transition-colors">
                    <td className="p-3 font-mono font-bold text-slate-700">#{ticket.id}</td>
                    <td className="p-3">
                      <div className="font-bold text-slate-900">{ticket.title}</div>
                      <div className="text-[11px] text-slate-500 truncate max-w-xs">
                        {ticket.description}
                      </div>
                    </td>
                    <td className="p-3">
                      <span className="px-2 py-0.5 rounded bg-slate-100 text-slate-700 font-semibold">
                        {ticket.category?.name}
                      </span>
                    </td>
                    <td className="p-3 text-red-600 font-bold">
                      {new Date(ticket.slaDeadline).toLocaleString()}
                    </td>
                    <td className="p-3">
                      {ticket.assignedStaffId ? (
                        <span className="text-slate-700 font-medium">
                          Staff #{ticket.assignedStaffId}
                        </span>
                      ) : (
                        <span className="text-red-500 font-semibold">Unassigned</span>
                      )}
                    </td>
                    <td className="p-3">
                      <button
                        onClick={() => setSelectedTicketForAssign(ticket)}
                        className="px-2.5 py-1 bg-purple-600 hover:bg-purple-700 text-white font-bold rounded shadow-xs text-[11px]"
                      >
                        Re-assign Staff
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ALL COMPLAINTS TABLE & RE-ASSIGNMENT */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
            All Society Complaints ({allComplaints.length})
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3">Ticket</th>
                <th className="p-3">Resident</th>
                <th className="p-3">Title</th>
                <th className="p-3">Priority</th>
                <th className="p-3">Status</th>
                <th className="p-3">Staff</th>
                <th className="p-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {allComplaints.map((ticket) => (
                <tr key={ticket.id} className="hover:bg-slate-50 transition-colors">
                  <td className="p-3 font-mono font-bold text-slate-700">#{ticket.id}</td>
                  <td className="p-3 text-slate-600">Resident #{ticket.residentId}</td>
                  <td className="p-3">
                    <span className="font-semibold text-slate-900">{ticket.title}</span>
                  </td>
                  <td className="p-3">
                    <span
                      className={`px-2 py-0.5 rounded font-bold ${
                        ticket.priority === 'HIGH'
                          ? 'bg-red-100 text-red-800'
                          : ticket.priority === 'MEDIUM'
                          ? 'bg-amber-100 text-amber-800'
                          : 'bg-slate-100 text-slate-700'
                      }`}
                    >
                      {ticket.priority}
                    </span>
                  </td>
                  <td className="p-3">
                    <span className="font-semibold text-slate-800">{ticket.status}</span>
                  </td>
                  <td className="p-3">
                    {ticket.assignedStaffId ? (
                      <span className="text-slate-700">Staff #{ticket.assignedStaffId}</span>
                    ) : (
                      <span className="text-amber-600 font-semibold">Not Assigned</span>
                    )}
                  </td>
                  <td className="p-3 text-right">
                    <button
                      onClick={() => setSelectedTicketForAssign(ticket)}
                      className="px-2.5 py-1 bg-slate-100 hover:bg-purple-100 text-slate-700 hover:text-purple-700 font-semibold rounded border border-slate-300 transition-colors"
                    >
                      {ticket.assignedStaffId ? 'Re-assign' : 'Assign Staff'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* STAFF RE-ASSIGNMENT MODAL */}
      {selectedTicketForAssign && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up">
            <div className="p-6 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-900">Assign Maintenance Staff</h3>
                <p className="text-xs text-slate-500">Ticket #{selectedTicketForAssign.id} - {selectedTicketForAssign.title}</p>
              </div>
              <button
                onClick={() => setSelectedTicketForAssign(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-semibold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleAssignSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Choose Staff Member *
                </label>
                <select
                  required
                  value={selectedStaffId}
                  onChange={(e) => setSelectedStaffId(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select available staff...</option>
                  {staffMembers.map((staff) => (
                    <option key={staff.id} value={staff.id}>
                      {staff.fullName} ({staff.department || 'General'}) - {staff.isAvailable ? '✅ Available' : 'Busy'}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Assignment Instructions / Notes
                </label>
                <textarea
                  rows={2}
                  value={assignNotes}
                  onChange={(e) => setAssignNotes(e.target.value)}
                  placeholder="e.g. Please check valve on the terrace before entering the flat..."
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
              </div>

              <div className="pt-2 flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setSelectedTicketForAssign(null)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 text-sm font-medium rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={assignMutation.isPending || !selectedStaffId}
                  className="px-5 py-2 bg-purple-600 hover:bg-purple-700 text-white text-sm font-bold rounded-lg shadow-sm disabled:opacity-50"
                >
                  {assignMutation.isPending ? 'Assigning...' : 'Confirm Assignment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
