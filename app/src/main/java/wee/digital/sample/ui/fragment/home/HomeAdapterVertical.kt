package wee.digital.sample.ui.fragment.home

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import wee.digital.library.extension.thumbnail
import wee.digital.sample.R
import wee.digital.sample.databinding.HomeItemVerticalBinding
import wee.digital.widget.adapter.BaseRecyclerAdapter
import wee.digital.widget.adapter.ItemOptions
import java.io.File

class HomeAdapterVertical :  BaseRecyclerAdapter<MediaItem>(){
    override fun modelItemOptions(item: MediaItem?, position: Int): ItemOptions? {
        return ItemOptions(R.layout.home_item_vertical, HomeItemVerticalBinding::bind)
    }

    override fun ViewBinding.onBindItem(item: MediaItem, position: Int) {
        if (this !is HomeItemVerticalBinding) return
        titleName.text =item.file?.name?.substringAfterLast("/")
        titleName.ellipsize = TextUtils.TruncateAt.MARQUEE
        titleName.isSelected = true
        titleName.isSingleLine = true
        val image : Bitmap? = item.file?.thumbnail
        imageViewPhoto.setImageBitmap(image)

    }
}