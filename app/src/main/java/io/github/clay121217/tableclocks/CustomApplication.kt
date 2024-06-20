package io.github.clay121217.tableclocks

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


class CustomApplication : Application() {


    companion object {
        //初期値
        private const val KEY_DARK_THEME = "set_dark_mode"
        private const val DEFAULT_MODE = "MODE_NIGHT_FOLLOW_SYSTEM"

        //設定ファイルバージョン
        private const val SETTINGS_FILE_VERSION:Int = 1

    }

    override fun onCreate() {
        //設定ファイルの検証・修正
        checkPreference()
        //リセットは未実装、必要になった頃に実装する

        //アプリ側ダークモード設定の反映
        //Q = api29
        if (Build.VERSION.SDK_INT >= 29) {
            applyDarkMode()
        } else {
            //todo:いったん旧バージョンは切る、今後実装
            //applyDarkMode()
        }



        // Called when the application is starting, before any activity, service,
        // or receiver objects (excluding content providers) have been created.
        super.onCreate()

    }

    private fun applyDarkMode() {
        val mode = PreferenceManager
            .getDefaultSharedPreferences(this).getString(KEY_DARK_THEME, DEFAULT_MODE)
        if(mode!=null) {
            putDarkTheme(mode)

            when (mode) {
                "MODE_NIGHT_NO" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "MODE_NIGHT_YES" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "MODE_NIGHT_FOLLOW_SYSTEM" -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )

                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }



    private fun putDarkTheme(value: String) {
        PreferenceManager
            .getDefaultSharedPreferences(this)
            .edit()
            .putString(KEY_DARK_THEME, value)
            .apply()
    }

    private fun checkPreference(){
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
//        val nowSettingsFileVer = preference.getInt("settings_ver", SETTINGS_FILE_VERSION)

        //現在の設定ファイルのバージョンチェック
//        if(nowSettingsFileVer < SETTINGS_FILE_VERSION){
//            //古い場合は必要な場所にリセットをかける予定
//            //まだそういう事態にはなっていないので未実装
//
//        }

        //バージョン情報の書き込み
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = packageInfo.versionName

        preference.edit()
            .putInt("settings_ver", SETTINGS_FILE_VERSION)
            .putString("app_ver", version)
            .apply()

    }

}