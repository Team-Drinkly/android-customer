package com.project.drinkly.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.databinding.FragmentMenuFullScreenBinding

class MenuFullscreenFragment(
    private val imageUrls: List<String>,
    private val startPosition: Int = 0
) : Fragment() {

    private var _binding: FragmentMenuFullScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuFullScreenBinding.inflate(inflater, container, false)

        binding.run {
            toolbar.run {
                textViewHead.text = arguments?.getString("storeName") ?: ""
                buttonClose.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = object : RecyclerView.Adapter<ImageViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                return ImageViewHolder(imageView)
            }

            override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                Glide.with(holder.imageView.context)
                    .load(imageUrls[position])
                    .into(holder.imageView)
            }

            override fun getItemCount(): Int = imageUrls.size
        }

        binding.viewPagerFullScreen.adapter = adapter
        binding.viewPagerFullScreen.setCurrentItem(startPosition, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
