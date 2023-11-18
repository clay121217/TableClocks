package com.example.tableclocks

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.tableclocks.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //キューの最後に予約されて、描画後に実行されるらしい、こうしないとサイズが取得できない
        binding.clocksContent.post{
            clockSizeSet()
        }

        //時計ここから
        _handler?.post(_runnable)

        //設定画面ボタン
        val settingsBtn = binding.settingsBtn
        settingsBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(application, ThemeGalleryActivity::class.java)
            startActivity(intent)
        })
        binding.settingsBtn.setImageResource(R.drawable.baseline_settings_24)


        //開発者モード
        if("debug" == BuildConfig.BUILD_TYPE ){
            themeTestButton()
        }
    }


    //todo:時計の位置について、再考したい
    //todo:もうすこし大きくしたいので、秒を下に置けるように
    //todo:最大幅を決めて自動伸縮できるようにしたいけど。。。
    private fun clockSizeSet(){
        /* 時計を適切なサイズにセットする */
        val n = binding.clocksContent.width *0.4
        binding.textviewTimes.width = n.toInt()
        binding.textviewTimes.setTextSize(TypedValue.COMPLEX_UNIT_PX,(n/2).toFloat())
        binding.textViewSec.width = (n/2).toInt()
        binding.textViewSec.setTextSize(TypedValue.COMPLEX_UNIT_PX,(n/4).toFloat())
        binding.textViewDays.setTextSize(TypedValue.COMPLEX_UNIT_PX,(n/5).toFloat())
    }

//    日付のセット
    private var _handler = Looper.myLooper()?.let { Handler(it) }
    private var _runnable = object : Runnable{
        override fun run() {
            //時間の取得・整形
            _handler?.postDelayed(this, 1000.toLong())
            val dataFormat = SimpleDateFormat("yyyy/MM/dd (EEE),HH:mm,ss") //ちょっと古いらしいが、代替の新しいやつは、API26~なのでこれでいいか
            val date = Date()
            val s = dataFormat.format(date).split(",")

            //初回と月替りで画像セット
            if(binding.textViewDays.text != s[0]){
                val month = s[0].split("/")
                /* あとでフェードアウト・インを入れる？初回だけフェードアウト入れないようにしたい */
                themeImageSet( month[1])
            }

            //時計セット
            binding.textViewDays.text = s[0]
            binding.textviewTimes.text = s[1]
            binding.textViewSec.text = s[2]


        }
    }
    //時計ここまで


    //テーマ画像セット
    private fun themeImageSet(month:String){
        //設定読み込み
        val pref = getSharedPreferences("com.TableClocks.settings",Context.MODE_PRIVATE)
        val themeName = pref.getString("themeName","flowers")//第二引数が初期値

        //データ整形 jpseasons_m_01のようにする
        val mainImgName = themeName+"_m_"+month.padStart(2,'0')
        val mainImgBGColor = themeName+"_col_"+month.padStart(2,'0')
        val coverImgName = themeName+"_cover"

        //メインイメージセット
        binding.mainOverImage.setImageResource(resources.getIdentifier(mainImgName, "drawable", packageName))        //getIdentifierを使う方法、Stringが使えるので引き出しやすそう
        //背景色セット
        binding.mainBG.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(mainImgBGColor, "color", packageName)))//◀半分 キーカラー
        //カバー画像セット
        binding.mainImageCover.setImageDrawable(ResourcesCompat.getDrawable(resources , resources.getIdentifier(coverImgName, "drawable", packageName) , null ))


    }

    //時計・テーマのフェードイン
    //フェードインは、背景色がついてメイン画像が外から入ってくる
    //todo:フェードインの前、おそらく画像を差し替えたタイミングでAlphaがデフォルト値？にもどってしまう
    //todo:ここで解決するより、ThemeDrawingFragmentで実装しなおすときに解決したい、隠す用の黒いフレームを挟むか、フラグメントごとフェードするでもいいかも
    //todo:プレビュー画面でカバーがフェード前後に変わる可能性があるので、カバーも隠したい
    private fun fadInMain(){
        // アルファ値を1.0fから0.0fへ変化させるフェードアウトアニメーション
        val fadeAnim = AlphaAnimation(0.0f, 1.0f)
        // 5秒間(5000ミリ秒)かけて行う
        fadeAnim.duration = 800
        // アルファ値をアニメーション終了後の値を維持する
        fadeAnim.fillAfter = true

        binding.mainOverImage.animation = fadeAnim
        binding.mainBG.animation = fadeAnim

    }

    //時計・テーマの切り替えフェードアウト
    //フェードアウトは、背景色が黒に・メイン画像が外に抜けるイメージ

    private fun fadOutMain(){
        // アルファ値を1.0fから0.0fへ変化させるフェードアウトアニメーション
        val fadeAnim = AlphaAnimation(1.0f, 0.0f)
        // 5秒間(5000ミリ秒)かけて行う
        fadeAnim.duration = 800
        // アルファ値をアニメーション終了後の値を維持する
        fadeAnim.fillAfter = true

        binding.mainOverImage.animation = fadeAnim
        binding.mainBG.animation = fadeAnim
    }

    //テスト用ボタン
    //月を切り替えられるように
    private fun themeTestButton(){
        //ボタン実装
        val themeTestButton = Button(this)
        themeTestButton.text = "表示テスト"
        themeTestButton.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        val linearLayout = findViewById<ConstraintLayout>(R.id.wrap)
        linearLayout.addView(themeTestButton)

        var month = 1
        //リスナー
        themeTestButton.setOnClickListener {
            fadOutMain()
            var monthStr = month.toString().padStart(2,'0')
            _handler?.postDelayed( {
                themeImageSet(monthStr)
                fadInMain()
            }, 900)

            themeTestButton.text = month.toString()+"月"

            Toast.makeText(this, "変更しました$month", Toast.LENGTH_SHORT).show()
            if(month < 12){
                month += 1
            }else{
                month = 1
            }

        }
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