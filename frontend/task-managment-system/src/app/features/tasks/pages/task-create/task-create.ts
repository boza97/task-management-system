import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../data/task.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CreateTaskRequest, TaskPriority } from '../../data/models/create-task-request.model';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { Store } from '@ngrx/store';
import { selectProject } from '../../../projects/data/store/project.selectors';
import { filter, map } from 'rxjs';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { ProjectMember } from '../../../projects/data/models/project-member.model';

@Component({
  selector: 'app-task-create',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-create.html',
  styleUrl: './task-create.scss',
})
export class TaskCreate {
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly taskService = inject(TaskService);
  private readonly membersService = inject(ProjectMembersService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  taskPriorityEnum = TaskPriority;
  form = this.fb.nonNullable.group({
    title: ['', [Validators.required]],
    description: [''],
    priority: [TaskPriority.MEDIUM, [Validators.required]],
    dueDate: [''],
    assigneeId: [null as string | null],
  });
  projectId = toSignal(
    this.store.select(selectProject).pipe(
      filter((p) => !!p),
      map((p) => p.id),
    ),
  );
  loading = signal(false);
  members = signal<ProjectMember[]>([]);

  constructor() {
    effect(() => {
      const projectId = this.projectId();
      if (projectId) {
        this.loadMembers(projectId);
      }
    });
  }

  submit() {
    if (this.form.invalid) {
      return;
    }

    this.loading.set(true);

    const request: CreateTaskRequest = {
      ...this.form.getRawValue(),
      projectId: this.projectId()!,
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

  private loadMembers(projectId: string) {
    this.membersService
      .listMembers(projectId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (list) => {
          this.members.set(list);
        },
      });
  }
}
