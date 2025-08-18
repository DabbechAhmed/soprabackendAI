package com.example.AIProject.services.notification;

import com.example.AIProject.dto.NotificationDto;
import com.example.AIProject.entities.Application;
import com.example.AIProject.entities.Notification;
import com.example.AIProject.entities.User;
import com.example.AIProject.enums.NotificationType;
import com.example.AIProject.repository.NotificationRepository;
import com.example.AIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements INotificationService{

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public void notifyApplicationSubmitted(Application application) {
        // Notifier l'employé
        createNotification(
            application.getUser(),
            "Candidature soumise",
            "Votre candidature pour le poste \"" + application.getPosition().getTitle() + "\" a été soumise avec succès.",
            NotificationType.APPLICATION_SUBMITTED,
            application
        );

        // Notifier les HR de la branche cible
        List<User> hrUsers = userRepository.findHRUsersByBranch(application.getPosition().getTargetBranch().getId());
        for (User hrUser : hrUsers) {
            createNotification(
                hrUser,
                "Nouvelle candidature",
                application.getUser().getFullName() + " a postulé pour le poste \"" + application.getPosition().getTitle() + "\".",
                NotificationType.APPLICATION_SUBMITTED,
                application
            );
        }
    }

    public void notifyApplicationAccepted(Application application) {
        createNotification(
            application.getUser(),
            "Candidature acceptée",
            "Félicitations ! Votre candidature pour le poste \"" + application.getPosition().getTitle() + "\" a été acceptée.",
            NotificationType.APPLICATION_ACCEPTED,
            application
        );
    }

    public void notifyApplicationRejected(Application application) {
        createNotification(
            application.getUser(),
            "Candidature non retenue",
            "Votre candidature pour le poste \"" + application.getPosition().getTitle() + "\" n'a pas été retenue cette fois-ci.",
            NotificationType.APPLICATION_REJECTED,
            application
        );
    }

    private void createNotification(User user, String title, String message, NotificationType type, Application application) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setApplication(application);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            });
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
        if (notification.getApplication() != null) {
            dto.setApplicationId(notification.getApplication().getId());
            dto.setPositionTitle(notification.getApplication().getPosition().getTitle());
            dto.setApplicantName(notification.getApplication().getUser().getFullName());
        }
        return dto;
    }
}