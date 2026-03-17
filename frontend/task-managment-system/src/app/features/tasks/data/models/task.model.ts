export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Task {
  id: string;
  title: string;
  description?: string;
  priority: TaskPriority;
  dueDate?: string | null;
  statusCode: string;
  statusLabel: string;
  projectId: string;
  createdById: string;
  createdByFullName: string;
  assigneeId?: string | null;
  assigneeFullName?: string | null;
  createdAt: string;
  updatedAt: string;
}
