import { Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Store } from '@ngrx/store';
import { selectComments, selectCommentsLoading } from '../../data/store/comments.selectors';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { addComment, deleteComment, loadComments } from '../../data/store/comments.action';
import { selectTask } from '../../../data/store/task.selectors';
import { filter, map } from 'rxjs';
import { DatePipe } from '@angular/common';
import { TokenStorageService } from '../../../../../shared/services/token-storage.service';
import { Comment } from '../../data/model/comment.model';

@Component({
  selector: 'app-task-comments',
  imports: [ReactiveFormsModule, FormsModule, DatePipe],
  templateUrl: './task-comments.html',
  styleUrl: './task-comments.scss',
})
export class TaskComments {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly tokenStorageService = inject(TokenStorageService);

  comments = toSignal(this.store.select(selectComments));
  loading = toSignal(this.store.select(selectCommentsLoading));
  taskId = toSignal(
    this.store.select(selectTask).pipe(
      filter((x) => !!x),
      map((x) => x.id),
    ),
  );

  form = this.fb.nonNullable.group({
    content: ['', [Validators.required, Validators.maxLength(2000)]],
  });
  currentUser = this.tokenStorageService.getUser()!;

  constructor() {
    effect(() => {
      const taskId = this.taskId();
      if (taskId) {
        this.store.dispatch(loadComments({ taskId: this.taskId()! }));
      }
    });
  }

  add() {
    if (this.form.invalid) {
      return;
    }

    this.store.dispatch(
      addComment({
        taskId: this.taskId()!,
        content: this.form.value.content!,
      }),
    );

    this.form.reset();
  }

  canDelete(comment: Comment) {
    return comment.authorId === this.currentUser.sub || this.currentUser.roles.includes('ADMIN');
  }

  delete(id: string) {
    this.store.dispatch(
      deleteComment({
        taskId: this.taskId()!,
        commentId: id,
      }),
    );
  }
}
