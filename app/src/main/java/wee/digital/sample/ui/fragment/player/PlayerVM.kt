package wee.digital.sample.ui.fragment.player

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.ui.base.BaseVM
import java.io.File


class PlayerVM: BaseVM(){

    var uri: String? = null

    val itemSelected =MutableLiveData<File>()



}