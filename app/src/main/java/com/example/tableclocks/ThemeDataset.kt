package com.example.tableclocks

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources

/**
 * 有効なテーマかのチェックは、Settingで選択するときに行う予定
 * 設定ボタンを出さない・設定メソッドでチェックの2回
 */
//テーマデータ格納クラス
class ThemeDataset(
    val context: Context, //コンテキスト、リソースにアクセスするのに使っているが、Fragmentから使える？
    val themeId:String, //テーマID
    var settedMonth: Int //現在の月
){
//    有効かチェック
    var isActivated:Boolean = false //有効なテーマか

    // 初期化
    init {
        // アプリ自体を有料・無料化するなら有料バージョンかチェック
        if("paid" == BuildConfig.FLAVOR){
            //有料版アプリは全てtrue
            isActivated = true
        }else{
            //無料版アプリ
            if(!context.resources.getBoolean(context.resources.getIdentifier(themeId+"_charge","Boolean",context.packageName))) {
                //無料スキンならばtrue
                isActivated = true
            }
        }
    }


    //テーマ名 日・英
    val themeNameJP = context.getString(context.resources.getIdentifier(themeId+"_themeName_jp","String",context.packageName)) //contextを使って強引に取得
    val themeNameEN = context.getString(context.resources.getIdentifier(themeId+"_themeName_en","String",context.packageName))

    //DrawingData：描画用データ
    var drawingData:DrawingData = generateDrawingData(context,themeId,settedMonth)


    //格納データの月を変更するメソッド
    fun changeMonth(month:Int){
        drawingData = generateDrawingData(context,themeId,month)
        settedMonth = month
    }
}

//描画用のデータ格納クラス
data class DrawingData(
    val color:Int,
    val mainImg:Drawable?,
    val coverId:Drawable?,
    val themeDrawingMode:String //今後全画面を実装するとき使う予定
)

//DrawingDataクラスの生成関数
fun generateDrawingData(context: Context,themeId:String, month:Int):DrawingData{
    //入力を整理
    val monthStr = month.toString().padStart(2,'0')

    //DrawingDataを構築して返す
    return DrawingData(
        //カラー
        context.getColor(context.resources.getIdentifier(
            themeId+"_col_"+monthStr,"color",context.packageName)),
        //メイン画像
        AppCompatResources.getDrawable(context,context.resources.getIdentifier(
            themeId+"_m_"+monthStr,"drawable", context.packageName)),
        //カバー
        AppCompatResources.getDrawable(context,context.resources.getIdentifier(
            themeId+"_cover","drawable", context.packageName)),
        //描画モード
        context.getString(context.resources.getIdentifier(
            themeId+"_mode","string",context.packageName))
    )
}