package com.example.tableclocks

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.cardview.widget.CardView

class AspectRatioCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var aspectRatio = 2.0f // デフォルトの縦横比

    // 縦横比を設定するメソッド
    fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize: Int

        // 画面の向きによって縦横比を調整
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横向きの場合
            heightSize = (widthSize / aspectRatio).toInt()
        } else {
            // 縦向きの場合
            heightSize = (widthSize * aspectRatio).toInt()
        }

        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)

        //プレビューカードに縦横比をセット
        //todo: なんか微妙にズレているきがする？
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val aspectRatio = screenWidth.toFloat() / screenHeight
        setAspectRatio(aspectRatio)
    }
}
