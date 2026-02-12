import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TaskService } from '../../data/task.service';
import { Task } from '../../data/models/task.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-task-details',
  imports: [DatePipe, RouterLink],
  templateUrl: './task-details.html',
  styleUrl: './task-details.scss',
})
export class TaskDetails implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);
  private readonly destroyRef = inject(DestroyRef);

  task = signal<Task | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    const taskId = this.route.snapshot.paramMap.get('taskId');

    if (!taskId) {
      this.error.set('Task ID missing');
      this.loading.set(false);
      return;
    }

    this.taskService
      .getTask(taskId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (task) => {
          this.task.set(task);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Failed to load task');
          this.loading.set(false);
        },
      });
  }
}
