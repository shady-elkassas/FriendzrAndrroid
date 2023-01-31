package com.friendzrandroid.home

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.StatsLog.logEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.callback.FilePickerCallback
import com.anggrayudi.storage.callback.StorageAccessCallback
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.getStorageId
import com.appsflyer.AppsFlyerLib
import com.friendzrandroid.R
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.presentation.ui.BaseActivity
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.LocationUtils
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.core.utils.hide
import com.friendzrandroid.core.utils.show
import com.friendzrandroid.databinding.ActivityMainBinding
import com.friendzrandroid.home.data.model.DataState
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.home.dialog.tutorialDialog.TutorialDialog
import com.friendzrandroid.home.fragment.home.messages.chat.ChatFragment
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxFragment
import com.friendzrandroid.home.fragment.more.editProfile.EditProfileFragment
import com.friendzrandroid.splash.adapters.IntroSliderAdapter
import com.friendzrandroid.splash.presentation.fragment.Intro1PeopleFragment
import com.friendzrandroid.splash.presentation.fragment.Intro2Fragment
import com.friendzrandroid.splash.presentation.fragment.Intro3Fragment
import com.friendzrandroid.splash.presentation.fragment.Intro5Fragment
import com.friendzrandroid.utils.HelperClass.onBackPressed
import com.friendzrandroid.utils.SocialMediaLogin
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaeger.library.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val REQUEST_CODE_STORAGE_ACCESS: Int = 201
    lateinit var socialMediaLogin: SocialMediaLogin
    private val fragmentList = ArrayList<Fragment>()

    private lateinit var receiver: BroadcastReceiver

    public var appsflyer: AppsFlyerLib? = null


    val viewModel: MainViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var locationUtils: LocationUtils

    lateinit var navView: BottomNavigationView

    companion object {
        var isActive: Boolean = false
    }

    private val storage = SimpleStorage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(binding.root)
        StatusBarUtil.setColor(this, Color.BLACK)
        locationUtils = LocationUtils(this, viewModel)
        setContentView(binding.root)
        locationUtils.locationStatus()

        socialMediaLogin = SocialMediaLogin(application, this)
//        FilePicker.init(activityResultRegistry, this, contentResolver)

        catchIntentExtraAndViewHideDesign()

        broadCastReceiver()

        val token = UserSessionManagement.getKeyAuthToken()
        if (token != null) {

            updateCurrentBadges()

        }

        if (UserSessionManagement.isWhiteLabel()) {
            navView = binding.navView
            navView.hide()

        }
