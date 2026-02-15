import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../data/task.service';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { catchError, filter, map, of, tap } from 'rxjs';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { selectProject } from '../../../projects/data/store/project.selectors';
import { Task } from '../../data/models/task.model';
import { ToastService } from '../../../../shared/services/toast.service';
import { TokenStorageService } from '../../../../shared/services/token-storage.service';

@Component({
  selector: 'app-task-list',
  imports: [DatePipe, RouterLink],
  templateUrl: './task-list.html',
  styleUrl: './task-list.scss',
})
export class TaskList {
  private readonly store = inject(Store);
  private readonly taskService = inject(TaskService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastService = inject(ToastService);
  private readonly tokenStorageService = inject(TokenStorageService);

  loading = signal(true);
  error = signal<string | null>(null);
  tasks = signal<Task[]>([]);
  projectId = toSignal(
    this.store.select(selectProject).pipe(
      filter((x) => !!x),
      map((x) => x.id),
    ),
  );

  constructor() {
    effect(() => {
      const projectId = this.projectId();
      if (projectId) {
        this.loadTasksForProject(projectId);
      }
    });
  }

  onDeleteClick(event: MouseEvent, taskId: string) {
    event.stopPropagation();
    this.deleteTask(taskId);
  }

  canDeleteTask(task: Task) {
    const currentUser = this.tokenStorageService.getUser();
    return currentUser?.roles.includes('ADMIN') || currentUser?.sub === task.createdById;
  }

  private deleteTask(taskId: string) {
    if (!confirm('Delete this task?')) {
      return;
    }

    this.taskService
      .deleteTask(taskId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.tasks.update((list) => list.filter((t) => t.id !== taskId));
          this.toastService.show('Task deleted', 'success');
        },
        error: () => {
          this.toastService.show('Failed to delete task', 'error');
        },
      });
  }

  private loadTasksForProject(projectId: string) {
    this.loading.set(true);

    this.taskService
      .getTasksForProject(projectId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(() => {
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(() => {
          this.loading.set(false);
          this.error.set('Failed to load tasks');
          return of([]);
        }),
      )
      .subscribe((list) => this.tasks.set(list));
  }
}
