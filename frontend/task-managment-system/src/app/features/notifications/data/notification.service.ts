import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { AppNotification } from './models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);

  getMyNotifications() {
    return this.http.get<AppNotification[]>(`${environment.apiUrl}/notifications`);
  }

  getUnreadCount() {
    return this.http.get<number>(`${environment.apiUrl}/notifications/unread-count`);
  }

  markAsRead(id: string) {
    return this.http.patch<AppNotification>(`${environment.apiUrl}/notifications/${id}/read`, {});
  }

  markAllAsRead() {
    return this.http.patch<void>(`${environment.apiUrl}/notifications/read-all`, {});
  }
}