//        setupSimpleStorage()
//        storage.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS)


        appsflyer = AppsFlyerLib.getInstance()

    }


    override fun onSaveInstanceState(outState: Bundle) {
        storage.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        storage.onRestoreInstanceState(savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Mandatory for Activity, but not for Fragment & ComponentActivity


//        storage.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun updateCurrentBadges() {
        viewModel.currentUserBadges.observe(this) {
            when (it) {
                is DataState.NewData -> {
                    UserSessionManagement.updateInboxNumber(it.data.message_Count)
                    UserSessionManagement.updateFriendRequestNumber(it.data.frindRequestNumber)
                    UserSessionManagement.updateNotificationNumber(it.data.notificationcount)

                    showInboxBadge(it.data.message_Count)
                    showFriendRequestBadge(it.data.frindRequestNumber)


                }
                else -> {}
            }
        }


    }

    private fun catchIntentExtraAndViewHideDesign() {


        if (intent != null) {

            if (intent.getIntExtra(
                    "needToUpdate",
                    -1
                ) == NeedToUpdateStatus.UPDATE_PROFILE.status
            ) {

                binding.container.hide()
                binding.includeIntroActivity.sVIntroLayout.show()

                inflateIntroAdapter()
            } else if (intent.getBooleanExtra("isWhiteLabel", false)) {
                binding.container.hide()


                inflateVavController()
                navView = binding.navView
                navView.hide()


//                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//                val inflater = navHostFragment.navController.navInflater
//                val graph = inflater.inflate(R.navigation.main_navigation)
//
//                if (isTrue){
//                    graph.startDestination = R.id.DetailsFragment
//                }else {
//                    graph.startDestinationId = R.layout.inbox_fragment
//                }
//
//                val navController = navHostFragment.navController
//                navController.setGraph(graph, intent.extras)
//
////                loadFragment(InboxFragment())


            }
//            else if (intent.getStringExtra("directionName") != null) {
//
//                inflateVavController()
//                val directionName = intent.getStringExtra("directionName")
//
//                if (directionName.equals("directionalFiltering")) {
//                    val directionId = intent.getStringExtra("directionId")
//                    if (directionId != null) {
//                        goToFragment(
//                            "directionId",
//                            directionId,
//                            com.friendzrandroid.R.id.navigation_feed
//                        )
//                    }
//
//                }
//
//
//
//            }
            else {


                inflateVavController()


            }


        } else {


            inflateVavController()


        }
    }

    private fun goToFragment(
        argumentIdKey: String,
        argumentIdValue: String,
        fragment: Int
    ) {
        val navController = findNavController(
            R.id.nav_host_fragment_activity_main
        )
//                    navController.navigateUp()

        val bundle = Bundle()
        bundle.putString(argumentIdKey, argumentIdValue)
        navController.navigate(fragment, bundle)
    }


    private fun inflateVavController() {

        binding.container.show()
        binding.includeIntroActivity.sVIntroLayout.hide()

        navView = binding.navView
        val navController =
            findNavController(com.friendzrandroid.R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        if (!UserSessionManagement.isWhiteLabel()) {
            hideBottomNavigation(navController, binding.navView)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //showBadge(UserSessionManagement.getRequestsNumber())
        showFriendRequestBadge(UserSessionManagement.getRequestsNumber())
        showInboxBadge(UserSessionManagement.getInboxNumber())


//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
//            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
//        }

    }

    var currentBadgeRequestNumber = 0
    fun showFriendRequestBadge(numberToShow: Int) {

        navView.getOrCreateBadge(com.friendzrandroid.R.id.communityGL).isVisible =
            (numberToShow > 0)
        currentBadgeRequestNumber = numberToShow
        navView.getOrCreateBadge(com.friendzrandroid.R.id.communityGL).apply {
            maxCharacterCount = 3
            number = numberToShow
            setVerticalOffset(20)

        }
    }

    var currentBadgeInboxNumber = 0
    fun showInboxBadge(numberToShow: Int) {
        navView.getOrCreateBadge(com.friendzrandroid.R.id.navigation_Inbox).isVisible =
            (numberToShow > 0)
        currentBadgeInboxNumber = numberToShow
        navView.getOrCreateBadge(com.friendzrandroid.R.id.navigation_Inbox).apply {
            maxCharacterCount = 3
            number = numberToShow
            setVerticalOffset(20)

        }
    }


    private fun hideBottomNavigation(navController: NavController, navView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                com.friendzrandroid.R.id.navigation_feed -> navView.visibility = View.VISIBLE
                com.friendzrandroid.R.id.profileFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.userProfileFragment -> navView.visibility = View.GONE

                com.friendzrandroid.R.id.editProfileFragment -> {
                    navView.visibility = View.GONE
                }

                com.friendzrandroid.R.id.detailsGroupChatFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.eventsFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.addEventFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.eventDetailsFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.editEventFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.notificationFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.settingsFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.navigation_Chat -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.reportFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.createGroupFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.eventShareFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.eventAttendenceFragment -> navView.visibility = View.GONE
                com.friendzrandroid.R.id.navigation_requests -> navView.visibility = View.GONE


                com.friendzrandroid.R.id.navigation_Inbox -> {
                    checkIfNoTokenToClosePrivatePages(navView)

                }

                com.friendzrandroid.R.id.communityGL -> {
                    checkIfNoTokenToClosePrivatePages(navView)
                }
                com.friendzrandroid.R.id.navigation_more -> {
                    checkIfNoTokenToClosePrivatePages(navView)
                }


                else -> {
                    navView.visibility = View.VISIBLE
                }
            }


        }
    }

    private fun checkIfNoTokenToClosePrivatePages(navView: BottomNavigationView) {
        val token = UserSessionManagement.getKeyAuthToken()
        if (token == null) {

            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        } else {
            navView.visibility = View.VISIBLE

        }
    }

    override fun onResume() {
        super.onResume()
        locationUtils.subScribeReceiver()

        if (UserSessionManagement.getKeyAuthToken().equals("")) {
            println("")
        } else {

            viewModel.currentLocation.observe(this) {
                viewModel.updateLocation(LatLng(it.latitude, it.longitude))
            }
        }



        isActive = true

//        viewModel.getRequests()
//        viewModel.currentRequests.observe(this) {
//            showBadge(it)
//        }
    }

    override fun onPause() {
        super.onPause()
        locationUtils.UnRegisterReceiver()
        isActive = false
    }


    private fun inflateIntroAdapter() {

        StatusBarUtil.setTransparent(this)
        val adapter = IntroSliderAdapter(this)
        binding.includeIntroActivity.vpIntroSlider.adapter = adapter

        fragmentList.addAll(
            listOf(
                Intro1PeopleFragment(), Intro2Fragment(), Intro3Fragment(), Intro5Fragment()
            )
        )
        adapter.setFragmentList(fragmentList)

        binding.includeIntroActivity.indicatorLayout.setIndicatorCount(adapter.itemCount)
        binding.includeIntroActivity.indicatorLayout.selectCurrentPosition(0)
        registerListeners()
    }


    private fun registerListeners() {
        binding.includeIntroActivity.vpIntroSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                binding.includeIntroActivity.indicatorLayout.selectCurrentPosition(position)

                if (position == 0) {
                    binding.includeIntroActivity.btnIntroNext.text =
                        getString(com.friendzrandroid.R.string.getStarted)
                } else {
                    binding.includeIntroActivity.btnIntroNext.text =
                        getString(com.friendzrandroid.R.string.next)
                }
            }
        })



        binding.includeIntroActivity.btnIntroNext.setOnClickListener {
            val position = binding.includeIntroActivity.vpIntroSlider.currentItem

            if (position < fragmentList.lastIndex) {
                binding.includeIntroActivity.vpIntroSlider.currentItem = position + 1
            } else {

//                startActivity(Intent(this, AuthActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                })


                loadFragment(EditProfileFragment())


            }
        }
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(com.friendzrandroid.R.id.RlMainMainContainer, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }


    private fun broadCastReceiver() {

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                intent.extras?.let {
                    when (it.getInt("update")) {
                        1 -> showFriendRequestBadge(UserSessionManagement.getRequestsNumber())
                        2 -> showInboxBadge(UserSessionManagement.getInboxNumber())
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("update-badge"))

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

/*
    else if (intent.getStringExtra("directionName") != null) {

        inflateVavController()
        val directionName = intent.getStringExtra("directionName")
        if (directionName.equals("eventDetails")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "eventID",
                    directionId,
                    com.friendzrandroid.R.id.eventDetailsFragment
                )
            }

        } else if (directionName.equals("createEvent")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "directionId",
                    directionId,
                    com.friendzrandroid.R.id.navigation_map
                )
            }

        } else if (directionName.equals("eventFilter")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "directionId",
                    directionId,
                    com.friendzrandroid.R.id.navigation_map
                )
            }

        } else if (directionName.equals("feed")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "directionId",
                    directionId,
                    com.friendzrandroid.R.id.navigation_feed
                )
            }

        } else if (directionName.equals("directionalFiltering")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "directionId",
                    directionId,
                    com.friendzrandroid.R.id.navigation_feed
                )
            }

        } else if (directionName.equals("interests")) {
            val directionId = intent.getStringExtra("directionId")
            if (directionId != null) {
                goToFragment(
                    "directionId",
                    directionId,
                    com.friendzrandroid.R.id.editProfileFragment
                )
            }

        } else if (directionName.equals("editProfile")) {

            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.editProfileFragment
                )
            }

        } else if (directionName.equals("personalSpace")) {
            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.settingsFragment
                )
            }

        } else if (directionName.equals("ageFilter")) {
            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.settingsFragment
                )
            }

        } else if (directionName.equals("privateMode")) {
            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.settingsFragment
                )
            }

        } else if (directionName.equals("distanceFilter")) {
            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.settingsFragment
                )
            }

        } else if (directionName.equals("eventFilter")) {
            val eventId = intent.getStringExtra("directionId")
            if (eventId != null) {
                goToFragment(
                    "directionId",
                    eventId,
                    com.friendzrandroid.R.id.navigation_map
                )
            }

        }


    }
*/

}