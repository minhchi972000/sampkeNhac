package wee.digital.sample.ui.fragment.home

import android.graphics.Bitmap
import android.graphics.Color
import androidx.viewbinding.ViewBinding
import wee.digital.library.extension.thumbnail
import wee.digital.sample.R
import wee.digital.sample.databinding.HomeItemHorizontalBinding
import wee.digital.sample.databinding.HomeItemVerticalBinding
import wee.digital.widget.adapter.BaseRecyclerAdapter
import wee.digital.widget.adapter.BaseSelectableAdapter
import wee.digital.widget.adapter.InflaterInvokerBinding
import wee.digital.widget.adapter.ItemOptions
import java.io.File

class ArtistAdapter :  BaseSelectableAdapter<String,HomeItemHorizontalBinding>(){

    override fun itemInflating(): InflaterInvokerBinding<HomeItemHorizontalBinding> {
        return HomeItemHorizontalBinding::inflate
    }

    override fun HomeItemHorizontalBinding.onBindDefaultItem(item: String, position: Int) {
        titleName.text =item
    }

    override fun HomeItemHorizontalBinding.onBindSelectedItem(item: String, position: Int) {
        layoutBackground.setBackgroundResource(R.drawable.bg_item)
        titleName.setTextColor(Color.WHITE)
    }

    override fun HomeItemHorizontalBinding.onBindUnselectedItem(item: String, position: Int) {
        layoutBackground.setBackgroundResource(0)
        titleName.setTextColor(Color.BLACK)
    }
}