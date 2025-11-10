package org.dmgarcia.app.infra;

import org.dmgarcia.app.model.User;

public class Params {
    private static Params instance = new Params();
    private static User user;

    private Params(){}

    public static Params getInstance(){
        return instance;
    }

    public static void destroy(){
        instance = new Params();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Params.user = user;
    }
}
