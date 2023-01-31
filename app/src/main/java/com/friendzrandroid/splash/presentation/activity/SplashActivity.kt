package com.friendzrandroid.splash.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.databinding.ActivitySplashBinding
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.home.fragment.whiteLabel.CommunityActivity
import com.jaeger.library.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        StatusBarUtil.setTransparent(this)


        lifecycleScope.launch {

            delay(800)
            val userLoggedIn = UserSessionManagement.isUserLoggedIn()
            Log.e("AuthToken", userLoggedIn.toString())

            if (!userLoggedIn) {

//                Log.e("AuthToken", UserSessionManagement.getKeyAuthToken()!!)
//                startActivity(Intent(this@SplashActivity, AuthActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                })

//                startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                })

                startActivity(Intent(this@SplashActivity, LandinPageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                })


            } else {

                Log.e("AuthToken", UserSessionManagement.getKeyAuthToken()!!)

                if (UserSessionManagement.userNeedToUpdate() == NeedToUpdateStatus.UPDATE_PROFILE.status) {


                    startActivity(
                        Intent(
                            this@SplashActivity,
                            IntroActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })


                } else if (UserSessionManagement.isWhiteLabel()) {
                    Log.e("AuthToken", UserSessionManagement.getKeyAuthToken()!!)


                    val whiteLabel = UserSessionManagement.isWhiteLabel()

                    startActivity(
                        Intent(
                            this@SplashActivity,
                            CommunityActivity::class.java
                        ).putExtra("isWhiteLabel", true).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })


                } else {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            MainActivity::class.java
                        ).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })

                }


            }

        }
    }

}