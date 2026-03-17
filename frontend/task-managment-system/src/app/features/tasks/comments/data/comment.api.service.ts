import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { Comment } from './model/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly http = inject(HttpClient);

  getComments(taskId: string) {
    return this.http.get<Comment[]>(`${environment.apiUrl}/tasks/${taskId}/comments`);
  }

  addComment(taskId: string, content: string) {
    return this.http.post<Comment>(`${environment.apiUrl}/tasks/${taskId}/comments`, { content });
  }

  deleteComment(taskId: string, commentId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/tasks/${taskId}/comments/${commentId}`);
  }
}
