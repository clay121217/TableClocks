package com.example.tableclocks

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.tableclocks.databinding.FragmentThemeDrawingBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val THEME_NAME = "theme_name"
private const val MONTH = "month"

/**
 * A simple [Fragment] subclass.
 * Use the [ThemeDrawingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/*
 *  TODO:なんらかの引数としてDrawingDataを受け取って描画
 *  RecyclarViewAdapterから呼び出されたlistitemのフラグメントからでも受け取れる方法
 *
 */

class ThemeDrawingFragment : Fragment() {
    //引数的なものでDrawingDataをとりたい
    // TODO: Rename and change types of parameters
    private var themeName: String? = null
    private var month: Int? = null

    private var _binding: FragmentThemeDrawingBinding? = null
    private val binding get() = _binding!!

//    private var _handler = Looper.myLooper()?.let { Handler(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            themeName = it.getString(THEME_NAME)
            month = it.getInt(MONTH)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThemeDrawingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




//        生成時テーマ描画

        //設定読み込み　何らかの方法でPreferenceを取ってくる？これってActyvity側でやることかも
//        val pref = getSharedPreferences("com.TableClocks.settings",Context.MODE_PRIVATE)
//        themeName = pref.getString("themeName","flowers")//第二引数が初期値
//        todo:デフォルトは設定から取ってきたいができるか不明

        //        アニメーターのセット
        val fadeAnimator = ObjectAnimator.ofFloat(binding.overBlack, View.ALPHA, 1f, 0f)
        fadeAnimator.duration = 1500

        //描画
        themeImageSet(themeName ?: "jpseasons", month ?: 1)

        //フェードイン
        fadeAnimator.start()


    }

//    DrawingDataを引数に描画をする

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param themeName Parameter 1.
         * @param month Parameter 2.
         * @return A new instance of fragment ThemeDrawingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(themeName: String, month: Int) =
            ThemeDrawingFragment().apply {
                arguments = Bundle().apply {
                    putString(THEME_NAME, themeName)
                    putInt(MONTH, month)
                }
            }
    }

    //テーマ画像セット
    //生で呼び出すのは生成時だけの予定
    //他の場合はフェードをつけたthemeImageChange()で対応したい
    private fun themeImageSet(newThemeName: String = "keep", newMonth: Int = -1) {

        //引数があるときはメンバ変数を置き換える
        if (newThemeName !== "keep") {
            themeName = newThemeName
        }
        if (newMonth != -1) {
            month = newMonth
        }

        //データ整形 jpseasons_m_01のようにする
        val mainImgName = themeName + "_m_" + month.toString().padStart(2, '0')
        val mainImgBGColor = themeName + "_col_" + month.toString().padStart(2, '0')
        val coverImgName = themeName + "_cover"

        //メインイメージセット
        binding.drawThemeImage.setImageResource(
            resources.getIdentifier(
                mainImgName,
                "drawable",
                requireContext().packageName
            )
        )        //getIdentifierを使う方法、Stringが使えるので引き出しやすそう

        //背景色セット
        binding.drawThemeBGColor.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                resources.getIdentifier(mainImgBGColor, "color", context?.packageName)
            )
        )

        //カバー画像セット　テーマ引数があるときだけ
        if(newThemeName !== "keep"){
            binding.drawThemeCover.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    resources.getIdentifier(coverImgName, "drawable", context?.packageName),
                    null
                )
            )
        }
    }

    //テーマや月の変更、フェード付き
    fun themeImageChange(newThemeName: String = "keep", newMonth: Int = -1){
        //アニメーションセット
        val fadeAnimator = ObjectAnimator.ofFloat(binding.overBlack, View.ALPHA, 1f, 0f)
        fadeAnimator.duration = 800

        //変更開始
        fadeAnimator.reverse()      //フェードアウト

        Handler(Looper.getMainLooper()).postDelayed( {    //遅延実行
            themeImageSet(newThemeName, newMonth) //画像セット
            fadeAnimator.start()    //フェードイン
        }, 1200)

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null //ビューバインディングをちゃんと破棄する

    }


}