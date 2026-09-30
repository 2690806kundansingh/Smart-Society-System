export type Role = 'ROLE_ADMIN' | 'ROLE_STAFF' | 'ROLE_RESIDENT';

export type Priority = 'HIGH' | 'MEDIUM' | 'LOW';

export type ComplaintStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

export type Department = 'PLUMBING' | 'ELECTRICAL' | 'CARPENTRY' | 'CLEANING' | 'GENERAL';

export interface User {
  id: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  societyId: number;
  roles: Role[];
  department?: Department;
  apartment?: string;
  isAvailable?: boolean;
  isActive?: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Category {
  id: number;
  code: string;
  name: string;
  department: Department;
  defaultPriority: Priority;
  defaultSlaHours: number;
}

export interface FeedbackRating {
  id: number;
  complaintId: number;
  residentId: number;
  rating: number;
  review?: string;
  createdAt: string;
}

export interface ComplaintAuditLog {
  id: number;
  complaintId: number;
  action: string;
  performedBy?: number;
  performedByRole?: string;
  previousStatus?: ComplaintStatus;
  newStatus?: ComplaintStatus;
  notes?: string;
  createdAt: string;
}

export interface Complaint {
  id: number;
  societyId: number;
  residentId: number;
  category: Category;
  title: string;
  description: string;
  locationDetails?: string;
  photoUrl?: string;
  priority: Priority;
  status: ComplaintStatus;
  assignedStaffId?: number;
  resolutionNotes?: string;
  resolutionPhotoUrl?: string;
  slaDeadline: string;
  slaBreached: boolean;
  escalated: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  feedback?: FeedbackRating;
  auditLogs?: ComplaintAuditLog[];
}

export interface DashboardStats {
  totalComplaints: number;
  resolvedCount: number;
  pendingCount: number;
  slaBreachedCount: number;
  slaBreachedPercentage: number;
  averageResolutionHours: number;
  categoryDistribution: Record<string, number>;
  statusDistribution: Record<string, number>;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'SUCCESS' | 'URGENT';
  complaintId?: number;
  societyId?: number;
  status?: string;
  timestamp: string;
}
