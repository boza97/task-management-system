import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { TaskService } from '../task.service';
import { map, switchMap, catchError, of, tap } from 'rxjs';
import * as TaskActions from './task.actions';
import { ToastService } from '../../../../shared/services/toast.service';

@Injectable()
export class TaskEffects {
  private readonly actions$ = inject(Actions);
  private readonly taskService = inject(TaskService);
  private readonly toastService = inject(ToastService);

  loadTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.loadTask),
      switchMap(({ taskId }) =>
        this.taskService.getTask(taskId).pipe(
          map((task) => TaskActions.loadTaskSuccess({ task })),
          catchError(() => of(TaskActions.loadTaskFailure({ error: 'Failed to load task' }))),
        ),
      ),
    ),
  );

  updateTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.updateTask),
      switchMap(({ taskId, data }) =>
        this.taskService.updateTask(taskId, data).pipe(
          map((task) => TaskActions.updateTaskSuccess({ task })),
          catchError(() => of(TaskActions.updateTaskFailure({ error: 'Failed to update task' }))),
        ),
      ),
    ),
  );

  updateTaskSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskActions.updateTaskSuccess),
        tap(() => this.toastService.show('Task updated successfully', 'success')),
      ),
    { dispatch: false },
  );

  changeStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.changeTaskStatus),
      switchMap(({ taskId, statusCode }) =>
        this.taskService.changeStatus(taskId, statusCode).pipe(
          map((task) => TaskActions.changeTaskStatusSuccess({ task })),
          catchError(() =>
            of(TaskActions.changeTaskStatusFailure({ error: 'Failed to change task status' })),
          ),
        ),
      ),
    ),
  );

  changeTaskStatusSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskActions.changeTaskStatusSuccess),
        tap(() => this.toastService.show('Task status changed successfully', 'success')),
      ),
    { dispatch: false },
  );

  changeAssignee$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.changeTaskAssignee),
      switchMap(({ taskId, assigneeId }) =>
        this.taskService.changeAssignee(taskId, assigneeId).pipe(
          map((task) => TaskActions.changeTaskAssigneeSuccess({ task })),
          catchError(() =>
            of(TaskActions.changeTaskAssigneeFailure({ error: 'Failed to change task assignee' })),
          ),
        ),
      ),
    ),
  );

  changeAssigneeSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskActions.changeTaskAssigneeSuccess),
        tap(() => this.toastService.show('Task assignee changed successfully', 'success')),
      ),
    { dispatch: false },
  );

  updateTaskFailureToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(
          TaskActions.updateTaskFailure,
          TaskActions.changeTaskStatusFailure,
          TaskActions.changeTaskAssigneeFailure,
        ),
        tap((err) => this.toastService.show(err.error, 'error')),
      ),
    { dispatch: false },
  );
}
