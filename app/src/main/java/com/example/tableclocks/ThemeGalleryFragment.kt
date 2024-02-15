package com.example.tableclocks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tableclocks.placeholder.PlaceholderContent


/**
 * A fragment representing a list of Items.
 */
class ThemeGalleryFragment : Fragment() {

    private var columnCount = 1
    private var listener: OnGalleryItemClickListener? = null

    override fun onAttach(context: Context) {

        //親アクティビティのContextをチェック、OnGalleryItemClickListenerインターフェイスを実装していればキャストしてlistenerに代入
        super.onAttach(context)
        if (context is OnGalleryItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FragmentListener")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_theme_gallery_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val themeNameList: Array<String> = resources.getStringArray(R.array.theme_id_arr)
                adapter = MyItemRecyclerViewAdapter(themeNameList, context, listener!!)
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ThemeGalleryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}