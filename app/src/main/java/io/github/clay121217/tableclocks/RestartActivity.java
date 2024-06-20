package io.github.clay121217.tableclocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RestartActivity extends Activity {
    //アプリ再起動をするActivity

    public static final String EXTRA_MAIN_PID = "RestartActivity.main_pid";

    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName(context.getPackageName(), RestartActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // メインプロセスの PID を Intent に保存しておく
        intent.putExtra(RestartActivity.EXTRA_MAIN_PID, android.os.Process.myPid());
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. メインプロセスを Kill する
        Intent intent = getIntent();
        int mainPid = intent.getIntExtra(EXTRA_MAIN_PID, -1);
        android.os.Process.killProcess(mainPid);

        // 2. MainActivity を再起動する
        Context context = getApplicationContext();
        Intent restartIntent = new Intent(Intent.ACTION_MAIN);
        restartIntent.setClassName(context.getPackageName(), MainActivity.class.getName());
        restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(restartIntent);

        // 3. RestartActivity を終了する
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}