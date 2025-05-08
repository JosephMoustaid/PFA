package spring.bricole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.bricole.common.AccountStatus;
import spring.bricole.dto.UserResponseDTO;
import spring.bricole.dto.UserUpdateDTO;
import spring.bricole.model.Notification;
import spring.bricole.model.User;
import spring.bricole.repository.ConversationRepository;
import spring.bricole.repository.NotificationRepository;
import spring.bricole.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class UserService {
    private static final String PROFILE_IMAGE_DIR = "src/main/resources/static/images/profile/";

    private final UserRepository userRepository;
    private ConversationService conversationService;
    private NotificationRepository notificationRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setConversationService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    // get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // get toal number of users`
    public long getTotalNumberOfUsers() {
        return userRepository.count();
    }
    // update user password
    public void updateUserPassword(int id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    // add notification
    public void addNotification(int userId, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification();
        notification.setReceiver(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getGender(),
                user.getProfilePicture(),
                user.getStatus()
        );
    }

    private static final Map<String, String> MIME_TYPE_TO_EXTENSION = Map.of(
            "image/jpg", "jpg",
            "image/jpeg", "jpeg",
            "image/png", "png",
            "image/webp", "webp");

    private String getExtensionFromMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || !MIME_TYPE_TO_EXTENSION.containsKey(mimeType)) {
            throw new IllegalArgumentException("Unsupported image type: " + mimeType);
        }
        return MIME_TYPE_TO_EXTENSION.get(mimeType);
    }


    public void updateUserAccountStatus(int id, AccountStatus status) {
        int updatedCount = userRepository.updateStatusById(id, status);
        if (updatedCount == 0) {
            throw new RuntimeException("No user found with id: " + id);
        }
    }

    // delete a user , you need to delete all the conversations of that user
    public void deleteUser(int id) {

    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponseDTO getUserProfile(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstname(user.getFirstname());
        userResponseDTO.setLastname(user.getLastname());
        userResponseDTO.setPhoneNumberPrefix(user.getPhoneNumberPrefix());
        userResponseDTO.setPhoneNumber(user.getPhoneNumber());
        userResponseDTO.setGender(user.getGender());
        userResponseDTO.setAddress(user.getAddress());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setProfilePicture(user.getProfilePicture());

        return userResponseDTO;
    }

    // updateProfile
    public UserResponseDTO updateProfile(int id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setFirstname(userUpdateDTO.getFirstname());
        user.setLastname(userUpdateDTO.getLastname());
        user.setPhoneNumberPrefix(userUpdateDTO.getPhoneNumberPrefix());
        user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        user.setGender(userUpdateDTO.getGender());
        user.setAddress(userUpdateDTO.getAddress());


        userRepository.save(user);

        return getUserProfile(id);
    }

    public UserResponseDTO uploadProfileImage(int userId, MultipartFile file) {
        // 1. Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Delete previous profile image if it exists
        if (user.getProfilePicture() != null) {
            Path oldImagePath = Paths.get(PROFILE_IMAGE_DIR + user.getProfilePicture());
            try {
                Files.deleteIfExists(oldImagePath);
            } catch (IOException e) {
                // Log warning, but don't fail
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        // 3. Create new filename
        String extension = getExtensionFromMimeType(file);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String newFilename = user.getId() + "_" + user.getFirstname() + "_" + user.getLastname()
                + "_" + timestamp + "." + extension;

        // 4. Save the new file
        try {
            Path newPath = Paths.get(PROFILE_IMAGE_DIR + newFilename);
            Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save new profile image", e);
        }

        // 5. Update user profilePicture in DB
        user.setProfilePicture(newFilename);
        userRepository.save(user);

        // 6. Return DTO
        return mapToUserResponseDTO(user);
    }

    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void removeNotification(User user , Notification notification) {
        user.removeNotification(notification);
        notificationRepository.delete(notification);
        userRepository.save(user);
    }
}
