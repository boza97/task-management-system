import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { interval, startWith, switchMap } from 'rxjs';
import { NotificationService } from '../../data/notification.service';
import { AppNotification } from '../../data/models/notification.model';

const REFRESH_INTERVAL_MS = 30_000;

@Component({
  selector: 'app-notification-bell',
  imports: [DatePipe],
  templateUrl: './notification-bell.html',
  styleUrl: './notification-bell.scss',
})
export class NotificationBell {
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  notifications = signal<AppNotification[]>([]);
  unreadCount = signal(0);
  open = signal(false);

  constructor() {
    interval(REFRESH_INTERVAL_MS)
      .pipe(
        startWith(0),
        switchMap(() => this.notificationService.getUnreadCount()),
        takeUntilDestroyed(),
      )
      .subscribe((count) => this.unreadCount.set(count));
  }

  toggle() {
    this.open.update((value) => !value);

    if (this.open()) {
      this.loadNotifications();
    }
  }

  close() {
    this.open.set(false);
  }

  onNotificationClick(notification: AppNotification) {
    if (!notification.read) {
      this.markAsRead(notification);
    }

    if (notification.projectId && notification.taskId) {
      this.router.navigate(['/projects', notification.projectId, 'tasks', notification.taskId]);
    }

    this.close();
  }

  markAsRead(notification: AppNotification) {
    this.notificationService.markAsRead(notification.id).subscribe((updated) => {
      this.notifications.update((items) =>
        items.map((item) => (item.id === updated.id ? updated : item)),
      );
      this.unreadCount.update((count) => Math.max(0, count - 1));
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.notifications.update((items) => items.map((item) => ({ ...item, read: true })));
      this.unreadCount.set(0);
    });
  }

  private loadNotifications() {
    this.notificationService.getMyNotifications().subscribe((items) => {
      this.notifications.set(items);
    });
  }
}
