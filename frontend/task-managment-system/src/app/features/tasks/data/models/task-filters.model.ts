import { TaskPriority } from './task.model';

export interface TaskFilters {
  search: string | null;
  priority: TaskPriority | null;
  statusCode: string | null;
  assigneeId: string | null;
  dueDateFrom: string | null;
  dueDateTo: string | null;
}
