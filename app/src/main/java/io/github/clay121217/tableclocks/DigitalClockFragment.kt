package io.github.clay121217.tableclocks

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.clay121217.tableclocks.databinding.FragmentDigitalClockBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DigitalClockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DigitalClockFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    private var _binding: FragmentDigitalClockBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDigitalClockBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // キューの最後に予約されて、描画後に実行されるらしい
        // こうしないと描画前のサイズを取りに行って0,0がでてきてしまいサイズが取得できない
        binding.digitalClocksContent.post {
            clockSizeSet()
        }
    }

    private fun clockSizeSet() {
        /* 時計を適切なサイズにセットする */
        val n =  binding.digitalClocksContent.width * 0.45
        binding.textviewTimes.width = n.toInt()
        binding.textviewTimes.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 2).toFloat())
        binding.textViewSec.width = (n / 2).toInt()
        binding.textViewSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 4).toFloat())
        binding.textViewDays.setTextSize(TypedValue.COMPLEX_UNIT_PX, (n / 5).toFloat())

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DigitalClockFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DigitalClockFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView(){

        super.onDestroyView()
        _binding = null

    }
}