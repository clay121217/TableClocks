package io.github.clay121217.tableclocks

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import io.github.clay121217.tableclocks.databinding.ActivityThemeGalleryBinding
import java.util.Locale

//todo: プレビューを色々表示させる処理をつけるかどうか検討
//todo: Freeとかそういうのも表示検討 Pアイコンとか
//todo: 現在のテーマを何処かでわかるように。画面の何処かに書いておくか、リストアイテムにマークするとか、選択ボタンがグレーアウトするとか
//todo: 有料テーマ実装時に、有料選択したらボタンを無効化するように。設定処理とボタンアクティベートで二重フィルター

class ThemeGalleryActivity : AppCompatActivity(), OnGalleryItemClickListener {
    private lateinit var binding: ActivityThemeGalleryBinding

    private var userThemeName :String = "jp_seasons" //ユーザーが設定しているテーマ名のホルダー
    private var previewThemeName :String = "jp_seasons" //プレビュー選択中のテーマ名のホルダー

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //設定取得
        val sharedPreferences = getDefaultSharedPreferences(this)
        val userTheme = sharedPreferences.getString("userTheme", "jp_seasons")!!
        userThemeName = userTheme //ついでにセット
        previewThemeName = userTheme //初期化

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
            onBackPressedDispatcher.onBackPressed()
        }



        //テーマ名をプレビューにセット
        setPreviewText()

        //プレビューフラグメントの生成
        val fragment = ThemeDrawingFragment.newInstance(
            userTheme,
            resources.getInteger(
                resources.getIdentifier(
                    userTheme + "_feature",
                    "integer",
                    packageName
                )
            )
        )
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.themeFragmentContainer, fragment)
        transaction.commit()

        //アスペクト比のセット
        binding.previewCard.setByScreenAspectRatio()

        //アニメーターのセット
        val fadeAnimator = ObjectAnimator.ofFloat(binding.previewThemeName, View.ALPHA, 0f, 1f)
        fadeAnimator.duration = 700
        val fadeAnimator2 = ObjectAnimator.ofFloat(binding.previewSubThemeName, View.ALPHA, 0f, 1f)
        fadeAnimator2.duration = 700

        fadeAnimator.start()
        fadeAnimator2.start()

        //テーマセットボタン
        binding.themeSetBtn.setOnClickListener {
            sharedPreferences.edit().putString("userTheme",previewThemeName).apply()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.previewSubThemeName.setOnClickListener {
            it.isSelected = true
        }

    }

    @SuppressLint("DiscouragedApi")
    private fun setPreviewText(){


        //アニメーターのセット
        val fadeAnimator = ObjectAnimator.ofFloat(binding.previewThemeName, View.ALPHA, 0f, 1f)
        fadeAnimator.duration = 400
        val fadeAnimator2 = ObjectAnimator.ofFloat(binding.previewSubThemeName, View.ALPHA, 0f, 1f)
        fadeAnimator2.duration = 400

        //フェードアウト
        fadeAnimator.reverse()
        fadeAnimator2.reverse()

        //テーマ名を取得、日英で逆に
        val locale = Locale.getDefault()
        val lang = locale.language

        val lang1: String
        val lang2: String
        if (lang == "ja"){
            lang1 = "jp"
            lang2 = "en"
        }else{
            lang1 = "en"
            lang2 = "jp"
        }

        val previewThemeName1 = resources.getString(resources.getIdentifier(previewThemeName+"_themeName_"+lang1,"string",packageName))
        val previewThemeName2 = resources.getString(resources.getIdentifier(previewThemeName+"_themeName_"+lang2,"string",packageName))

        Handler(Looper.getMainLooper()).postDelayed({
            //テーマ名をプレビューにセット
            binding.previewThemeName.text = previewThemeName1
            binding.previewSubThemeName.text = previewThemeName2

            //フェードイン
            fadeAnimator.start()
            fadeAnimator2.start()
        },600)

    }

    //RecyclerViewのアイテムがクリックされたときのコールバック
    @SuppressLint("DiscouragedApi")
    override fun onItemClick(themeName: String) {

        //連打防止
        if (!isDelayClickEvent()) return

        //選択テーマをtextViewとメンバ変数にセット
        previewThemeName = themeName
        setPreviewText()

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

    //todo 数秒ごと？にランダムで月変更？とにかく複数月を見られるようにする？

    //連続でのイベント防止
    private val delay: Long = 1000  // １秒未満のイベントは無視
    private var mOldTime: Long = 0  // 前回イベント実施時刻

    private fun isDelayClickEvent(): Boolean = isDelayClickEvent(delay)
    @Suppress("SameParameterValue")
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
        // API 31以上の場合
        // API 30から切り替えるべきだが「戻る」で帰ってきたあと挙動が違う、31から平気っぽい
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.decorView.windowInsetsController?.apply {
                // systemBars : Status barとNavigation bar両方
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            }

            // APIがそれ以下の場合
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        //ノッチ侵略用 API 28以上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attrib = window.attributes
            attrib.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
}