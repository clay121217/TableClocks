package com.example.tableclocks

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
            val intent = Intent(application, SettingsActivity::class.java)
            startActivity(intent)
        })


        //開発者モード
        getSharedPreferences("clock_settings", Context.MODE_PRIVATE).edit().apply {
            putBoolean("DEV_MODE", true)
            apply()
        }
        themeTestButton()

    }

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
        val pref = getSharedPreferences("com.tableClocks.settings",Context.MODE_PRIVATE)
        val themeName = pref.getString("themeName","jpseasons")

        //データ整形
        val mainImgName = "m_"+themeName+"_"+month.padStart(2,'0')  //m_jpseasons_01のようにする

        //メインイメージセット
        binding.mainOverImage.setImageResource(resources.getIdentifier(mainImgName, "drawable", packageName))        //getIdentifierを使う方法、Stringが使えるので引き出しやすそう
        //時計背景セット
        val drawableBG = ResourcesCompat.getDrawable(resources , R.drawable.mbg_simple , null )
        //drawableBG?.setTint(Color.parseColor("#000000")) //▶半分
        binding.mainBGImage.setImageDrawable(drawableBG)
        binding.wrap.setBackgroundColor(Color.parseColor("#634ecc"))//◀半分 キーカラー

        binding.settingsBtn.setImageResource(R.drawable.baseline_settings_24)

    }

    //時計・テーマのフェードイン
    //フェードインは、背景色がついてメイン画像が外から入ってくる

    //時計・テーマの切り替えフェードアウト
    //フェードアウトは、背景色が黒に・メイン画像が外に抜けるイメージ



    //テスト用ボタン
    //月を切り替えられるように
    private fun themeTestButton(){
        val pref = getSharedPreferences("clock_settings",Context.MODE_PRIVATE)
        if(pref.getBoolean("DEV_MODE",false)){
            //ボタン実装
            val themeTestButton = Button(this)
            themeTestButton.text = "次の月へ"
            themeTestButton.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

            val linearLayout = findViewById<ConstraintLayout>(R.id.wrap)
            linearLayout.addView(themeTestButton)

            var month = 1
            //リスナー
            themeTestButton.setOnClickListener {
                var monthStr = month.toString().padStart(2,'0')
                themeImageSet(monthStr)
                Toast.makeText(this, "変更しました$month", Toast.LENGTH_SHORT).show()
                if(month < 12){
                    month += 1
                }else{
                    month = 1
                }


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