package com.friendzrandroid.home.fragment.whiteLabel

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.friendzrandroid.R
import com.friendzrandroid.databinding.ActivityCommunityBinding
import com.friendzrandroid.utils.SocialMediaLogin

@kotlinx.coroutines.ExperimentalCoroutinesApi
@dagger.hilt.android.AndroidEntryPoint
class CommunityActivity : AppCompatActivity() {
    lateinit var socialMediaLogin: SocialMediaLogin

    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socialMediaLogin = SocialMediaLogin(application, this)

//        val navController = findNavController(R.id.nav_host_fragment_content_community)


    }


}