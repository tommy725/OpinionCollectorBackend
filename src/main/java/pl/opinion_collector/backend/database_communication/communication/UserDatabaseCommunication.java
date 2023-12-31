package pl.opinion_collector.backend.database_communication.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.opinion_collector.backend.database_communication.model.User;
import pl.opinion_collector.backend.database_communication.repository.UserRepository;
import pl.opinion_collector.backend.database_communication.utils.EntityPreUpdater;

import java.util.List;

@Component
@Transactional
public class UserDatabaseCommunication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityPreUpdater<User> entityPreUpdater;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    public User createUser(String firstName, String lastName, String email, String passwordHash, String profilePictureUrl, Boolean isAdmin) {
        User user = new User(firstName, lastName, email, passwordHash, profilePictureUrl, isAdmin);
        return userRepository.save(user);
    }

    public void updateUser(Long userId, String firstName, String lastName, String email, String passwordHash, String profilePictureUrl, Boolean isAdmin) {
        entityPreUpdater.saveOldData(getUserById(userId));
        userRepository.updateUser(userId, firstName, lastName, email, passwordHash, profilePictureUrl, isAdmin);
    }

}
