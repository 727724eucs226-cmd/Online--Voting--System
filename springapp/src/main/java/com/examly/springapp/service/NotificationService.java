package com.examly.springapp.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.examly.springapp.dto.NotificationResponse;
import com.examly.springapp.model.Notification;
import com.examly.springapp.model.Poll;
import com.examly.springapp.repository.NotificationRepository;
import com.examly.springapp.repository.PollRepository;



@Service
public class NotificationService {


    private final NotificationRepository notificationRepository;

    private final PollRepository pollRepository;


    public NotificationService(
            NotificationRepository notificationRepository,
            PollRepository pollRepository) {

        this.notificationRepository = notificationRepository;
        this.pollRepository = pollRepository;
    }



    public List<NotificationResponse> getNotifications(String username) {


        createExpiredPollNotifications();


        return notificationRepository
                .findByUsername(username)
                .stream()
                .map(n ->
                    new NotificationResponse(
                        n.getId(),
                        n.getMessage(),
                        n.isReadStatus(),
                        n.getCreatedAt()
                    )
                )
                .toList();
    }




    private void createExpiredPollNotifications() {


        List<Poll> polls = pollRepository.findAll();


        for(Poll poll : polls) {


            if(poll.getExpiresAt()!=null &&
               poll.getExpiresAt().isBefore(LocalDateTime.now())) {


                String message =
                "Your poll '" + poll.getTitle()
                + "' duration has ended.";


                boolean exists =
                    notificationRepository
                    .findByUsername(poll.getCreatedBy())
                    .stream()
                    .anyMatch(n ->
                    n.getMessage().equals(message));


                if(!exists) {

                    Notification notification =
                            new Notification();


                    notification.setUsername(
                            poll.getCreatedBy()
                    );


                    notification.setMessage(message);


                    notification.setCreatedAt(
                            LocalDateTime.now()
                    );


                    notificationRepository.save(notification);
                }
            }
        }
    }





    public String markAsRead(Long id) {


        Notification notification =
        notificationRepository.findById(id)
        .orElseThrow(() ->
        new RuntimeException("Notification not found"));


        notification.setReadStatus(true);


        notificationRepository.save(notification);


        return "Notification marked as read";
    }

}