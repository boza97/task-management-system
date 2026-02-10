import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TaskService } from '../../data/task.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-task-list',
  imports: [DatePipe, RouterLink],
  templateUrl: './task-list.html',
  styleUrl: './task-list.scss',
})
export class TaskList {
  private readonly route = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);

  loading = signal(true);
  error = signal<string | null>(null);

  private readonly projectId = this.route.parent?.snapshot.paramMap.get('projectId')!;

  tasks = toSignal(
    this.taskService.getTasksForProject(this.projectId).pipe(
      tap(() => {
        this.loading.set(false);
        this.error.set(null);
      }),
      catchError(() => {
        this.loading.set(false);
        this.error.set('Failed to load tasks');
        return of([]);
      }),
    ),
    { initialValue: [] },
  );
}
