import { createAction, props } from '@ngrx/store';
import { Comment } from '../model/comment.model';

export const loadComments = createAction('[Comments] Load', props<{ taskId: string }>());
export const loadCommentsSuccess = createAction(
  '[Comments] Load Success',
  props<{ comments: Comment[] }>(),
);

export const addComment = createAction(
  '[Comments] Add',
  props<{ taskId: string; content: string }>(),
);
export const addCommentSuccess = createAction(
  '[Comments] Add Success',
  props<{ comment: Comment }>(),
);

export const deleteComment = createAction(
  '[Comments] Delete',
  props<{ taskId: string; commentId: string }>(),
);
export const deleteCommentSuccess = createAction(
  '[Comments] Delete Success',
  props<{ commentId: string }>(),
);
