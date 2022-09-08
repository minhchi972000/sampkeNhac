package wee.digital.sample.ui.fragment.player

import android.util.Log
import androidx.viewbinding.ViewBinding
import com.google.android.material.animation.AnimationUtils
import wee.digital.library.extension.connectionInfo
import wee.digital.sample.R
import wee.digital.sample.app
import wee.digital.sample.databinding.PlayerItemListCommentBinding
import wee.digital.widget.adapter.BaseRecyclerAdapter
import wee.digital.widget.adapter.ItemOptions
import wee.digital.widget.extension.anim


class CommentAdapter :  BaseRecyclerAdapter<CommentItem>(){

    override fun modelItemOptions(item: CommentItem?, position: Int): ItemOptions? {
        return ItemOptions(R.layout.player_item_list_comment,PlayerItemListCommentBinding::bind)
    }


    override fun ViewBinding.onBindItem(item: CommentItem, position: Int) {
        if (this !is PlayerItemListCommentBinding) return
        imageViewPhoto.setImageResource(R.drawable.ic_action_fav)
        titleName.text = item.name
       // cardView.startAnimation(anim(R.anim.anim_one))

        comment.text = item.comment
        playerTime.text = "${item.duration}"

    }



}