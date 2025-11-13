package org.dmgarcia.app.infra;

import org.dmgarcia.app.model.User;

public class Params {
    private static Params instance = new Params();
    private User user;

    private Params(){}

    public static Params getInstance(){
        return instance;
    }

    public static void destroy(){
        instance = new Params();
        instance.user = null;
    }

    public static User getUser() {
        return instance.user;
    }

    public static void setUser(User user) {
        instance.user = user;
    }
}
