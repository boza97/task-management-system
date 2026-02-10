import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Task } from './models/task.model';
import { CreateTaskRequest } from './models/create-task-request.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);

  getTasksForProject(projectId: string) {
    return this.http.get<Task[]>(`${environment.apiUrl}/tasks/project/${projectId}`);
  }

  createTask(request: CreateTaskRequest) {
    return this.http.post<Task>(`${environment.apiUrl}/tasks`, request);
  }
}
