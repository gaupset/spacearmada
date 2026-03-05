package com.invaders99.service;

public class CallbackFactory {

    public static FirebaseService.FirebaseCallback handle() {
        return new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("OnSuccess: " + response);
            }

            @Override
            public void onFailure(String error) {
                System.out.println("OnFailure: " + error);
            }
        };
    }

    public static FirebaseService.FirebaseCallback handle(Runnable success) {
        return new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                success.run();
            }

            @Override
            public void onFailure(String error) {
                System.out.println(error);
            }
        };
    }
    public static FirebaseService.FirebaseCallback handle(Runnable success, Runnable failure) {
        return new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                success.run();
            }

            @Override
            public void onFailure(String error) {
                System.out.println(error);
                failure.run();
            }
        };
    }

}
