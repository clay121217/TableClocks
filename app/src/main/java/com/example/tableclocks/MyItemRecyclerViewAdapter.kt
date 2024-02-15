package com.example.tableclocks

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.tableclocks.databinding.FragmentThemeGalleryBinding

import com.example.tableclocks.placeholder.PlaceholderContent.PlaceholderItem


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
/*
* todo:GPTに中身の実装アイデア聞いたのでそれ参照
* アイテムがクリックされたらpositionとクリックイベントを親アクティビティ(ここではtheme)知らせる方法
*
*
 */
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //リスト用画像
        holder.galleryItemBGView.setImageResource(
            context.resources.getIdentifier(
                values[position] + "_li",
                "drawable",
                context.packageName
            )
        )
        //タイトルセット
        holder.themeNameView.text = context.getString(
            context.resources.getIdentifier(
                values[position] + "_themeName_jp",
                "string",
                context.packageName
            )
        )
        holder.subThemeNameView.text = context.getString(
            context.resources.getIdentifier(
                values[position] + "_themeName_en",
                "string",
                context.packageName
            )
        )

        //クリックリスナー
        holder.galleryItemBGView.setOnClickListener {
            // アイテムがクリックされたときに、クリックイベントをアクティビティに通知
            itemClickListener.onItemClick(values[position])
        }
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentThemeGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val galleryItemBGView: ImageView = binding.galleryItemBG
        val themeNameView: TextView = binding.themeName
        val subThemeNameView: TextView = binding.subThemeName
    }

}