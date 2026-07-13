export enum NotificationType {
  TASK_ASSIGNED = 'TASK_ASSIGNED',
}

export interface AppNotification {
  id: string;
  type: NotificationType;
  message: string;
  taskId: string | null;
  projectId: string | null;
  read: boolean;
  createdAt: string;
}
