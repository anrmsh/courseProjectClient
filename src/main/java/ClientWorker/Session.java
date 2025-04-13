package ClientWorker;

import salonOrg.User;

public class Session {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getUserLogin() {
        return currentUser != null ? currentUser.getLogin() : "";
    }

    public static int getUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public static void clear() {
        currentUser = null;
    }

}
