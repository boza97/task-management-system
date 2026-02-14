import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TaskState, TASK_FEATURE_KEY } from './tas.reducer';

export const selectTaskState = createFeatureSelector<TaskState>(TASK_FEATURE_KEY);

export const selectTask = createSelector(selectTaskState, (s) => s.selected);
export const selectTaskLoading = createSelector(selectTaskState, (s) => s.loading);
export const selectTaskError = createSelector(selectTaskState, (s) => s.error);
