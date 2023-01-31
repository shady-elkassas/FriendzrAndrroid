package com.friendzrandroid.utils

import android.app.Activity
import android.view.View
import android.widget.PopupMenu

class MenuUtil(val activity: Activity, val view: View, val menu: Int) {

    private var popMenu: PopupMenu = PopupMenu(activity, view)

    fun showMenu(listener: (Int) -> Boolean) {
        popMenu.menuInflater.inflate(menu, popMenu.menu)

        popMenu.setOnMenuItemClickListener { listener(it.itemId) }
        popMenu.show()
    }
}