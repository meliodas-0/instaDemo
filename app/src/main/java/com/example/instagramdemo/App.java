package com.example.instagramdemo;

import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("450c0e897ea70ea785730c5b90dad1ac692bb487")
                .clientKey("b30c4f1ef5b11f0c53cf04b740e6f32086da0dee")
                .server("http://13.233.168.22:80/parse")
                .build()
        );

            //Kt2gaiYnc1ym
    }
}