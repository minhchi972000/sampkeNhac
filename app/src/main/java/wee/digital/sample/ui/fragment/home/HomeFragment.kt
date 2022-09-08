package wee.digital.sample.ui.fragment.home

import android.Manifest
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import wee.digital.library.extension.*
import wee.digital.sample.R
import wee.digital.sample.R.anim
import wee.digital.sample.app
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.fragment.player.PlayerVM
import wee.digital.sample.ui.main.MainFragment
import java.io.File


class HomeFragment : MainFragment<HomeBinding>() {

    val adapterList = HomeAdapterVertical()
    val artistAdapter = ArtistAdapter()
    var mediaList: MutableList<MediaItem>? = null

    private val playerVM by lazyActivityVM(PlayerVM::class)

    override fun inflating(): Inflating {
        return HomeBinding::inflate
    }


    override fun onViewCreated() {
        onGrantedPermission(Manifest.permission.READ_EXTERNAL_STORAGE, onGranted = {
            getMediaData()
        }, onDenied = {

        })

        bottomNavigation()
    }


    private fun getMediaData() {

        // Đọc file từ Download
        val fileDir = File(Environment.getExternalStorageDirectory(), "Download")
        val file = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val artistList = mutableListOf("All")

        val rs: Cursor? = app.contentResolver
            .query(file, null, selection, null, null)
        mediaList = mutableListOf()
        if (rs != null) {
            while (rs.moveToNext()) {
                val fileName = rs.getString(rs.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val artist = rs.getString(rs.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val path = rs.getString(rs.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH))
                if (path == "Download/" && !artistList.contains(artist)) {
                    artistList.add(artist)
                }
                val file = File(fileDir, fileName)
                if (file.exists()) {
                    val mediaItem = MediaItem()
                    mediaItem.file = file
                    if (artist == "<unknown>") {
                        mediaItem.artist = "Unknown"
                    } else {
                        mediaItem.artist = artist
                    }
                    mediaList?.add(mediaItem)
                }

            }
        }


        adapterList.bind(vb.recyclerViewList)
            .set(mediaList)
            .onItemClick {
                onItemClick(it)
            }

        artistAdapter.bind(vb.recyclerViewArtist) {
            this.orientation = LinearLayoutManager.HORIZONTAL
        }
            .set(artistList)
        artistAdapter.select("All")

        artistList.add("Unknown")
        artistAdapter.onSelectionChanged { artist ->
            when (artist) {
                "All" -> adapterList.set(mediaList)
                else -> {
                    val filterList = mediaList?.filter { mediaItem -> mediaItem.artist == artist }
                    adapterList.set(filterList)
                }
            }
        }
    }

    private fun onItemClick(it: MediaItem) {

        vb.line1.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                anim.slide_up
            )
        )

        playerVM.itemSelected.value = it.file
        mainNavigate(R.id.action_global_playerFragment)


    }

    private fun bottomNavigation() {

        val item1 = AHBottomNavigationItem(R.string.home, R.drawable.ic_home, R.color.blue_x)
        val item2 =
            AHBottomNavigationItem(R.string.favorite, R.drawable.ic_action_fav, R.color.blue_x)

        vb.bottomNavigation.addItem(item1)
        vb.bottomNavigation.addItem(item2)

        // Use colored navigation with circle reveal effect
        vb.bottomNavigation.isColored = true
    }

}