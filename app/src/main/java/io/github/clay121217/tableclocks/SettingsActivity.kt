package io.github.clay121217.tableclocks

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() , Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener {

    private var restartFlag: Boolean = false //設定変更時の再起動フラグ、再生成時に消えちゃう？ので注意

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // 戻るの上書き用コールバックをセット
        onBackPressedDispatcher.addCallback(callback)

        //右上の戻るボタン
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //戻るアクションの処理内容
    //OnBackPressedCallbackのコンストラクタはtrueにすることでコールバックを有効にする
    private val callback = object : OnBackPressedCallback(true) {
        //コールバックのhandleOnBackPressedを呼び出して、戻るキーを押したときの処理を記述
        override fun handleOnBackPressed() {
            //戻る時にrestartFlagをチェック
            if(restartFlag){
                AlertDialog.Builder(this@SettingsActivity) // コンテキスト、FragmentではActivityを取得して生成
                    .setTitle(getString(R.string.restart_dialog_text))
                    .setPositiveButton("OK") { dialog, which ->
                        // Yesが押された時の挙動
                        restartApp()
                    }
                    .show()
            }else{
                finish()
            }

            return
        }
    }

    //再起動処理
    private fun restartApp() {
        val context = applicationContext
        val intent = RestartActivity.createIntent(context)
        // RestartActivity を起動（AndroidManifest.xml での宣言により別プロセスで起動する
        context.startActivity(intent)
    }

    //OnPreferenceChangeListenerインターフェースのための実装
    //チェンジリスナーの実体はこれ
    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        // 変更された設定項目 (preference) のkeyを取得
        val changedPreferenceKey = preference.key

        // 新しい設定値 (newValue) を取得
        //val newValueString = newValue?.toString() ?: ""

        // 変更内容に応じて処理を行う場合はここに記述
        when (changedPreferenceKey) {
            //ダークモード変更時
            "set_dark_mode" -> {
                restartFlag = true
            }
        }

        return true
    }

    //OnPreferenceClickListenerインターフェースのための実装
    //クリックリスナーの実体
    override fun onPreferenceClick(preference: Preference): Boolean {
        // 変更された設定項目 (preference) のkeyを取得
        val changedPreferenceKey = preference.key

//        when(changedPreferenceKey){
//
//        }

        return true
    }


    /* ******************
    フラグメント側
     ******************* */
    class SettingsFragment : PreferenceFragmentCompat() {

        //リスナーのホルダー
        private var prefChangeListener: Preference.OnPreferenceChangeListener? = null
//        private var prefClickListener:Preference.OnPreferenceClickListener? = null

        //親アクティビティのContextをチェック、各リスナーのインターフェイスを実装していればそれにキャストしてlistenerに代入
        override fun onAttach(context: Context) {
            super.onAttach(context)

            //OnPreferenceChangeListener
            if (context is Preference.OnPreferenceChangeListener) {
                prefChangeListener = context
            } else {
                throw RuntimeException("$context must implement PreferenceChangeListener")
            }

            //OnPreferenceClickListener
            if (context is Preference.OnPreferenceClickListener) {
                prefChangeListener = context
            } else {
                throw RuntimeException("$context must implement PreferenceClickListener")
            }

        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val darkModePref: Preference? = findPreference("set_dark_mode")
            val contactPref: Preference? = findPreference("contact")

            //Preferenceにリスナーを設定
            //特に再起動の設定をするものに
            //設定項目 "set_dark_mode" に チェンジリスナーを設定
            if (darkModePref != null) {
                darkModePref.onPreferenceChangeListener = prefChangeListener
            }

            /*
             *メール情報
             */
            val preference = preferenceManager.sharedPreferences
            if(preference != null){
                //本文構築
                var message:String =
                    "■お問い合わせ内容/Message\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n" +
                            "\r\n■user device: " +
                            "\r\ndevice_model: " + Build.MODEL +
                            "\r\ndevice_OS_ver: " + Build.VERSION.SDK_INT +
                            "\r\n■settings"

                val allEntries = preference.all.entries
                for (entry in allEntries) {
                    val key = entry.key
                    val value = entry.value
                    message += "\r\n$key: $value"
                }

                //intentにputExtraで情報追加
                if (contactPref != null) {
                    contactPref.intent?.putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf("claymanstudiodevelop@gmail.com")
                    )
                        ?.putExtra(Intent.EXTRA_SUBJECT, "【TableClocks】【お問い合わせ】")
                        ?.putExtra(Intent.EXTRA_TEXT, message)
                }
            }




        }


        override fun onResume() {
            super.onResume()


            //設定項目の有効化・無効化の制御
            //ダークモード：api29以上で有効化
            val darkModePref: Preference? = findPreference("set_dark_mode")
            if (darkModePref != null) {
                darkModePref.isEnabled = Build.VERSION.SDK_INT >= 29
                darkModePref.isVisible = Build.VERSION.SDK_INT >= 29
            }

        }



    }

}