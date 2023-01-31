package com.friendzrandroid.home.fragment.more.contactus

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friendzrandroid.R

class ContactusFragment : Fragment() {

    companion object {
        fun newInstance() = ContactusFragment()
    }

    private lateinit var viewModel: ContactusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contactus_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactusViewModel::class.java)
        // TODO: Use the ViewModel
    }

}