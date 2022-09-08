package wee.digital.sample.ui.fragment.home

import wee.digital.sample.shared.Pref
import wee.digital.sample.ui.base.BaseVM

class HomeVM : BaseVM() {

    fun logoutUser() {
        Pref.user = null
    }
}