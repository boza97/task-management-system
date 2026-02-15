import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { CommentService } from '../comment.api.service';
import * as CommentsAction from './comments.action';
import { map, switchMap, tap } from 'rxjs';
import { ToastService } from '../../../../../shared/services/toast.service';

@Injectable()
export class CommentsEffects {
  private readonly actions$ = inject(Actions);
  private readonly service = inject(CommentService);
  private readonly toastService = inject(ToastService);

  load$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CommentsAction.loadComments),
      switchMap(({ taskId }) =>
        this.service
          .getComments(taskId)
          .pipe(map((comments) => CommentsAction.loadCommentsSuccess({ comments }))),
      ),
    ),
  );

  add$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CommentsAction.addComment),
      switchMap(({ taskId, content }) =>
        this.service
          .addComment(taskId, content)
          .pipe(map((comment) => CommentsAction.addCommentSuccess({ comment }))),
      ),
    ),
  );

  addCommentSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(CommentsAction.addCommentSuccess),
        tap(() => this.toastService.show('Comment added successfuly', 'success')),
      ),
    { dispatch: false },
  );

  delete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CommentsAction.deleteComment),
      switchMap(({ taskId, commentId }) =>
        this.service
          .deleteComment(taskId, commentId)
          .pipe(map(() => CommentsAction.deleteCommentSuccess({ commentId }))),
      ),
    ),
  );

  deleteCommentSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(CommentsAction.deleteCommentSuccess),
        tap(() => this.toastService.show('Comment deleted successfuly', 'success')),
      ),
    { dispatch: false },
  );
}
