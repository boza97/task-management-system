import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Task } from './models/task.model';
import { CreateTaskRequest } from './models/create-task-request.model';
import { UpdateTaskRequest } from './models/update-task-request.model';
import { TaskStatus } from './models/task-status.model';
import { TaskFilters } from './models/task-filters.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);

  getTasks(projectId: string, filters: TaskFilters) {
    let params = new HttpParams();

    if (filters.search) {
      params = params.set('search', filters.search);
    }

    if (filters.priority) {
      params = params.set('priority', filters.priority);
    }

    if (filters.statusCode) {
      params = params.set('statusCode', filters.statusCode);
    }

    if (filters.assigneeId) {
      params = params.set('assigneeId', filters.assigneeId);
    }

    if (filters.dueDateFrom) {
      params = params.set('dueDateFrom', filters.dueDateFrom);
    }

    if (filters.dueDateTo) {
      params = params.set('dueDateTo', filters.dueDateTo);
    }

    return this.http.get<Task[]>(`${environment.apiUrl}/tasks/project/${projectId}`, { params });
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

  deleteTask(taskId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/tasks/${taskId}`);
  }
}
