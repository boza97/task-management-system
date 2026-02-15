import { Component, effect, inject, signal } from '@angular/core';
import { TaskAuditService } from '../../data/audit.service';
import { ActionType, TaskAuditLog } from '../../data/models/audit.model';
import { Store } from '@ngrx/store';
import { toSignal } from '@angular/core/rxjs-interop';
import { selectTask } from '../../../data/store/task.selectors';
import { filter, map } from 'rxjs';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-task-audit',
  imports: [DatePipe],
  templateUrl: './task-audit.html',
  styleUrl: './task-audit.scss',
})
export class TaskAudit {
  private readonly auditService = inject(TaskAuditService);
  private readonly store = inject(Store);

  taskId = toSignal(
    this.store.select(selectTask).pipe(
      filter((x) => !!x),
      map((x) => x.id),
    ),
  );
  items = signal<TaskAuditLog[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  actionTypeEnum = ActionType;

  constructor() {
    effect(() => {
      const taskId = this.taskId();
      if (taskId) {
        this.loadTaskAudit(taskId);
      }
    });
  }

  label(action: ActionType) {
    switch (action) {
      case ActionType.TASK_CREATED:
        return 'Task created';
      case ActionType.STATUS_CHANGED:
        return 'Status changed';
      case ActionType.ASSIGNEE_CHANGED:
        return 'Assignee changed';
      case ActionType.TITLE_CHANGED:
        return 'Title changed';
      case ActionType.DESCRIPTION_CHANGED:
        return 'Description changed';
      case ActionType.DUE_DATE_CHANGED:
        return 'Due date changed';
      case ActionType.PRIORITY_CHANGED:
        return 'Priority changed';
      case ActionType.COMMENT_ADDED:
        return 'Comment added';
      case ActionType.COMMENT_DELETED:
        return 'Comment deleted';
      default:
        return action;
    }
  }

  private loadTaskAudit(taskId: string) {
    this.auditService.getTaskAuditLogs(taskId).subscribe({
      next: (list) => {
        this.items.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load audit logs');
        this.loading.set(false);
      },
    });
  }
}
