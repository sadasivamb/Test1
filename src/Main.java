import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {


    static class UserSession {

        private String userId;
        private boolean valid;

        public UserSession(String userId, boolean valid) {
            this.userId = userId;
            this.valid = valid;
        }
        public String getUserId() {
            return userId;
        }

        public boolean isValid() {
            return valid;
        }

    }
    interface UserSessionManager {
        public UserSession create(String userId);
        public UserSession retrieve(String userId);
        public void inValidate(UserSession session);
    }
    static class UserSessionManagerImpl implements UserSessionManager {


        private static final UserSessionManager sessionManager = new UserSessionManagerImpl(); // Signleton instance of Session Manager

        private static final Map<String, UserSession> sessionTracker = new ConcurrentHashMap<>();

        private UserSessionManagerImpl() {
            System.out.println("One instance of singleton");
        }
        public static UserSessionManager getInstance() {
            return sessionManager;
        }

        @Override
        public UserSession create(String userId) {
            UserSession session = null;
            synchronized (UserSessionManagerImpl.class) {
                if (sessionTracker.containsKey(userId)) {
                    inValidate(sessionTracker.get(userId));
                    System.out.println("Session already exists for " + userId + ", invalidated.");
                }
                session = new UserSession(userId, true);
                sessionTracker.put(userId, session);
                System.out.println("New session created for userId "+userId);
            }
            return session;
        }

        @Override
        public UserSession retrieve(String userId) {
            return sessionTracker.get(userId);
        }

        @Override
        public void inValidate(UserSession session) {
            sessionTracker.remove(session.getUserId());
        }
    }

    public static void main(String[] args) {

        UserSessionManager firstSessionManager = UserSessionManagerImpl.getInstance();
        UserSession firstSession = firstSessionManager.create("Sada");
        System.out.println(firstSession.getUserId());

        Thread simpleThread = new Thread(() -> {
            UserSessionManager secondSessionManager = UserSessionManagerImpl.getInstance();
            UserSession secondSession = secondSessionManager.create("Sada");
            System.out.println(secondSession.getUserId());
        });

        simpleThread.start();
    }
}