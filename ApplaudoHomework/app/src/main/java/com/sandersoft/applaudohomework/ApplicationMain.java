package com.sandersoft.applaudohomework;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Sander on 11/05/2017.
 */

public class ApplicationMain extends Application {
    //the webmanager
    public WebManager webManager;
    //the instance of this class
    public static ApplicationMain instance;

    static Toast t_msg;

    public ApplicationMain(){
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        webManager = new WebManager();
    }

    public static Context getContext(){
        return instance;
    }

    /**
     * Show a toast, if there was a toast shown, it removes it so the toasts dont stack
     * @param resId string id to show
     */
    public static void showToast(final int resId){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (t_msg != null) t_msg.cancel();
                t_msg = Toast.makeText(instance, resId, Toast.LENGTH_SHORT);
                t_msg.show();
            }
        });
    }
    /**
     * Show a toast, if there was a toast shown, it removes it so the toasts dont stack
     * @param text text to show
     */
    public static void showToast(final String text){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (t_msg != null) t_msg.cancel();
                t_msg = Toast.makeText(instance, text, Toast.LENGTH_SHORT);
                t_msg.show();
            }
        });
    }

}
