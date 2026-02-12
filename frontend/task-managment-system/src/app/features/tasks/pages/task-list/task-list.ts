import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TaskService } from '../../data/task.service';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { catchError, filter, map, of, tap } from 'rxjs';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { selectProject } from '../../../projects/data/store/project.selectors';
import { Task } from '../../data/models/task.model';

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
