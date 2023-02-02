package com.friendzrandroid.auth.presentation.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.friendzrandroid.R
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.core.presentation.ui.BaseActivity
import com.friendzrandroid.core.presentation.viewmodel.BaseViewModel
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.data.model.enum.NeedToUpdateStatus
import com.friendzrandroid.splash.presentation.activity.LandinPageActivity
import com.friendzrandroid.utils.SocialMediaLogin
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.jaeger.library.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.json.JSONException
import org.json.JSONObject
import java.util.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AuthActivity : BaseActivity() {


    var socialMediaUserId: String? = null
    var faceBookFirst_name: String? = null
    var faceBookLast_name: String? = null
    var faceBookGender: String? = null
    var faceBookName: String? = null
    var faceBookBirthday: String? = null
    var socialMediaUserEmail: String? = null
    var faceBookPhone: String? = null

    var viewModel: AuthViewModel? = null
    var isLogin: Boolean = false
    val validation = application
    var mGoogleSignInClient: GoogleSignInClient? = null


    lateinit var socialMediaLogin: SocialMediaLogin

    private var callbackManager: CallbackManager? = null

    private val viewModel2: MainViewModel by viewModels()

    override val baseViewModel: BaseViewModel
        get() = viewModel2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        StatusBarUtil.setTransparent(this)
        AppEventsLogger.activateApp(application)
        FacebookSdk.sdkInitialize(applicationContext);




        socialMediaLogin = SocialMediaLogin(application, this)

        catchIntentExtraAndViewHideDesign()
//        UserSessionManagement.clearSharedPreferences()

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

    private fun catchIntentExtraAndViewHideDesign() {


        if (intent != null) {
            val stringExtra = intent.getStringExtra("directionName")


            if (intent.getIntExtra(
                    "landingPage",
                    -1
                ) == 1
            ) {
                val navController = findNavController(R.id.nav_host_fragment_activity_main)


                navController.navigate(R.id.action_navigation_to_login);

            } else if (intent.getStringExtra("directionName") != null) {

                val navController = findNavController(R.id.nav_host_fragment_activity_main)


                navController.navigate(R.id.action_navigation_to_login);
//                goToFragment(
//                    "login",
//                    "login",
//                    com.friendzrandroid.R.id.fragmentLogin
//                )


            }


        } else {
            var x =0


//            inflateVavController()


        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.RlMainMainContainer, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }


    fun googleLoginListener(isLogin: Boolean, viewModel: AuthViewModel) {
        socialMediaLogin.googleLogin(resultLauncher, viewModel, isLogin)

//        val googleEmail = socialMediaLogin!!.socialMediaUserEmail
//        val googleId = socialMediaLogin!!.socialMediaUserId

//        if (googleId != null) {
//            if (isLogin)
//                viewModel.performLogin(
//                    googleEmail.toString(),
//                    "",
//                    LoginRegisterTypeEnum.GMAIL,
//                    googleId.toString(),
//                    LoginRegisterTypeEnum.ANDROID
//                )
//            else
//                viewModel.performRegister(
//                    "",
//                    googleEmail.toString(),
//                    "",
//                    LoginRegisterTypeEnum.GMAIL,
//                    googleId.toString(),
//                    LoginRegisterTypeEnum.ANDROID
//                )
//
//        }
    }

    fun facebookLoginListener(isLogin: Boolean, viewModel: AuthViewModel) {

        callFaceBook()
//        socialMediaLogin.socialMediaFaceBookLogin(isLogin, viewModel)

//        val faceBookEmail = socialMediaLogin.socialMediaUserEmail
//        val faceBookId = socialMediaLogin.socialMediaUserId
//
//        if (faceBookId != null) {
//            if (isLogin)
//                viewModel.performLogin(
//                    faceBookEmail.toString(),
//                    "",
//                    LoginRegisterTypeEnum.FACEBOOK,
//                    faceBookId.toString(),
//                    LoginRegisterTypeEnum.ANDROID
//                )
//            else
//                viewModel.performRegister(
//                    "",
//                    faceBookEmail.toString(),
//                    "",
//                    LoginRegisterTypeEnum.FACEBOOK,
//                    faceBookId.toString(),
//                    LoginRegisterTypeEnum.ANDROID
//                )
//
//        }

    }

    fun handleFaceBookLogin(isLogin: Boolean, viewModel: AuthViewModel) {
        val faceBookEmail = socialMediaLogin.socialMediaUserEmail
        val faceBookId = socialMediaLogin.socialMediaUserId

        if (faceBookId != null) {
            if (isLogin)
                viewModel.performLogin(
                    faceBookEmail.toString(),
                    "",
                    LoginRegisterTypeEnum.FACEBOOK,
                    faceBookId.toString(),
                    LoginRegisterTypeEnum.ANDROID
                )
            else
                viewModel.performRegister(
                    "",
                    faceBookEmail.toString(),
                    "",
                    LoginRegisterTypeEnum.FACEBOOK,
                    faceBookId.toString(),
                    LoginRegisterTypeEnum.ANDROID
                )

        }
    }

    private fun callFaceBook() {


        callbackManager = CallbackManager.Factory.create()

//        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        LoginManager.getInstance()
            .logInWithReadPermissions(
                this,
                Arrays.asList("email")
            )


        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken

                    val isLoggedIn = !accessToken.isExpired
                    val loggedOut = AccessToken.getCurrentAccessToken() == null
                    if (!loggedOut) {
                        viewModel?.let { getFaceBookUserProfile(result, isLogin, it) }
                    } else {
                        var x = 0
                    }

                }


                override fun onCancel() {
                    Toast.makeText(
                        applicationContext,
                        getString(com.facebook.R.string.search_menu_title),
                        Toast.LENGTH_LONG
                    ).show()

                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }
            })

    }


    var resultLauncher =

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            var x = 3
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
//                callbackManager!!.onActivityResult(result.resultCode, result.resultCode, data)

                socialMediaLogin.activityResult(result.resultCode, result.resultCode, data!!);


            } else {
                x = 0
            }


        }


    private fun getFaceBookUserProfile(
        loginResult: LoginResult,
        isLogin: Boolean,
        viewModel: AuthViewModel
    ) {
        val accessToken = loginResult.accessToken


        val request = GraphRequest.newMeRequest(
            accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(
                    obj: JSONObject?,
                    response: GraphResponse?
                ) {

                    try {
                        if (response?.error != null) {
                            // handle error
                            response.error!!.errorMessage?.let { Log.e("FaceBook", it) }
                        } else {
                            if (obj != null) {

                                socialMediaUserId = obj.optString("id")
                                var id = obj?.optString("person-id")
                                faceBookFirst_name = obj?.optString("first_name")
                                faceBookLast_name = obj?.optString("last_name")
                                faceBookGender = obj?.optString("gender")
                                socialMediaUserEmail = obj?.getString("email")
                                faceBookName = obj?.optString("name")
                                faceBookBirthday = obj?.optString("user_birthday")
                                socialMediaUserEmail = obj?.optString("email")
                                faceBookPhone = obj?.optString("phone")


                                handleFaceBookLogin(isLogin, viewModel)

                            }
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            applicationContext,
                            "Facebook Authentication Failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })


        val parameters = Bundle()
        parameters.putString(
            "fields",
            "public_profile,email,id,name,first_name,last_name,email,gender, birthday"
        )
        request.parameters = parameters
        request.executeAsync()


//        val mGraphRequest = GraphRequest.newMeRequest(
//            accessToken
//        ) { me, response ->
//            if (response?.error != null) {
//                // handle error
//                response.error!!.errorMessage?.let { Log.e("FaceBook", it) }
//            } else {
//                socialMediaUserId = me?.optString("id")
//                var id = me?.optString("person-id")
//                faceBookFirst_name = me?.optString("first_name")
//                faceBookLast_name = me?.optString("last_name")
//                faceBookGender = me?.optString("gender")
//
//
//                //                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
//                try {
//                    socialMediaUserEmail = me?.getString("email")
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                faceBookName = me?.optString("name")
//                faceBookBirthday = me?.optString("user_birthday")
//                socialMediaUserEmail = me?.optString("email")
//                faceBookPhone = me?.optString("phone")
//
//
////                facebookLogin()
//
//            }
//        }
//        val parameters2 = Bundle()
//        parameters2.putString("fields", "id,name,first_name,last_name,email,gender, birthday")
//        mGraphRequest.parameters = parameters2
//        mGraphRequest.executeAsync()


    }

/*
    override fun onBackPressed() {

        if (UserSessionManagement.userNeedToUpdate() == NeedToUpdateStatus.UPDATE_PROFILE.status) {
            finish()
        } else {
            val token = UserSessionManagement.getKeyAuthToken()

            if (token == null) {

                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else {
                super.onBackPressed()

            }
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialMediaLogin.callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}