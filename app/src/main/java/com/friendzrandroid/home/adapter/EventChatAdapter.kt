package com.friendzrandroid.home.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.*
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.*
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.data.model.MessageData
import com.friendzrandroid.home.data.model.enum.MessageTypeEnum
import com.friendzrandroid.home.fragment.home.messages.chat.ChatViewModel
import com.friendzrandroid.utils.ImageDialog
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat


class EventChatAdapter(
    viewmodel: ChatViewModel,
    val activity: Activity,
    val listener: BaseAdapterListener<EventItemData>
) : PagingAdapter<MessageData>(viewmodel) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<MessageData> {

        when (viewType) {


            10,
            1 -> {// my message data


                val item = DataBindingUtil.inflate<ItemMyMessageBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_my_message,
                    parent,
                    false
                )



                return MyMessageViewHolder(parent, item)
            }
            else -> {// other message data
                val item2 = DataBindingUtil.inflate<ItemOtherMessageBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_other_message,
                    parent,
                    false
                )
                return OtherMessageViewHolder(parent, item2)
            }
        }


    }

    fun appendNewMessage(dataMessage: MessageData) {

        val newMessage = BasePagingModel<MessageData>()
        newMessage.type = DataType.DATA
        newMessage.data = dataMessage

        adapterList.add(0, newMessage)
        notifyDataSetChanged()
    }

    fun append(data: MessageData) {

        //Log.e("Chat", "append: $data")

        val newMessage = BasePagingModel<MessageData>()
        newMessage.type = DataType.DATA
        newMessage.data = data

        if (adapterList.size > 0) {
            adapterList.add(0, newMessage)
            notifyItemInserted(0)

        } else {
            //todo  adapterList clear after append
            adapterList.clear()
            adapterList.add(newMessage)
            notifyItemInserted(0)
        }

        //notifyDataSetChanged()
        //notifyItemRangeInserted(0, 1)
        //notifyItemInserted(0)
        //notifyItemRangeChanged(0, adapterList.size)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<MessageData>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(adapterList.get(position))


    }

    override fun getItemViewType(position: Int): Int {
        val item = adapterList.get(position)
        if (item.type == DataType.LOADING) {
            if (position % 2 == 0) {
                return 10
            } else {
                return 11
            }
        } else {
            if (item.data!!.currentuserMessage) {
                return 1 // my message
            } else
                return 2 // other message
        }
    }

    inner class MyMessageViewHolder(val parent: ViewGroup, val item: ItemMyMessageBinding) :
        BaseViewHolder<MessageData>(item) {
        override fun bind(pageData: BasePagingModel<MessageData>) {
            when (pageData.type) {
                DataType.LOADING -> {
                    item.shimmerLoading.startShimmer()
                }
                DataType.DATA -> {

                    val dateManipulation = if (pageData.data?.messagesdate != "Today")
                        getCurrentIsToday(pageData.data!!.messagesdate)
                    else
                        Pair(first = true, second = false)


                    item.shimmerLoading.hideShimmer()
                    item.messageImage.loadImage(pageData.data!!.userimage)

                    if (pageData.data!!.messagetype == MessageTypeEnum.IMAGE.value) {

                        val view = getImageMyView(parent, item, pageData, dateManipulation)
                        item.linearLayout.removeAllViews()
                        item.linearLayout.addView(view?.root)

                    } else if (pageData.data!!.messagetype == MessageTypeEnum.FILE.value) {
                        val view = getFileMyView(parent, item, pageData, dateManipulation)
                        item.linearLayout.removeAllViews()
                        item.linearLayout.addView(view?.root)


                    } else if (pageData.data!!.messagetype == MessageTypeEnum.SHARE.value) {

                        val view = getEventShareMyView(parent, item, pageData.data?.eventData!!)
                        item.linearLayout.removeAllViews()
                        item.linearLayout.addView(view?.root)

                    } else if (pageData.data!!.messagetype == MessageTypeEnum.MESSAGE.value) {
                        val view = getMessageMyView(parent, item, pageData, dateManipulation)
                        item.linearLayout.removeAllViews()
                        item.linearLayout.addView(view?.root)
                    }
                }
            }
        }
    }

    private fun getFileMyView(
        parent: ViewGroup,
        item: ItemMyMessageBinding,
        pageData: BasePagingModel<MessageData>,
        dateManipulation: Pair<Boolean, Boolean>
    ): ItemChatFileBinding {
        val itemFile = DataBindingUtil.inflate<ItemChatFileBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_chat_file,
            parent,
            false
        )


        val attached = pageData.data!!.messageAttachedVM.get(0).attached
        itemFile.apply {

            handleWebViewPDF(itemFile.pdfView,itemFile.progressBar, pageData.data!!.messageAttachedVM.get(0).attached)

            rlItemEventContainer.setOnClickListener {

                val x = 0
                Log.e("chat", "testPDF  on click: true ")
                Log.e("chat", "attach= $attached ")


                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/viewerng/viewer?embedded=true&url=" + attached)
                )
                itemFile.pdfView.context.startActivity(browserIntent)
            }


        }





        itemFile.pdfView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                val x = 0


                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("chat", "testPDF  on touch: true ")

                    return false
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {

//                    val browserIntent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse( attached)
//                    )
//
//
//                    itemFile.pdfView.context.startActivity(browserIntent)


                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(
                        Uri.parse("http://docs.google.com/viewer?url=$attached"),
                        "text/html"
                    )

                    itemFile.pdfView.context.startActivity(intent)

                    return false
                } else {
                    return false
                }
            }


        }
        )


        return itemFile
    }

    inner class OtherMessageViewHolder(val parent: ViewGroup, val item: ItemOtherMessageBinding) :
        BaseViewHolder<MessageData>(item) {
        override fun bind(pageData: BasePagingModel<MessageData>) {
            when (pageData.type) {
                DataType.LOADING -> {
                    item.shimmerLoading.startShimmer()
                }
                DataType.DATA -> {

                    val dateManipulation = getCurrentIsToday(pageData.data!!.messagesdate)


                    item.shimmerLoading.hideShimmer()
                    item.otherName.text = pageData.data!!.username
                    item.messageImage.loadImage(pageData.data!!.userimage)




                    if (pageData.data!!.messagetype == MessageTypeEnum.IMAGE.value) {

                        val view = getImageOtherView(parent, item, pageData, dateManipulation)
                        item.constraintLayout.removeAllViews()
                        item.constraintLayout.addView(view?.root)


                    } else if (pageData.data!!.messagetype == MessageTypeEnum.FILE.value) {

                        val view = getFileOtherView(parent, item, pageData, dateManipulation)


                        item.constraintLayout.removeAllViews()
                        item.constraintLayout.addView(view?.root)

                    } else if (pageData.data!!.messagetype == MessageTypeEnum.SHARE.value) {

                        val view = getEventShareOtherView(parent, item, pageData.data?.eventData!!)
                        item.constraintLayout.removeAllViews()

                        item.constraintLayout.addView(view?.root)

                    } else if (pageData.data!!.messagetype == MessageTypeEnum.MESSAGE.value) {

                        val view = getMessageOtherView(parent, item, pageData, dateManipulation)
                        item.constraintLayout.removeAllViews()
                        item.constraintLayout.addView(view?.root)

                    }
                }
            }
        }

    }

    private fun getFileOtherView(
        parent: ViewGroup,
        item: ItemOtherMessageBinding,
        pageData: BasePagingModel<MessageData>,
        dateManipulation: Pair<Boolean, Boolean>
    ): ItemChatFileBinding {

        val itemFile = DataBindingUtil.inflate<ItemChatFileBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_chat_file,
            parent,
            false
        )


        val attached = pageData.data!!.messageAttachedVM.get(0).attached
        itemFile.apply {

            handleWebViewPDF(itemFile.pdfView,itemFile.progressBar, pageData.data!!.messageAttachedVM.get(0).attached)

            rlItemEventContainer.setOnClickListener {

                val x = 0
                Log.e("chat", "testPDF  on click: true ")
                Log.e("chat", "attach= $attached ")


                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/viewerng/viewer?embedded=true&url=" + attached)
                )
                itemFile.pdfView.context.startActivity(browserIntent)
            }


        }





        itemFile.pdfView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View, event: MotionEvent): Boolean {
                val x = 0


                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("chat", "testPDF  on touch: true ")

                    return false
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {

//                    val browserIntent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse( attached)
//                    )
//
//
//                    itemFile.pdfView.context.startActivity(browserIntent)


                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(
                        Uri.parse("http://docs.google.com/viewer?url=$attached"),
                        "text/html"
                    )

                    itemFile.pdfView.context.startActivity(intent)

                    return false
                } else {
                    return false
                }
            }


        }
        )












        return itemFile
    }


    private fun getImageMyView(
        parent: ViewGroup,
        item: ItemMyMessageBinding,
        pageData: BasePagingModel<MessageData>,
        date: Pair<Boolean, Boolean>
    ): ItemEventShareChatBinding? {

//        val itemImage = DataBindingUtil.inflate<ItemMessageImageBinding>(
//            LayoutInflater.from(item.root.context),
//            R.layout.item_message_image,
//            parent,
//            false
//        )
        val itemImage = DataBindingUtil.inflate<ItemEventShareChatBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_event_share_chat,
            parent,
            false
        )


        itemImage.apply {
            eventImage.loadImage(pageData.data!!.messageAttachedVM.get(0).attached)
            if (!date.first && !date.second) {
                tvEventsAttendance.text =
                    "${pageData.data!!.messagesdate} ${pageData.data!!.messagestime}"
            } else if (date.first) {
                tvEventsAttendance.text =
                    "Today ${pageData.data!!.messagestime}"
            } else if (date.second) {
                tvEventsAttendance.text =
                    "Yesterday ${pageData.data!!.messagestime}"
            }

            eventImage.setOnClickListener {
                ImageDialog.setImageBigger(
                    activity,
                    pageData.data!!.messageAttachedVM.get(0).attached
                )
            }

        }




        return itemImage

    }


    private fun handleWebViewPDF(pdfView: WebView, progressPar:ProgressBar,attached: String) {

        pdfView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        pdfView.settings.javaScriptEnabled = true
        pdfView.settings.builtInZoomControls = true
        pdfView.settings.displayZoomControls = false
        pdfView.settings.useWideViewPort = true
        pdfView.settings.loadWithOverviewMode = true
        pdfView.settings.loadsImagesAutomatically = true
        pdfView.settings.domStorageEnabled = true
        pdfView.settings.setSupportZoom(false)

        pdfView.settings.setSupportMultipleWindows(true)



        pdfView.webViewClient = object : WebViewClient() {


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {


                view!!.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + attached);


                return true
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                                    if (progressPar.isVisible) {
                                        progressPar.visibility = View.GONE
                                    }

            }


            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(
                        activity,
                        "Error:" + error?.description.toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {

                    Toast.makeText(
                        activity,
                        "Error: Please try again!",
                        Toast.LENGTH_LONG
                    )
                }

            }


        }
        pdfView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + attached)


    }


    private fun getImageOtherView(
        parent: ViewGroup,
        item: ItemOtherMessageBinding,
        pageData: BasePagingModel<MessageData>,
        date: Pair<Boolean, Boolean>
    ): ItemEventShareChatBinding? {

//        val itemImage = DataBindingUtil.inflate<ItemMessageImageBinding>(
//            LayoutInflater.from(item.root.context),
//            R.layout.item_message_image,
//            parent,
//            false
//        )

        val itemImage = DataBindingUtil.inflate<ItemEventShareChatBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_event_share_chat,
            parent,
            false
        )


        /*  for (img in pageData.data!!.messageAttachedVM) {

              *//*   if (!date.first && !date.second) {
                   itemImage.imageMessageTime.text =
                       "${pageData.data!!.messagesdate} ${pageData.data!!.messagestime}"
               }else if (date.first) {
                       itemImage.imageMessageTime.text =
                           "Today ${pageData.data!!.messagestime}"
                   }else if (date.second) {
                   itemImage.imageMessageTime.text =
                       "Yesterday ${pageData.data!!.messagestime}"
               }

               itemImage.messageContainerImage.loadImage(img.attached)
               itemImage.messageContainerImage.setOnClickListener {
                   ImageDialog.setImageBigger(activity, img.attached)
               }*//*
            itemImage.imageContainer.show()
        }*/

        itemImage.apply {
            eventImage.loadImage(pageData.data!!.messageAttachedVM.get(0).attached)
            if (!date.first && !date.second) {
                tvEventsAttendance.text =
                    "${pageData.data!!.messagesdate} ${pageData.data!!.messagestime}"
            } else if (date.first) {
                tvEventsAttendance.text =
                    "Today ${pageData.data!!.messagestime}"
            } else if (date.second) {
                tvEventsAttendance.text =
                    "Yesterday ${pageData.data!!.messagestime}"
            }

            eventImage.setOnClickListener {
                ImageDialog.setImageBigger(
                    activity,
                    pageData.data!!.messageAttachedVM.get(0).attached
                )
            }

//            eventCategory.text = pageData.categorie
//            eventTitle.text = pageData.title
//            tvEventsEventDateAndTime.text =
//                "${pageData.eventdate}"
//            tvEventsAttendance.text =
//                "${root.resources.getString(R.string.title_attendance)} ${pageData.joined} / ${pageData.totalnumbert}"
//            itemImage.imageContainer.show()

        }

        return itemImage

    }


    private fun getMessageMyView(
        parent: ViewGroup,
        item: ItemMyMessageBinding,
        pageData: BasePagingModel<MessageData>,
        date: Pair<Boolean, Boolean>
    ): ItemMyMessageTextBinding? {

        val itemMessage = DataBindingUtil.inflate<ItemMyMessageTextBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_my_message_text,
            parent,
            false
        )

        itemMessage.messageContainer.show()

        itemMessage.messageContent.text = pageData.data!!.messages
        if (!date.first && !date.second) {
            itemMessage.messageTime.text =
                "${pageData.data!!.messagesdate} ${pageData.data!!.messagestime}"
        } else if (date.first) {
            itemMessage.messageTime.text =
                "Today ${pageData.data!!.messagestime}"
        } else if (date.second) {
            itemMessage.messageTime.text =
                "Yesterday ${pageData.data!!.messagestime}"
        }

        return itemMessage

    }

    private fun getMessageOtherView(
        parent: ViewGroup,
        item: ItemOtherMessageBinding,
        pageData: BasePagingModel<MessageData>,
        date: Pair<Boolean, Boolean>
    ): ItemOtherMessageTextBinding? {

        val itemMessage = DataBindingUtil.inflate<ItemOtherMessageTextBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_other_message_text,
            parent,
            false
        )

        itemMessage.messageContainer.show()
        itemMessage.messageContent.text = pageData.data!!.messages
        if (!date.first && !date.second) {
            itemMessage.messageTime.text =
                "${pageData.data!!.messagesdate} ${pageData.data!!.messagestime}"
        } else if (date.first) {
            itemMessage.messageTime.text =
                "Today ${pageData.data!!.messagestime}"
        } else if (date.second) {
            itemMessage.messageTime.text =
                "Yesterday ${pageData.data!!.messagestime}"
        }
        return itemMessage

    }

    private fun getEventShareOtherView(
        parent: ViewGroup,
        item: ItemOtherMessageBinding,
        pageData: EventItemData
    ): ItemEventShareChatBinding? {

        val itemEventShare = DataBindingUtil.inflate<ItemEventShareChatBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_event_share_chat,
            parent,
            false
        )

        //item.messageContainer.removeAllViews()
        //item.messageTime.hide()

        //item.messageContainerEvent.removeAllViews()

        itemEventShare.apply {
            eventImage.loadImage(pageData.image)
            eventCategory.text = pageData.categorie
            eventTitle.text = pageData.title
            tvEventsEventDateAndTime.text =
                "${pageData.eventdate}"
            tvEventsAttendance.text =
                "${root.resources.getString(R.string.title_attendance)} ${pageData.joined} / ${pageData.totalnumbert}"

        }

        itemEventShare.eventShareData.setOnClickListener {
            listener.onItemSelected(pageData)
        }

        return itemEventShare
    }

    private fun getEventShareMyView(
        parent: ViewGroup,
        item: ItemMyMessageBinding,
        pageData: EventItemData
    ): ItemEventShareChatBinding? {

        val itemEventShare = DataBindingUtil.inflate<ItemEventShareChatBinding>(
            LayoutInflater.from(item.root.context),
            R.layout.item_event_share_chat,
            parent,
            false
        )

        //item.messageContainer.removeAllViews()
        //item.messageTime.hide()

        //item.messageContainerEvent.removeAllViews()

        itemEventShare.apply {
            eventImage.loadImage(pageData.image)
            eventCategory.text = pageData.categorie
            eventTitle.text = pageData.title
            tvEventsEventDateAndTime.text =
                "${pageData.eventdate}"
            tvEventsAttendance.text =
                "${root.resources.getString(R.string.title_attendance)} ${pageData.joined} / ${pageData.totalnumbert}"

        }

        itemEventShare.eventShareData.setOnClickListener {
            listener.onItemSelected(pageData)
        }

        return itemEventShare
    }

    private fun getCurrentIsToday(date: String): Pair<Boolean, Boolean> {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = format.parse(date)

        var isToday = false
        var isYesterday = false
        currentDate?.let {
            isToday = DateUtils.isToday(it.time)
            isYesterday =
                DateUtils.isToday(currentDate.time + DateUtils.DAY_IN_MILLIS)
        }

        return Pair(isToday, isYesterday)
    }


    private fun display(
        _n: Int,
        renderer: PdfRenderer,
        imageview1: ShapeableImageView
    ) {


        val page: PdfRenderer.Page = renderer.openPage(_n)
        val mBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imageview1.setImageBitmap(mBitmap)
        page.close()
    }



}