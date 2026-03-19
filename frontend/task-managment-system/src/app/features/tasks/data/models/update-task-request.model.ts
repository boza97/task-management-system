import { TaskPriority } from './task.model';

export interface UpdateTaskRequest {
  title: string;
  description: string | null;
  dueDate: string | null;
  priority: TaskPriority;
}
