package com.friendzrandroid.splash.presentation.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.ActivityIntroBinding
import com.friendzrandroid.home.fragment.home.more.MoreViewModel
import com.friendzrandroid.home.fragment.more.editProfile.EditProfileFragment
import com.friendzrandroid.splash.adapters.IntroSliderAdapter
import com.friendzrandroid.splash.presentation.fragment.*
import com.jaeger.library.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    private val moreViewModel: MoreViewModel by viewModels()

    private val fragmentList = ArrayList<Fragment>()

    private val isFromHelp: Boolean by lazy {
        intent.extras?.getBoolean("isFromHelp") ?: false
    }

    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        inflateIntroAdapter()

        registerListeners()
    }

    private fun inflateIntroAdapter() {

        StatusBarUtil.setTransparent(this)
        val adapter = IntroSliderAdapter(this)
        binding.vpIntroSlider.adapter = adapter
        binding.vpIntroSlider.setUserInputEnabled(false);


        fragmentList.addAll(
            listOf(
                Intro1PeopleFragment(),
                Intro2Fragment(),
                Intro3Fragment(),
                Intro4Fragment(),
                Intro5Fragment(),
                Intro6Fragment(),
                Intro7Fragment()
            )
        )
        adapter.setFragmentList(fragmentList)

        binding.indicatorLayout.setIndicatorCount(adapter.itemCount)
        binding.indicatorLayout.selectCurrentPosition(0)


    }


    private fun registerListeners() {
        binding.vpIntroSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                binding.indicatorLayout.selectCurrentPosition(position)



                if (position == 1 && isFromHelp) {
                    binding.btnIntroExit.show()
                }
                if (position == 0 && !isFromHelp) {
                    binding.btnIntroNext.text = getString(R.string.getStarted)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.btnIntroExit.text = Html.fromHtml(
                            "<font color='#0BBEA1'>In a hurry? Skip to <u>profile</u></font>",
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        binding.btnIntroExit.text =
                            (Html.fromHtml("<font color='#0BBEA1'>In a hurry? Skip to <u>profile</u></font>"));
                    }
                    binding.btnIntroExit.show()

                } else if (position != fragmentList.lastIndex) {

                    binding.btnIntroNext.text = getString(R.string.next)
                } else if (position == fragmentList.lastIndex && isFromHelp) {
                    binding.btnIntroNext.text = getString(R.string.title_exit)
                    binding.btnIntroExit.hide()
                } else if (position == fragmentList.lastIndex && !isFromHelp) {
                    binding.btnIntroNext.text = getString(R.string.title_gotoProfile)
                    binding.btnIntroExit.hide()
                }


            }

        })



        binding.btnIntroNext.setOnClickListener {
            val position = binding.vpIntroSlider.currentItem

            if (position < fragmentList.lastIndex) {

                binding.vpIntroSlider.currentItem = position + 1

            } else if (!isFromHelp) {
                loadFragment(EditProfileFragment())
            } else
                finish()
        }

        binding.btnIntroExit.setOnClickListener {
            if (!isFromHelp) {

                binding.vpIntroSlider.currentItem = fragmentList.lastIndex
                loadFragment(EditProfileFragment())


                countClickCall("SkipTutorial")
            } else

                finish()
        }


    }


    private fun countClickCall(clickedScreen: String) {
        moreViewModel.sendLinkClick(clickedScreen)

    }

    private fun loadFragment(fragment: Fragment) {

        binding.flMainMainContainer.show()
        binding.vpIntroSlider.hide()
        binding.indicatorLayout.hide()
        binding.btnIntroNext.hide()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMainMainContainer, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}