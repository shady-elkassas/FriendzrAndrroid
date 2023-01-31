package com.friendzrandroid.home.fragment.whiteLabel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.friendzrandroid.R
import com.friendzrandroid.core.presentation.ui.BaseActivity
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.databinding.ActivityMainBinding
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxFragment


@kotlinx.coroutines.ExperimentalCoroutinesApi
@dagger.hilt.android.AndroidEntryPoint
class WhiteLabelActivityActivity : BaseActivity() {

    private val viewModel2: MainViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_white_label_activity)


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(com.friendzrandroid.R.id.RlMainMainContainer, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }



}