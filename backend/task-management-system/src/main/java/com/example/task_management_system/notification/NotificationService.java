package com.example.task_management_system.notification;

import com.example.task_management_system.notification.dto.NotificationResponse;
import com.example.task_management_system.task.Task;
import com.example.task_management_system.user.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void notifyTaskAssigned(User recipient, Task task, User performedBy);

    List<NotificationResponse> getMyNotifications();

    long getUnreadCount();

    NotificationResponse markAsRead(UUID notificationId);

    void markAllAsRead();
}
