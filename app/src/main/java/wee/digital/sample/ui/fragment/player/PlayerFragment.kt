package wee.digital.sample.ui.fragment.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Path
import android.graphics.RectF
import android.media.MediaPlayer
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.Observer
import wee.digital.library.extension.thumbnail
import wee.digital.library.extension.toString
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.databinding.PlayerBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class PlayerFragment : MainFragment<PlayerBinding>() {

    private val playerVM by lazyActivityVM(PlayerVM::class)

    private var mediaPlayer: MediaPlayer? = null

    val trackAdapter = CommentAdapter()
    var commentList: MutableList<CommentItem>? = null


    override fun inflating(): Inflating {
        return PlayerBinding::inflate
    }

    override fun onViewCreated() {
        // addClickListener(vb.ibtnPlay,vb.ibtnPrev,vb.itbnStop,vb.ibtnNext)
        val animation1 = AnimationUtils.loadAnimation(requireActivity(), R.anim.disc_rotate)

        playerVM.itemSelected.observe(lifecycleOwner, Observer {
            val thumbnail = it.thumbnail
            vb.textViewTitle.text = it.name
            vb.textViewTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            vb.textViewTitle.isSelected = true
            vb.textViewTitle.isSingleLine = true
            vb.backgroundPlayer.setImageBitmap(thumbnail)
            vb.imgHinh.setImageBitmap(thumbnail)

            // accessing the songs on storage
            val uri = Uri.parse(it.toString())
            mediaPlayer = MediaPlayer.create(context, uri)

        })


        vb.ibtnPlay.setOnClickListener(View.OnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                vb.ibtnPlay.setImageResource(R.drawable.play_icon)

            } else {
                mediaPlayer!!.start()
                vb.ibtnPlay.setImageResource(R.drawable.pause_icon)

            }
            val timeDuration = mediaPlayer!!.duration.toLong()
            animation1.duration = timeDuration
            vb.imgHinh.startAnimation(animation1)

        })
        vb.includeSwipeBottom.heartImage.setOnClickListener {
            heartOnClick()
        }

        val commentList : List<CommentItem> = emptyList()

        trackAdapter.bind(vb.recyclerViewListComment) {
            stackFromEnd = true
            reverseLayout = false
        }
            .set(commentList)
            .onItemClick {
                // onItemClick(it)
            }

        vb.recyclerViewListComment.scrollToPosition(trackAdapter.itemCount - 1)
        vb.includeSwipeBottom.textmessage.setEndIconOnClickListener {
            val comment = vb.includeSwipeBottom.edtSend.text.toString().trim()
            val c = Calendar.getInstance()
            val dateformat = SimpleDateFormat("hh:mm:ss aa")
            val datetime = dateformat.format(c.time)
            Log.d("AAA", "datetime: ${datetime}")
            println(datetime)
            trackAdapter.add(trackAdapter.itemCount, CommentItem("Nguyen Van A", datetime, comment))
            Log.d("AAA", "commentlist: ${trackAdapter.itemCount}")
            trackAdapter.notifyItemInserted(trackAdapter.itemCount)
            vb.recyclerViewListComment.scrollToPosition(trackAdapter.itemCount - 1)
        }
    }

    private fun heartOnClick() {
        // Disable clips on all parent generations
        disableAllParentsClip(vb.includeSwipeBottom.heartImage)

        // Create clone
        val imageClone = cloneImage()

        // Animate
        animateFlying(imageClone)
        animateFading(imageClone)
    }

    private fun cloneImage(): ImageView {
        val clone = ImageView(context)
        clone.layoutParams = vb.includeSwipeBottom.heartImage.layoutParams
        clone.setImageDrawable(vb.includeSwipeBottom.heartImage.drawable)
        vb.includeSwipeBottom.cloneContainer.addView(clone)
        return clone
    }

    private fun animateFlying(image: ImageView) {
        val x = 0f
        val y = 0f
        val r = Random.nextInt(1000, 5000)
        val angle = 25f

        val path = Path().apply {
            when (r % 2) {
                0 -> arcTo(RectF(x, y - r, x + 2 * r, y + r), 180f, angle)
                else -> arcTo(RectF(x - 2 * r, y - r, x, y + r), 0f, -angle)
            }
        }

        ObjectAnimator.ofFloat(image, View.X, View.Y, path).apply {
            duration = 1000
            start()
        }
    }

    private fun animateFading(image: ImageView) {
        image.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    vb.includeSwipeBottom.cloneContainer.removeView(image)
                }
            })
    }

    private fun disableAllParentsClip(view: View) {
        var view = view
        view.parent?.let {
            while (view.parent is ViewGroup) {
                val viewGroup = view.parent as ViewGroup
                viewGroup.clipChildren = false
                viewGroup.clipToPadding = false
                view = viewGroup
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playerVM.uri?.also {
            //play music by uri
        }
    }

    override fun onPause() {
        super.onPause()
        playerVM.uri?.also {
            //pause music on pause
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            vb.ibtnPlay -> {
                toast("play")
            }
//            vb.ibtnPrev->{
//                toast("top")
//            }
        }
    }

}