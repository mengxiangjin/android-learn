package com.jin.note;

import android.app.Application;

public class NoteApplication extends Application {

    public static NoteApplication app;


    public NoteApplication() {
        app = this;
    }


}
