import { createReducer, on } from '@ngrx/store';
import { Comment } from '../model/comment.model';
import * as CommentsAction from './comments.action';

export const COMMENTS_FEATURE_KEY = 'taskComments';

export interface CommentsState {
  items: Comment[];
  loading: boolean;
}

export const initialState: CommentsState = {
  items: [],
  loading: false,
};

export const commentsReducer = createReducer(
  initialState,

  on(CommentsAction.loadComments, (state) => ({
    ...state,
    loading: true,
  })),
  on(CommentsAction.loadCommentsSuccess, (state, { comments }) => ({
    ...state,
    items: comments,
    loading: false,
  })),
  on(CommentsAction.addCommentSuccess, (state, { comment }) => ({
    ...state,
    items: [...state.items, comment],
  })),
  on(CommentsAction.deleteCommentSuccess, (state, { commentId }) => ({
    ...state,
    items: state.items.filter((c) => c.id !== commentId),
  })),
);
