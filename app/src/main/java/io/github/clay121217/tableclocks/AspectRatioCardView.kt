package io.github.clay121217.tableclocks

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

    //画面縦横比をそのままセットするメソッド
    fun setByScreenAspectRatio(){
        //プレビューカードに縦横比をセット
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val aspectRatio = screenWidth.toFloat() / screenHeight

        this.aspectRatio = aspectRatio
        requestLayout()
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        // 画面の向きによって縦横比を調整
        val heightSize: Int
        = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横向きの場合
            (widthSize / aspectRatio).toInt()
        } else {
            // 縦向きの場合
            (widthSize * aspectRatio).toInt()
        }

        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }
}
