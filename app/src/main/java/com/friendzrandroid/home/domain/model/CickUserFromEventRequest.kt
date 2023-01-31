package com.friendzrandroid.home.domain.model

import com.friendzrandroid.home.data.model.enum.CickUserFromEventEnum

class CickUserFromEventRequest(val eventID:String,val AttendUserID:String,val status: CickUserFromEventEnum)