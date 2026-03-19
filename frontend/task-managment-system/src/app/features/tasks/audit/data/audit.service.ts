import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { TaskAuditLog } from './models/audit.model';

@Injectable({ providedIn: 'root' })
export class TaskAuditService {
  private readonly http = inject(HttpClient);

  getTaskAuditLogs(taskId: string) {
    return this.http.get<TaskAuditLog[]>(`${environment.apiUrl}/tasks/${taskId}/audit-logs`);
  }
}
