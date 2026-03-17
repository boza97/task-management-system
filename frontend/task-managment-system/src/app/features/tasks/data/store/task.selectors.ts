import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TaskState, TASK_FEATURE_KEY } from './task.reducer';

export const selectTaskState = createFeatureSelector<TaskState>(TASK_FEATURE_KEY);

export const selectTask = createSelector(selectTaskState, (s) => s.selected);
export const selectTaskLoading = createSelector(selectTaskState, (s) => s.loading);
export const selectTaskError = createSelector(selectTaskState, (s) => s.error);

export const selectTasks = createSelector(selectTaskState, (s) => s.tasks);
export const selectTaskListLoading = createSelector(selectTaskState, (s) => s.tasksLoading);
export const selectTaskListError = createSelector(selectTaskState, (s) => s.tasksError);
export const selectTaskFilters = createSelector(selectTaskState, (s) => s.filters);
