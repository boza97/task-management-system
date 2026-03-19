import { createFeatureSelector, createSelector } from '@ngrx/store';
import { COMMENTS_FEATURE_KEY, CommentsState } from './comments.reducer';

export const selectCommentsState = createFeatureSelector<CommentsState>(COMMENTS_FEATURE_KEY);
export const selectComments = createSelector(selectCommentsState, (s) => s.items);
export const selectCommentsLoading = createSelector(selectCommentsState, (s) => s.loading);
