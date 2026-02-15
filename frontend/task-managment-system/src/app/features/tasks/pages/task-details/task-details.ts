import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { toSignal } from '@angular/core/rxjs-interop';
import { selectTask, selectTaskError, selectTaskLoading } from '../../data/store/task.selectors';
import {
  changeTaskAssignee,
  changeTaskStatus,
  loadTask,
  updateTask,
} from '../../data/store/task.actions';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskPriority } from '../../data/models/create-task-request.model';
import { TaskStatus } from '../../data/models/task-status.model';
import { selectProject } from '../../../projects/data/store/project.selectors';
import { filter, map, switchMap } from 'rxjs';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { Task } from '../../data/models/task.model';
import { TaskService } from '../../data/task.service';
import { TaskComments } from '../../comments/components/task-comments/task-comments';
import { TaskAudit } from '../../audit/components/task-audit/task-audit';

@Component({
  selector: 'app-task-details',
  imports: [DatePipe, RouterLink, ReactiveFormsModule, FormsModule, TaskComments, TaskAudit],
  templateUrl: './task-details.html',
  styleUrl: './task-details.scss',
})
export class TaskDetails {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly membersService = inject(ProjectMembersService);
  private readonly tasksService = inject(TaskService);

  task = toSignal(this.store.select(selectTask));
  loading = toSignal(this.store.select(selectTaskLoading));
  error = toSignal(this.store.select(selectTaskError));
  members = toSignal(
    this.store.select(selectProject).pipe(
      filter((x) => !!x),
      map((x) => x.id),
      switchMap((projectId) => this.membersService.listMembers(projectId)),
    ),
    { initialValue: [] },
  );
  taskStatuses = toSignal(this.tasksService.getTaskStatuses());

  activeTab = signal<'overview' | 'comments' | 'history'>('overview');
  editMain = signal(false);
  mainForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
    description: [null as string | null, [Validators.maxLength(5000)]],
    priority: [TaskPriority.MEDIUM as string],
    dueDate: [null as string | null],
  });
  taskPriorityEnum = TaskPriority;

  constructor() {
    const taskId = this.route.snapshot.paramMap.get('taskId');
    if (taskId) {
      this.store.dispatch(loadTask({ taskId }));
    }

    effect(() => {
      const task = this.task();
      if (task) {
        this.updateMainForm(task);
      }
    });
  }

  saveMain() {
    const task = this.task();
    if (!task || this.mainForm.invalid) {
      return;
    }

    const value = this.mainForm.getRawValue();

    this.store.dispatch(
      updateTask({
        taskId: task.id,
        data: {
          title: value.title,
          description: value.description,
          priority: value.priority as TaskPriority,
          dueDate: value.dueDate,
        },
      }),
    );

    this.editMain.set(false);
  }

  changeStatus(statusCode: string) {
    const task = this.task();
    if (!task || statusCode === task.statusCode) {
      return;
    }

    this.store.dispatch(
      changeTaskStatus({
        taskId: task.id,
        statusCode,
      }),
    );
  }

  changeAssignee(userId: string | null) {
    const task = this.task();
    if (!task || userId === task.assigneeId) {
      return;
    }

    this.store.dispatch(
      changeTaskAssignee({
        taskId: task.id,
        assigneeId: userId,
      }),
    );
  }

  private updateMainForm(task: Task) {
    this.mainForm.patchValue(
      {
        title: task.title,
        description: task.description,
        priority: task.priority as string,
        dueDate: task.dueDate,
      },
      { emitEvent: false },
    );
  }
}
