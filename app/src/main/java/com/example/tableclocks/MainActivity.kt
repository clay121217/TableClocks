package com.example.tableclocks

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.tableclocks.BuildConfig
import com.example.tableclocks.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

//todo 設定用のActivityを作らないと？設定項目を考えないと・・・タイムゾーンとかお問い合わせとかバージョン情報とか
//todo Amazonに出せるようにするかも？ビルドバリアント作るのかな

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //設定の取得、正常/freeのチェック、じゃなければjpseasonsにもどす？
        val sharedPreferences = getSharedPreferences("userSettings", Context.MODE_PRIVATE)
        var userTheme = sharedPreferences.getString("userTheme", "jpseasons")!!

        //テーマが有効かチェック
        val isActive:Boolean = isActiveTheme(userTheme)
        //無効なテーマならデフォルトテーマに書き換え
        //todo 設定まで書き換えに行く？
        if(!isActive){
            userTheme = "jpseasons"
        }

        //フラグメントの生成
        val calendar = Calendar.getInstance()        //月取得
        val month = calendar.get(Calendar.MONTH) + 1 //0オリジンなため+1

        val fragment = ThemeDrawingFragment.newInstance( userTheme , month)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.themeFragmentContainer, fragment)
        transaction.commit()

        // キューの最後に予約されて、描画後に実行されるらしい
        // こうしないと描画前のサイズを取りに行って0,0がでてきてしまいサイズが取得できない
        binding.clocksContent.post {
            clockSizeSet()
        }

        //時計ここから
        _handler?.post(_runnable)

        //設定画面ボタン
        //todo 設定画面ができたら差し替え
        val settingsBtn = binding.settingsBtn
        settingsBtn.setOnClickListener {
            val intent = Intent(application, ThemeGalleryActivity::class.java)
            startActivity(intent)
        }
        binding.settingsBtn.setImageResource(R.drawable.baseline_settings_24)

        //テーマギャラリーボタン
        val themeGalleryBtn = binding.themeGalleryBtn
        themeGalleryBtn.setOnClickListener {
            val intent = Intent(application, ThemeGalleryActivity::class.java)
            startActivity(intent)
        }
        binding.themeGalleryBtn.setImageResource(R.drawable.baseline_brush_24)

        //開発者モード
        if ("debug" == BuildConfig.BUILD_TYPE) {
            themeTestButton()
        }
    }

    private fun clockSizeSet() {
        /* 時計を適切なサイズにセットする */
        val n = binding.clocksContent.width * 0.45
        binding.textviewTimes.width = n.toInt()
        binding.textviewTimes.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 2).toFloat())
        binding.textViewSec.width = (n / 2).toInt()
        binding.textViewSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 4).toFloat())
        binding.textViewDays.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 5).toFloat())

    }

    //    日付のセット
    private var _handler = Looper.myLooper()?.let { Handler(it) }
    private var _runnable = object : Runnable {
        override fun run() {
            //時間の取得・整形
            _handler?.postDelayed(this, 1000.toLong())
            val dataFormat =
                SimpleDateFormat("yyyy/MM/dd (EEE),HH:mm,ss") //ちょっと古いらしいが、代替の新しいやつは、API26~なのでこれでいいか
            val date = Date()
            val s = dataFormat.format(date).split(",")

            //初回と月替りで画像セット
//            if(binding.textViewDays.text != s[0]){
//                val month = s[0].split("/")
//                /* あとでフェードアウト・インを入れる？初回だけフェードアウト入れないようにしたい */
//                themeImageSet( month[1])
//            }

            //時計セット
            binding.textViewDays.text = s[0]
            binding.textviewTimes.text = s[1]
            binding.textViewSec.text = s[2]
        }
    }
    //時計ここまで

    //テーマの有効化チェック
    private fun isActiveTheme(themeName:String):Boolean{
        //有料・無料チェック
        if ("paid" == BuildConfig.FLAVOR) {
            //有料版は素通り
            return true
        }else{
//            //無料版は[theme]_is_freeを取得する
            try {
                // リソースIDを取得
                val resourceId = resources.getIdentifier(themeName + "_is_free", "bool", packageName)

                // リソースが存在し、かつそのリソースが `true` であれば有効
                return if (resourceId != 0) {
                    resources.getBoolean(resourceId)
                } else {
                    // リソースが見つからない場合も false として扱う
                    false
                }
            } catch (e: Resources.NotFoundException) {
                // リソースが見つからない場合も false として扱う
                return false
            }
        }
    }

    //テスト用ボタン
    //月を切り替えられるように
    private fun themeTestButton() {
        //ボタン実装
        val themeTestButton = Button(this)
        themeTestButton.text = "表示テスト"
        themeTestButton.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val linearLayout = findViewById<ConstraintLayout>(R.id.wrap)
        linearLayout.addView(themeTestButton)


        var month = 1        //月カウンター

        //リスナー
        themeTestButton.setOnClickListener {
            //Fragmentを保持
            val fragmentHolder: Fragment? =
                supportFragmentManager.findFragmentById(R.id.themeFragmentContainer)
            if (fragmentHolder != null && fragmentHolder is ThemeDrawingFragment) {
                //月変更実行
                fragmentHolder.themeImageChange(newMonth = month)
            }

            themeTestButton.text = month.toString() + "月"

            //トースト
            Toast.makeText(this, "変更しました$month", Toast.LENGTH_SHORT).show()

            //月カウンターを進める
            if (month < 12) {
                month += 1
            } else {
                month = 1
            }

        }
    }


    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()

        //設定再確認
        val sharedPreferences = getSharedPreferences("userSettings", Context.MODE_PRIVATE)
        var userTheme = sharedPreferences.getString("userTheme", "jpseasons")!!

        //テーマ設定を反映
        //Fragmentを保持
        val fragmentHolder: Fragment? =
            supportFragmentManager.findFragmentById(R.id.themeFragmentContainer)
        if (fragmentHolder != null && fragmentHolder is ThemeDrawingFragment) {
            val calendar = Calendar.getInstance()        //月取得
            val month = calendar.get(Calendar.MONTH) + 1 //0オリジンなため+1

            //テーマ変更実行
            fragmentHolder.themeImageChange( userTheme , month )
        }

        //ステータスバーとナビゲーションバー消す
        // API 30以上の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.apply {
                // systemBars : Status barとNavigation bar両方
                hide(WindowInsets.Type.systemBars())
                // hide(WindowInsets.Type.statusBars())
                // hide(WindowInsets.Type.navigationBars())
                systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            }

            //ノッチ侵略用
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val attrib = window.attributes
                attrib.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

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