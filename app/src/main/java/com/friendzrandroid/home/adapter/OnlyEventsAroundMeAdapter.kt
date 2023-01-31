package com.friendzrandroid.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import com.friendzrandroid.R
import com.friendzrandroid.core.paggingList.*
import com.friendzrandroid.core.utils.loadImage
import com.friendzrandroid.databinding.ItemMapEventAroundMeBinding
import com.friendzrandroid.home.adapter.listener.GoToEventDetailsListener
import com.friendzrandroid.home.data.model.EventItemData
import com.friendzrandroid.home.fragment.home.map.OnlyEventsAroundMeViewModel

class OnlyEventsAroundMeAdapter(
    viewModelOnly: OnlyEventsAroundMeViewModel,
    private val listener: BaseAdapterListener<EventItemData>,
    private val eventDetailsListenerListener: GoToEventDetailsListener<EventItemData>
) : PagingAdapter<EventItemData>(viewModelOnly) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<EventItemData> {
        val item = DataBindingUtil.inflate<ItemMapEventAroundMeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_map_event_around_me,
            parent,
            false
        )

        return EventViewHolder(item)
    }


    override fun onBindViewHolder(
        holder: BaseViewHolder<EventItemData>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)

        holder.bind(adapterList.get(position))



        adapterList.get(position).data?.let {

            holder.itemView.setOnClickListener {

                listener.onItemSelected(adapterList.get(position).data!!)
            }


        }
    }

    inner class EventViewHolder(val view: ItemMapEventAroundMeBinding) :
        BaseViewHolder<EventItemData>(view) {


        override fun bind(pageData: BasePagingModel<EventItemData>) {

            when (pageData.type) {
                DataType.LOADING -> {
                    view.shimmerLoading.startShimmer()

                }

                DataType.DATA -> {
                    view.shimmerLoading.hideShimmer()
                    view.imgEventImage.loadImage(pageData.data!!.image)
                    view.eventTitle.text = pageData.data!!.title
                    view.eventAttendeceValue.text =
                        "${pageData.data!!.joined} / ${pageData.data!!.totalnumbert}"
                    view.eventDateValue.text = pageData.data!!.eventdate

                    pageData.data?.eventtypecolor?.toColorInt()
                        ?.let { view.eventTypeColor.setBackgroundColor(it) }

                    view.expandContainer.setOnClickListener {
                        eventDetailsListenerListener.goToEventDetails(pageData.data!!)
                    }

                }

            }

        }
    }

}