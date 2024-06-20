package io.github.clay121217.tableclocks

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.github.clay121217.tableclocks.databinding.FragmentThemeGalleryBinding

import java.util.Locale

class MyItemRecyclerViewAdapter(
    private val values: Array<String>,
    private val context: Context,
    private val itemClickListener: OnGalleryItemClickListener
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentThemeGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //カードアス比セット
        holder.galleryListItemCard.setAspectRatio(3.0f)

        //設定取得・整理
        val featureMonth = context.resources.getInteger(
            context.resources.getIdentifier(
                values[position] + "_feature",
                "integer",
                context.packageName
            )
        )

        val mainImgName = values[position] + "_m_" + featureMonth.toString().padStart(2, '0')
        val mainImgBGColor = values[position] + "_col_" + featureMonth.toString().padStart(2, '0')
        val coverImgName = values[position] + "_cover_li"

        //画像セット
        holder.galleryThemeImage.setImageResource(
            context.resources.getIdentifier(
                mainImgName,"drawable",context.packageName
            )
        )

        //背景色
        holder.galleryThemeBGColor.setBackgroundColor(
            ContextCompat.getColor(
                context,
                context.resources.getIdentifier(mainImgBGColor, "color", context.packageName)
            )
        )

        //カバーセット
        holder.galleryThemeCover.setImageResource(
            context.resources.getIdentifier(
                coverImgName,"drawable",context.packageName
            )
        )

        //タイトルセット
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
        holder.themeNameView.text = context.getString(
            context.resources.getIdentifier(
                values[position] + "_themeName_" + lang1,
                "string",
                context.packageName
            )
        )
        holder.subThemeNameView.text = context.getString(
            context.resources.getIdentifier(
                values[position] + "_themeName_" + lang2,
                "string",
                context.packageName
            )
        )

        //クリックリスナー
        holder.galleryListItemCard.setOnClickListener {
            // アイテムがクリックされたときに、クリックイベントをアクティビティに通知
            itemClickListener.onItemClick(values[position])
        }
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentThemeGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val galleryListItemCard: AspectRatioCardView = binding.galleryListItemCard
            val galleryThemeCover: ImageView = binding.galleryThemeCover
            val galleryThemeImage: ImageView = binding.galleryThemeImage
            val galleryThemeBGColor: View = binding.galleryThemeBGColor

//            val galleryItemBGView: ImageView = binding.galleryItemBG
            val themeNameView: TextView = binding.themeName
            val subThemeNameView: TextView = binding.subThemeName
    }
}