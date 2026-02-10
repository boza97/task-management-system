import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../data/task.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CreateTaskRequest, TaskPriority } from '../../data/models/create-task-request.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-task-create',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-create.html',
  styleUrl: './task-create.scss',
})
export class TaskCreate {
  private readonly fb = inject(FormBuilder);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  private readonly projectId = this.route.parent?.snapshot.paramMap.get('projectId')!;

  taskPriorityEnum = TaskPriority;
  form = this.fb.nonNullable.group({
    title: ['', [Validators.required]],
    description: [''],
    priority: [TaskPriority.MEDIUM, [Validators.required]],
    dueDate: [''],
  });
  loading = signal(false);

  submit() {
    if (this.form.invalid) {
      return;
    }

    this.loading.set(true);

    const request: CreateTaskRequest = {
      ...this.form.getRawValue(),
      projectId: this.projectId,
      dueDate: this.form.value.dueDate || null,
    };

    this.taskService
      .createTask(request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.router.navigate(['../tasks'], { relativeTo: this.route });
        },
        error: () => {
          this.loading.set(false);
        },
      });
  }
}
