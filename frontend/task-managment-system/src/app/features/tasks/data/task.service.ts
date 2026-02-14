import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Task } from './models/task.model';
import { CreateTaskRequest } from './models/create-task-request.model';
import { UpdateTaskRequest } from './models/update-task-request.model';
import { TaskStatus } from './models/task-status.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);

  getTasksForProject(projectId: string) {
    return this.http.get<Task[]>(`${environment.apiUrl}/tasks/project/${projectId}`);
  }

  createTask(request: CreateTaskRequest) {
    return this.http.post<Task>(`${environment.apiUrl}/tasks`, request);
  }

  getTask(taskId: string) {
    return this.http.get<Task>(`${environment.apiUrl}/tasks/${taskId}`);
  }

  updateTask(taskId: string, data: UpdateTaskRequest) {
    return this.http.patch<Task>(`${environment.apiUrl}/tasks/${taskId}`, data);
  }

  changeStatus(taskId: string, statusCode: string) {
    return this.http.patch<Task>(`${environment.apiUrl}/tasks/${taskId}/status`, { statusCode });
  }

  changeAssignee(taskId: string, assigneeId: string | null) {
    return this.http.patch<Task>(`${environment.apiUrl}/tasks/${taskId}/assignee`, { assigneeId });
  }

  getTaskStatuses() {
    return this.http.get<TaskStatus[]>(`${environment.apiUrl}/task-statuses`);
  }
}
