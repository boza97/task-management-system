import { createReducer, on } from '@ngrx/store';
import { Task } from '../models/task.model';
import * as TaskActions from './task.actions';

export const TASK_FEATURE_KEY = 'task';

export interface TaskState {
  selected: Task | null;
  loading: boolean;
  error: string | null;
  updating: boolean;
}

export const initialState: TaskState = {
  selected: null,
  loading: false,
  error: null,
  updating: false,
};

export const taskReducer = createReducer(
  initialState,
  on(TaskActions.loadTask, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(TaskActions.loadTaskSuccess, (state, { task }) => ({
    ...state,
    selected: task,
    loading: false,
    error: null,
  })),
  on(TaskActions.loadTaskFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
  on(
    TaskActions.updateTaskSuccess,
    TaskActions.changeTaskStatusSuccess,
    TaskActions.changeTaskAssigneeSuccess,
    (state, { task }) => ({
      ...state,
      selected: task,
      updating: false,
      error: null,
    }),
  ),
  on(
    TaskActions.updateTaskFailure,
    TaskActions.changeTaskStatusFailure,
    TaskActions.changeTaskAssigneeFailure,
    (state, { error }) => ({
      ...state,
      updating: false,
      error,
    }),
  ),
);
