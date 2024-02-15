package com.example.tableclocks

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceFragmentCompat
import com.example.tableclocks.databinding.ActivityThemeGalleryBinding
import java.util.Locale

//todo: 設定変更ボタンを実装する
//todo: プレビューを色々表示させる処理をつけるかどうか検討
//todo: サンプルだとかプレビューの表示を整理する、表示いらないかも
//todo: Freeとかそういうのも表示検討

class ThemeGalleryActivity : AppCompatActivity(), OnGalleryItemClickListener {
    private lateinit var binding: ActivityThemeGalleryBinding

    private var userThemeName :String = "jpseasons" //ユーザーが設定しているテーマ
    private var previewThemeName :String = "jpseasons" //プレビュー選択中のテーマ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ギャラリーリストのフラグメント生成
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.themeSettingsFrame, ThemeGalleryFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //戻るボタン
        binding.backButton.setOnClickListener {
            val intent = Intent(application, MainActivity::class.java)
            startActivity(intent)
        }


        //設定取得
        //todo 設定を取得できるようにしたい
        val sharedPreferences = getSharedPreferences("userSettings", Context.MODE_PRIVATE)
        val themeName = sharedPreferences.getString("userTheme", "jpseasons")!!
        userThemeName = themeName //ついでにセット

        //テーマ名をプレビューにセット


        //プレビューフラグメントの生成
        val fragment = ThemeDrawingFragment.newInstance(
            themeName,
            resources.getInteger(
                resources.getIdentifier(
                    themeName + "_feature",
                    "integer",
                    packageName
                )
            )
        )
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.themeFragmentContainer, fragment)
        transaction.commit()

    }

    fun setPreviewText(themeName: String){
        var locale = Locale.getDefault()
        var lang = locale.getDisplayLanguage()

        //テーマ名をプレビューにセット
        binding.previewThemeName.text =
            resources.getString(resources.getIdentifier(themeName+"_themeName_jp","string",packageName))
        binding.previewSubThemeName.text =
            resources.getString(resources.getIdentifier(themeName+"_themeName_en","string",packageName))
    }

    //RecyclerViewのアイテムがクリックされたときのコールバック
    override fun onItemClick(themeName: String) {

        //連打防止
        if (!isDelayClickEvent()) return

        //メンバ変数にセット
        previewThemeName = themeName

        //todo: テーマをテーマ設定変更ボタンに渡す？ 押したときにpreviewThemeName参照するでも可、先にボタンを実装する




        //Fragmentを保持
        val fragmentHolder: Fragment? =
            supportFragmentManager.findFragmentById(R.id.themeFragmentContainer)
        if (fragmentHolder != null && fragmentHolder is ThemeDrawingFragment) {

            //テーマ変更実行、フィーチャー月にセット
            fragmentHolder.themeImageChange(
                newThemeName = themeName,
                newMonth = resources.getInteger(
                    resources.getIdentifier(
                        themeName + "_feature",
                        "integer",
                        packageName
                    )
                )
            )

        }


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

//    val pref = getSharedPreferences("com.TableClocks.settings", Context.MODE_PRIVATE)
//    val themeName = pref.getString("themeName","jpseasons")

    //todo 数秒ごと？にランダムで月変更？とにかく複数月を見られるように


    //連続でのイベント防止
    private val DELAY: Long = 1000  // １秒未満のイベントは無視
    private var mOldTime: Long = 0  // 前回イベント実施時刻

    private fun isDelayClickEvent(): Boolean = isDelayClickEvent(DELAY)
    private fun isDelayClickEvent(delay: Long): Boolean {
        // 今の時間を覚える
        val time = System.currentTimeMillis()

        // 前回の時間と比較してdelayミリ秒以上経っていないと無視
        if (time - mOldTime < delay) {
            return false
        }

        // 一定時間経過したら実行可能とする
        mOldTime = time
        return true
    }


    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()

        //ステータスバーとナビゲーションバー消す
        // API 30以上の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.apply {
                // systemBars : Status barとNavigation bar両方
                hide(WindowInsets.Type.systemBars())
                // hide(WindowInsets.Type.statusBars())
                // hide(WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            }

//            //ノッチ侵略用
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val attrib = window.attributes
//                attrib.layoutInDisplayCutoutMode =
//                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//            }

            // API 29以下の場合
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
}