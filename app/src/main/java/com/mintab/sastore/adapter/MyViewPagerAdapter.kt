package com.mintab.sastore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.mintab.sastore.R
import com.mintab.sastore.databinding.PostDetailsImageItemLayoutBinding
import com.mintab.sastore.model.ProductImagesModel

class MyViewPagerAdapter(val imagesModel:List<ProductImagesModel>) : PagerAdapter() {
    override fun getCount(): Int {
       return imagesModel.size
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var binding:PostDetailsImageItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater
            .from(container.context), R.layout.post_details_image_item_layout,container,false)
        binding.model = imagesModel[position]
        container.addView(binding.root)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}