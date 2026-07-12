# Online Voting System

Online Voting System is a secure backend application that allows users to create polls, participate in voting, and view results. It provides authentication, poll management, admin controls, notifications, and secure vote handling.

## Tech Stack

- Java 17+
- Spring Boot
- Maven
- MySQL Database
- Spring Security + JWT

## Run Locally

1. Clone the repository:

git clone <repository-url>

2. Configure MySQL database in `application.properties`.

3. Run the application:

mvn spring-boot:run

Server runs on:

http://localhost:8080


## MVC Structure

src/main/java/com/examly/springapp

controller
- AdminController.java
- AdminPollController.java
- AuthController.java
- NotificationController.java
- PollController.java
- UserController.java

dto
- AuthRequest.java
- AuthResponse.java
- NotificationResponse.java
- PollCreateRequest.java
- PollResponse.java
- PollResultsResponse.java
- PollVoteRequest.java
- RegisterRequest.java
- UpdateProfileRequest.java
- UserProfileResponse.java

model
- User.java
- Poll.java
- PollOption.java
- Vote.java
- Notification.java
- PollStatus.java
- Role.java

repository
- UserRepository.java
- PollRepository.java
- PollOptionRepository.java
- VoteRepository.java
- NotificationRepository.java

service
- AuthService.java
- CustomUserDetailsService.java
- NotificationService.java
- PollService.java
- UserService.java

security
- Security configuration files


## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register user |
| POST | /api/auth/login | User login |
| POST | /api/polls | Create poll |
| GET | /api/polls | Get all polls |
| GET | /api/polls/{id} | Get poll details |
| POST | /api/polls/{id}/vote | Cast vote |
| GET | /api/polls/{id}/results | View results |
| PUT | /api/polls/{id}/open | Open poll |
| PUT | /api/polls/{id}/close | Close poll |
| PUT | /api/polls/{id}/privacy | Update privacy |
| GET | /api/polls/status | Get poll status |
| GET | /api/polls/search | Search polls |
| GET | /api/polls/public | Get public polls |
| GET | /api/polls/private/{privateLink} | Access private poll |
| GET | /api/polls/my | Get user polls |
| GET | /api/polls/history | Voting history |
| GET | /api/admin/users | View users |
| GET | /api/admin/users/{id} | View user details |
| PUT | /api/admin/users/{id}/role | Update user role |
| DELETE | /api/admin/users/{id} | Delete user |


## Author

727724eucs226-cmd
