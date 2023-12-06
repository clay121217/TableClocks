package com.example.tableclocks

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceFragmentCompat
import com.example.tableclocks.databinding.ActivityThemeGalleryBinding


class ThemeGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemeGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        //フラグメントの生成
        //todo 設定を取得できるようにしたい、フィーチャー月を取ってくるのもやる
        val fragment = ThemeDrawingFragment.newInstance("jpseasons", 2)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.themeFragmentContainer, fragment)
        transaction.commit()

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

//    val pref = getSharedPreferences("com.TableClocks.settings", Context.MODE_PRIVATE)
//    val themeName = pref.getString("themeName","jpseasons")

    //todo リストのクリックに反応してプレビューを変更したい
    //todo 数秒ごと？にランダムで月変更？とにかく複数月を見られるように


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