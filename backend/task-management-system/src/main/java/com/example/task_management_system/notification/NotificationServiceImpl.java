package com.example.task_management_system.notification;

import com.example.task_management_system.common.exception.ResourceNotFoundException;
import com.example.task_management_system.common.security.CurrentUserProvider;
import com.example.task_management_system.notification.dto.NotificationResponse;
import com.example.task_management_system.task.Task;
import com.example.task_management_system.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public void notifyTaskAssigned(User recipient, Task task, User performedBy) {
        if (recipient.getId().equals(performedBy.getId())) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(NotificationType.TASK_ASSIGNED);
        notification.setMessage(
                performedBy.getFirstName() + " " + performedBy.getLastName()
                + " assigned you to task \"" + task.getTitle() + "\""
        );
        notification.setTaskId(task.getId());
        notification.setProjectId(task.getProject().getId());

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = currentUserProvider.getCurrentUser();
        return notificationRepository.findTop50ByRecipientIdOrderByCreatedAtDesc(currentUser.getId())
                                     .stream()
                                     .map(this::mapToResponse)
                                     .toList();
    }

    @Override
    public long getUnreadCount() {
        User currentUser = currentUserProvider.getCurrentUser();
        return notificationRepository.countByRecipientIdAndReadFalse(currentUser.getId());
    }

    @Override
    public NotificationResponse markAsRead(UUID notificationId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setRead(true);

        return mapToResponse(notification);
    }

    @Override
    public void markAllAsRead() {
        User currentUser = currentUserProvider.getCurrentUser();
        notificationRepository.markAllAsRead(currentUser.getId());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getTaskId(),
                notification.getProjectId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
