import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { complaintApi } from '../services/api';
import { Complaint, Category, ComplaintStatus } from '../types';
import { PhotoCaptureInput } from './PhotoCaptureInput';
import { 
  PlusCircle, 
  AlertCircle, 
  Clock, 
  CheckCircle2, 
  Star, 
  UploadCloud, 
  ShieldAlert, 
  Zap, 
  ChevronRight, 
  Image as ImageIcon 
} from 'lucide-react';

interface ResidentPortalProps {
  residentId: number;
  societyId: number;
}

export const ResidentPortal: React.FC<ResidentPortalProps> = ({ residentId, societyId }) => {
  const queryClient = useQueryClient();

  // Form State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [categoryId, setCategoryId] = useState<number | ''>('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [locationDetails, setLocationDetails] = useState('');
  const [photoUrl, setPhotoUrl] = useState('');

  // Live Predicted Priority State
  const [predictedPriority, setPredictedPriority] = useState<string>('MEDIUM');
  const [predictedSla, setPredictedSla] = useState<number>(12);
  const [predictReason, setPredictReason] = useState<string>('Standard default SLA applies');

  // Feedback Modal State
  const [selectedComplaintForFeedback, setSelectedComplaintForFeedback] = useState<Complaint | null>(null);
  const [rating, setRating] = useState(5);
  const [review, setReview] = useState('');

  // Queries
  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: complaintApi.getCategories,
  });

  const { data: complaints = [], isLoading } = useQuery({
    queryKey: ['resident-complaints', residentId],
    queryFn: () => complaintApi.getComplaints({ residentId }),
    refetchInterval: 5000,
  });

  // Debounced Priority Prediction
  useEffect(() => {
    if (!title && !description) {
      setPredictedPriority('MEDIUM');
      setPredictedSla(12);
      setPredictReason('Enter details to calculate AI predicted priority');
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const res = await complaintApi.predictPriority({
          title,
          description,
          categoryId: categoryId ? Number(categoryId) : undefined,
        });
        setPredictedPriority(res.priority);
        setPredictedSla(res.slaHours);
        setPredictReason(res.reason);
      } catch (e) {
        console.error('Error predicting priority', e);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [title, description, categoryId]);

  // Mutations
  const createMutation = useMutation({
    mutationFn: complaintApi.createComplaint,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['resident-complaints', residentId] });
      setIsModalOpen(false);
      resetForm();
    },
  });

  const feedbackMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: { rating: number; review?: string } }) =>
      complaintApi.submitFeedback(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['resident-complaints', residentId] });
      setSelectedComplaintForFeedback(null);
      setReview('');
      setRating(5);
    },
  });

  const resetForm = () => {
    setCategoryId('');
    setTitle('');
    setDescription('');
    setLocationDetails('');
    setPhotoUrl('');
    setPredictedPriority('MEDIUM');
    setPredictedSla(12);
  };

  const handleCreateComplaint = (e: React.FormEvent) => {
    e.preventDefault();
    if (!categoryId) return;
    createMutation.mutate({
      categoryId: Number(categoryId),
      title,
      description,
      locationDetails,
      photoUrl: photoUrl || undefined,
    });
  };

  const handleFeedbackSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedComplaintForFeedback) return;
    feedbackMutation.mutate({
      id: selectedComplaintForFeedback.id,
      data: { rating, review },
    });
  };

  // Helper for SLA Countdown Display
  const getSlaBadge = (complaint: Complaint) => {
    if (complaint.status === 'RESOLVED' || complaint.status === 'CLOSED') {
      return (
        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold bg-emerald-100 text-emerald-800">
          <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> SLA Met
        </span>
      );
    }

    if (complaint.slaBreached) {
      return (
        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-bold bg-red-100 text-red-800 animate-pulse">
          <ShieldAlert className="w-3.5 h-3.5 mr-1 text-red-600" /> SLA Breached! Escalated
        </span>
      );
    }

    const now = new Date().getTime();
    const deadline = new Date(complaint.slaDeadline).getTime();
    const diffMs = deadline - now;

    if (diffMs <= 0) {
      return (
        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-bold bg-red-100 text-red-800">
          <ShieldAlert className="w-3.5 h-3.5 mr-1 text-red-600" /> Deadline Exceeded
        </span>
      );
    }

    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMins = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    const isUrgent = diffHours === 0 && diffMins < 30;

    return (
      <span
        className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium border ${
          isUrgent
            ? 'bg-amber-100 border-amber-300 text-amber-900 animate-pulse'
            : 'bg-blue-50 border-blue-200 text-blue-800'
        }`}
      >
        <Clock className="w-3.5 h-3.5 mr-1" />
        SLA Target: {diffHours}h {diffMins}m remaining
      </span>
    );
  };

  const steps: ComplaintStatus[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

  return (
    <div className="space-y-6">
      {/* Top Banner & Action */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Resident Maintenance Center</h2>
          <p className="text-xs text-slate-500 mt-1">
            Log maintenance complaints, monitor real-time SLA countdowns, and rate resolved tasks.
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="inline-flex items-center px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-lg shadow-sm transition-colors"
        >
          <PlusCircle className="w-4 h-4 mr-2" />
          Raise New Complaint
        </button>
      </div>

      {/* Complaints List / Tracker */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-4 border-b border-slate-200 bg-slate-50/50 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
            Your Active & Past Tickets ({complaints.length})
          </h3>
          <span className="text-xs text-slate-500">Auto-refreshes in real-time</span>
        </div>

        {isLoading ? (
          <div className="p-12 text-center text-slate-400">Loading your complaints...</div>
        ) : complaints.length === 0 ? (
          <div className="p-12 text-center">
            <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center mx-auto text-slate-400 mb-3">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <h4 className="text-sm font-semibold text-slate-700">No complaints reported</h4>
            <p className="text-xs text-slate-500 mt-1">
              Have a plumbing, electrical, or cleanliness issue? Click "Raise New Complaint" above.
            </p>
          </div>
        ) : (
          <div className="divide-y divide-slate-200">
            {complaints.map((item) => {
              const currentStepIndex = steps.indexOf(item.status);

              return (
                <div key={item.id} className="p-6 hover:bg-slate-50/50 transition-colors">
                  <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
                    {/* Ticket Header & Details */}
                    <div className="space-y-1.5 flex-1">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="text-xs font-mono font-bold text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                          #{item.id}
                        </span>
                        <h4 className="text-base font-bold text-slate-900">{item.title}</h4>
                        <span
                          className={`text-xs px-2 py-0.5 font-bold rounded ${
                            item.priority === 'HIGH'
                              ? 'bg-red-100 text-red-800'
                              : item.priority === 'MEDIUM'
                              ? 'bg-amber-100 text-amber-800'
                              : 'bg-slate-100 text-slate-700'
                          }`}
                        >
                          {item.priority} Priority
                        </span>
                        <span className="text-xs text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                          {item.category?.name || 'General'}
                        </span>
                      </div>
                      <p className="text-xs text-slate-600 leading-relaxed max-w-3xl">
                        {item.description}
                      </p>
                      {item.locationDetails && (
                        <p className="text-xs text-slate-400">
                          <span className="font-medium text-slate-500">Location:</span> {item.locationDetails}
                        </p>
                      )}
                    </div>

                    {/* SLA Badge & Actions */}
                    <div className="flex flex-col sm:flex-row sm:items-center gap-3">
                      {getSlaBadge(item)}

                      {/* Post-Resolution Review Button */}
                      {(item.status === 'RESOLVED' || item.status === 'CLOSED') && (
                        <div>
                          {item.feedback ? (
                            <div className="flex items-center text-amber-500 text-xs font-semibold bg-amber-50 px-2.5 py-1 rounded border border-amber-200">
                              <Star className="w-3.5 h-3.5 fill-amber-500 mr-1" />
                              {item.feedback.rating}/5 Rated
                            </div>
                          ) : (
                            <button
                              onClick={() => setSelectedComplaintForFeedback(item)}
                              className="inline-flex items-center px-3 py-1 bg-amber-500 hover:bg-amber-600 text-white text-xs font-bold rounded shadow-sm transition-colors"
                            >
                              <Star className="w-3.5 h-3.5 mr-1" />
                              Rate Resolution
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Horizontal Interactive Status Stepper */}
                  <div className="mt-6 pt-4 border-t border-slate-100">
                    <div className="flex items-center justify-between relative max-w-2xl mx-auto">
                      {/* Connecting Line */}
                      <div className="absolute top-1/2 left-0 right-0 -translate-y-1/2 h-1 bg-slate-200 z-0" />
                      <div
                        className="absolute top-1/2 left-0 -translate-y-1/2 h-1 bg-emerald-500 z-0 transition-all duration-500"
                        style={{
                          width: `${(currentStepIndex / (steps.length - 1)) * 100}%`,
                        }}
                      />

                      {steps.map((st, idx) => {
                        const isCompleted = idx <= currentStepIndex;
                        const isCurrent = idx === currentStepIndex;

                        return (
                          <div key={st} className="flex flex-col items-center relative z-10">
                            <div
                              className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold border-2 transition-all ${
                                isCompleted
                                  ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm'
                                  : 'bg-white text-slate-400 border-slate-300'
                              } ${isCurrent ? 'ring-4 ring-emerald-100 scale-110' : ''}`}
                            >
                              {isCompleted ? '✓' : idx + 1}
                            </div>
                            <span
                              className={`text-[10px] mt-1 font-semibold uppercase tracking-tight ${
                                isCurrent
                                  ? 'text-emerald-700 font-bold'
                                  : isCompleted
                                  ? 'text-slate-800'
                                  : 'text-slate-400'
                              }`}
                            >
                              {st.replace('_', ' ')}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  {/* Issue Photo & GPS Tag display if present */}
                  {item.photoUrl && (
                    <div className="mt-3 p-2.5 rounded-lg bg-slate-50 border border-slate-200 flex items-center space-x-3">
                      <img
                        src={item.photoUrl}
                        alt="Issue Photo"
                        className="w-16 h-16 object-cover rounded-lg border border-slate-300 shadow-xs"
                      />
                      <div className="text-xs space-y-1">
                        <span className="font-bold text-slate-800 flex items-center">
                          <ImageIcon className="w-3.5 h-3.5 text-slate-600 mr-1" /> Attached Issue Photo
                        </span>
                        {item.locationDetails && (
                          <div className="text-[11px] text-emerald-800 font-medium bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 inline-flex items-center">
                            <span>{item.locationDetails}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  {/* Resolution Notes & Fulfillment Receipt Display (if completed) */}
                  {(item.status === 'RESOLVED' || item.status === 'CLOSED') && (
                    <div className="mt-4 p-4 rounded-xl bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-300 shadow-xs space-y-3">
                      <div className="flex items-center justify-between border-b border-emerald-200/80 pb-2">
                        <div className="flex items-center text-xs font-extrabold text-emerald-900 space-x-1.5">
                          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                          <span>🎉 COMPLAINT FULFILLED & RESOLVED SUCCESSFULLY</span>
                        </div>
                        {item.resolvedAt && (
                          <span className="text-[10px] text-emerald-700 font-mono">
                            Resolved on {new Date(item.resolvedAt).toLocaleString()}
                          </span>
                        )}
                      </div>

                      {item.resolutionNotes && (
                        <div className="text-xs text-emerald-950 leading-relaxed">
                          <span className="font-bold text-emerald-900">Work Summary: </span>
                          {item.resolutionNotes}
                        </div>
                      )}

                      {/* Before & After Photo Proof Comparison */}
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                        {item.photoUrl && (
                          <div className="bg-white p-2 rounded-lg border border-slate-200 text-xs space-y-1">
                            <span className="font-bold text-slate-700 text-[11px] block">1. Reported Issue Photo (Before):</span>
                            <img src={item.photoUrl} alt="Before" className="w-full h-24 object-cover rounded-md border" />
                          </div>
                        )}
                        {item.resolutionPhotoUrl && (
                          <div className="bg-white p-2 rounded-lg border border-emerald-300 text-xs space-y-1">
                            <span className="font-bold text-emerald-800 text-[11px] block">2. Repair Completion Photo (After):</span>
                            <img src={item.resolutionPhotoUrl} alt="After" className="w-full h-24 object-cover rounded-md border" />
                          </div>
                        )}
                      </div>

                      {/* Post-Resolution Review Rating Prompt */}
                      {item.feedback ? (
                        <div className="pt-2 flex items-center space-x-2 text-xs text-emerald-900 font-semibold border-t border-emerald-200/80">
                          <Star className="w-4 h-4 text-amber-500 fill-amber-500" />
                          <span>Your Rating: {item.feedback.rating}/5 — "{item.feedback.review}"</span>
                        </div>
                      ) : (
                        <div className="pt-2 flex items-center justify-between border-t border-emerald-200/80">
                          <span className="text-xs text-emerald-800 font-medium">How was the maintenance service?</span>
                          <button
                            onClick={() => setSelectedComplaintForFeedback(item)}
                            className="inline-flex items-center px-3 py-1 bg-amber-500 hover:bg-amber-600 text-white text-xs font-bold rounded-lg shadow-sm transition-colors"
                          >
                            <Star className="w-3.5 h-3.5 mr-1 fill-amber-200" />
                            Rate Service & Feedback
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* MODAL 1: Create New Complaint */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up">
            <div className="p-6 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Report a Society Maintenance Issue</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 text-sm font-semibold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateComplaint} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Issue Category *
                </label>
                <select
                  required
                  value={categoryId}
                  onChange={(e) => setCategoryId(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                >
                  <option value="">Select a category...</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name} ({c.department} - Baseline SLA: {c.defaultSlaHours}h)
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Complaint Title *
                </label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Major pipe burst causing water leakage in bathroom"
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Description & Context *
                </label>
                <textarea
                  required
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Describe the issue in detail. Emergency keywords (flood, spark, stuck elevator) will trigger high-priority SLA!"
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              {/* LIVE PREDICTED PRIORITY BADGE */}
              <div
                className={`p-3 rounded-lg border flex items-start space-x-3 transition-colors ${
                  predictedPriority === 'HIGH'
                    ? 'bg-red-50 border-red-200 text-red-900'
                    : predictedPriority === 'MEDIUM'
                    ? 'bg-amber-50 border-amber-200 text-amber-900'
                    : 'bg-emerald-50 border-emerald-200 text-emerald-900'
                }`}
              >
                <Zap
                  className={`w-5 h-5 flex-shrink-0 mt-0.5 ${
                    predictedPriority === 'HIGH'
                      ? 'text-red-600 animate-bounce'
                      : predictedPriority === 'MEDIUM'
                      ? 'text-amber-600'
                      : 'text-emerald-600'
                  }`}
                />
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="text-xs font-bold uppercase tracking-wider">
                      AI Predicted SLA Priority: {predictedPriority}
                    </span>
                    <span className="text-xs px-2 py-0.5 font-extrabold rounded bg-white shadow-xs">
                      {predictedSla} Hours SLA
                    </span>
                  </div>
                  <p className="text-[11px] mt-0.5 opacity-85 leading-snug">{predictReason}</p>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Location / Flat Details
                </label>
                <input
                  type="text"
                  value={locationDetails}
                  onChange={(e) => setLocationDetails(e.target.value)}
                  placeholder="e.g. Master Bedroom Balcony / Lift #2 Ground Floor"
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <PhotoCaptureInput
                value={photoUrl}
                onChange={setPhotoUrl}
                label="Attach Issue Photo (Direct Camera or File Upload)"
              />

              <div className="pt-3 flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 text-sm font-medium rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending || !categoryId}
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-lg shadow-sm disabled:opacity-50"
                >
                  {createMutation.isPending ? 'Submitting...' : 'Submit Complaint'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: 5-Star Feedback & Review */}
      {selectedComplaintForFeedback && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-slate-100 overflow-hidden animate-scale-up">
            <div className="p-6 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-900">Rate Maintenance Resolution</h3>
                <p className="text-xs text-slate-500">Ticket #{selectedComplaintForFeedback.id}</p>
              </div>
              <button
                onClick={() => setSelectedComplaintForFeedback(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-semibold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleFeedbackSubmit} className="p-6 space-y-4">
              <div className="text-center">
                <label className="block text-xs font-semibold text-slate-700 mb-2">
                  How satisfied are you with the resolution?
                </label>
                <div className="flex justify-center space-x-2">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setRating(star)}
                      className="p-1 text-amber-400 hover:scale-125 transition-transform"
                    >
                      <Star
                        className={`w-8 h-8 ${
                          star <= rating ? 'fill-amber-400 text-amber-500' : 'text-slate-300'
                        }`}
                      />
                    </button>
                  ))}
                </div>
                <span className="text-xs font-bold text-slate-600 mt-2 block">
                  {rating === 5 && '🌟 Exceptional - Fixed promptly!'}
                  {rating === 4 && '👍 Good - Well handled'}
                  {rating === 3 && '😐 Average - Met expectations'}
                  {rating === 2 && '👎 Poor - Took too long'}
                  {rating === 1 && '⚠️ Terrible - Unsatisfactory'}
                </span>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Comments or Feedback
                </label>
                <textarea
                  rows={3}
                  value={review}
                  onChange={(e) => setReview(e.target.value)}
                  placeholder="Share feedback on staff professionalism and quality of work..."
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div className="pt-2 flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setSelectedComplaintForFeedback(null)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 text-sm font-medium rounded-lg hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={feedbackMutation.isPending}
                  className="px-5 py-2 bg-amber-500 hover:bg-amber-600 text-white text-sm font-bold rounded-lg shadow-sm disabled:opacity-50"
                >
                  {feedbackMutation.isPending ? 'Submitting...' : 'Submit Rating'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
