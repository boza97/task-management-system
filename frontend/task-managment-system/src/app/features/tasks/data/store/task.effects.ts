import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { TaskService } from '../task.service';
import { map, switchMap, catchError, of, tap, withLatestFrom, filter } from 'rxjs';
import * as TaskActions from './task.actions';
import { ToastService } from '../../../../shared/services/toast.service';
import { Store } from '@ngrx/store';
import { selectTaskFilters } from './task.selectors';
import { selectProject } from '../../../projects/data/store/project.selectors';

@Injectable()
export class TaskEffects {
  private readonly actions$ = inject(Actions);
  private readonly taskService = inject(TaskService);
  private readonly toastService = inject(ToastService);
  private readonly store = inject(Store);

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

  loadTasks$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.loadTasks),
      withLatestFrom(this.store.select(selectTaskFilters)),
      switchMap(([{ projectId }, filters]) =>
        this.taskService.getTasks(projectId, filters).pipe(
          map((tasks) => TaskActions.loadTasksSuccess({ tasks })),
          catchError(() => of(TaskActions.loadTasksFailure({ error: 'Failed to load tasks' }))),
        ),
      ),
    ),
  );

  reloadOnFiltersChange$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.setTaskFilters),
      withLatestFrom(
        this.store.select(selectProject).pipe(
          filter((x) => !!x),
          map((x) => x.id),
        ),
      ),
      map(([_, projectId]) => TaskActions.loadTasks({ projectId })),
    ),
  );

  deleteTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskActions.deleteTask),
      switchMap(({ taskId }) =>
        this.taskService.deleteTask(taskId).pipe(
          map(() => TaskActions.deleteTaskSuccess({ taskId })),
          catchError(() => of(TaskActions.deleteTaskFailure({ error: 'Failed to delete task' }))),
        ),
      ),
    ),
  );

  deleteTaskSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskActions.deleteTaskSuccess),
        tap(() => this.toastService.show('Task deleted', 'success')),
      ),
    { dispatch: false },
  );

  deleteTaskErrorToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskActions.deleteTaskFailure),
        tap(() => this.toastService.show('Failed to delete task', 'error')),
      ),
    { dispatch: false },
  );
}
