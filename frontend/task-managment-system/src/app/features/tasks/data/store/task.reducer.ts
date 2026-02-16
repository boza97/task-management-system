import { createReducer, on } from '@ngrx/store';
import { Task } from '../models/task.model';
import * as TaskActions from './task.actions';
import { TaskFilters } from '../models/task-filters.model';

export const TASK_FEATURE_KEY = 'task';

export interface TaskState {
  selected: Task | null;
  loading: boolean;
  error: string | null;
  updating: boolean;
  tasks: Task[];
  tasksLoading: boolean;
  tasksError: string | null;
  filters: TaskFilters;
}

export const initialState: TaskState = {
  selected: null,
  loading: false,
  error: null,
  updating: false,
  tasks: [],
  tasksLoading: false,
  tasksError: null,
  filters: {
    search: null,
    priority: null,
    statusCode: null,
    assigneeId: null,
    dueDateFrom: null,
    dueDateTo: null,
  },
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
  on(TaskActions.loadTasks, (state) => ({
    ...state,
    tasksLoading: true,
    tasksError: null,
  })),
  on(TaskActions.loadTasksSuccess, (state, { tasks }) => ({
    ...state,
    tasks,
    tasksLoading: false,
  })),
  on(TaskActions.loadTasksFailure, (state, { error }) => ({
    ...state,
    tasksLoading: false,
    tasksError: error,
  })),
  on(TaskActions.setTaskFilters, (state, { filters }) => ({
    ...state,
    filters: {
      ...state.filters,
      ...filters,
    },
  })),
  on(TaskActions.resetTaskFilters, (state) => ({
    ...state,
    filters: initialState.filters,
  })),
  on(TaskActions.deleteTaskSuccess, (state, { taskId }) => ({
    ...state,
    list: state.tasks.filter((t) => t.id !== taskId),
  })),
);
