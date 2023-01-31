package com.friendzrandroid.home.dialog.eventsDialog

import com.friendzrandroid.home.data.model.EventMapData
import com.friendzrandroid.home.data.model.EventMapResponseItem

interface onEventSelected {
    fun EventData(dialog: EventsDialog,data: EventMapData)
}