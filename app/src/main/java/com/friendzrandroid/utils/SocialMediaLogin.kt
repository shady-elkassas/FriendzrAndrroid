package com.friendzrandroid.utils


import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.data.model.SocialMediaLoginResponse
import com.friendzrandroid.auth.presentation.AuthViewModel
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.home.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Job
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SocialMediaLogin(application: Application, activity: Activity) : AppCompatActivity() {
    var callbackManager: CallbackManager? = null

    private val TAG = "FACEBOOK"
    private val RC_SIGN_IN = 1

    var socialMediaUserId: String? = null
    var faceBookFirst_name: String? = null
    var faceBookLast_name: String? = null
    var faceBookGender: String? = null
    var faceBookName: String? = null
    var faceBookBirthday: String? = null
    var faceBookUserFacebookEmail: String? = null
    var socialMediaUserEmail: String? = null
    var twitterEmail: String? = null
    var userFacebookPhone: String? = null
    var faceBookPhone: String? = null
    var subScribeUserId: String? = null
    var language: String? = null

    var viewModel: AuthViewModel? = null
    var isLogin: Boolean = false
    val activity = activity
    val validation = application
    var mGoogleSignInClient: GoogleSignInClient? = null
    val GOOGLE_REQUEST_CODE = 1
    private var loginJob: Job? = null


    init {
//        FacebookSdk.sdkInitialize(validation)
        AppEventsLogger.activateApp(validation)
        googleLoginInit(activity)

    }

    fun googleLoginInit(activity: Activity?) {

        // Configure sign-in to request the user's ID, email    private static CallbackManager callbackManager; address, and basic
// test_profile. ID and basic test_profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        //        requestIdToken("22697418790-vg96ghaj0s2tbqdlp9so5pdena7lkfss.apps.googleusercontent.com")
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
    }

    fun socialMediaFaceBookLogin(isLogin: Boolean, viewModel: AuthViewModel) {

        callbackManager = CallbackManager.Factory.create()

//        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        LoginManager.getInstance()
            .logInWithReadPermissions(
                activity,
                Arrays.asList("public_profile", "email", "user_friends")
            )


        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    val isLoggedIn = !accessToken.isExpired
                    val loggedOut = AccessToken.getCurrentAccessToken() == null

                    if (!loggedOut) {
                        getFaceBookUserProfile(result, isLogin, viewModel)
                    } else {
                        var x = 0
                    }

                }


                override fun onCancel() {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.search_menu_title),
                        Toast.LENGTH_LONG
                    ).show()

                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                }
            })


    }


    fun googleLogin(
        resultLauncher: ActivityResultLauncher<Intent>,
        vm: AuthViewModel,
        isLoginIn: Boolean
    ) {

        viewModel = vm
        isLogin = isLoginIn
        mGoogleSignInClient?.signOut()
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        resultLauncher.launch(signInIntent)

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
                    userObject: JSONObject?,
                    response: GraphResponse?
                ) {

                    try {
                        if (response?.error != null) {
                            // handle error
                            response.error!!.errorMessage?.let { Log.e("FaceBook", it) }
                        } else {
                            if (userObject != null) {

                                socialMediaUserId = userObject?.optString("id")
                                var id = userObject?.optString("person-id")
                                faceBookFirst_name = userObject?.optString("first_name")
                                faceBookLast_name = userObject?.optString("last_name")
                                faceBookGender = userObject?.optString("gender")
                                socialMediaUserEmail = userObject?.getString("email")
                                faceBookName = userObject?.optString("name")
                                faceBookBirthday = userObject?.optString("user_birthday")
                                socialMediaUserEmail = userObject?.optString("email")
                                faceBookPhone = userObject?.optString("phone")

                                (activity as AuthActivity).handleFaceBookLogin(isLogin, viewModel)

                            }
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity,
                            "Facebook Authentication Failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })

        val parameters = Bundle()
        parameters.putString("fields", "id,name,first_name,last_name,email,gender, birthday")
        request.parameters = parameters
        request.executeAsync()


        val mGraphRequest = GraphRequest.newMeRequest(
            accessToken
        ) { me, response ->
            if (response?.error != null) {
                // handle error
                response.error!!.errorMessage?.let { Log.e("FaceBook", it) }
            } else {
                socialMediaUserId = me?.optString("id")
                var id = me?.optString("person-id")
                faceBookFirst_name = me?.optString("first_name")
                faceBookLast_name = me?.optString("last_name")
                faceBookGender = me?.optString("gender")


                //                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
                try {
                    socialMediaUserEmail = me?.getString("email")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                faceBookName = me?.optString("name")
                faceBookBirthday = me?.optString("user_birthday")
                socialMediaUserEmail = me?.optString("email")
                faceBookPhone = me?.optString("phone")


//                facebookLogin()

            }
        }
        val parameters2 = Bundle()
        parameters2.putString("fields", "id,name,first_name,last_name,email,gender, birthday")
        mGraphRequest.parameters = parameters2
        mGraphRequest.executeAsync()


    }


    fun activityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (resultCode == Activity.RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (socialMediaUserId == null) {
                handleGoogleSignInResult(task)
            }

        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            googleUpdateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            googleUpdateUI(null)
        }
    }

    private fun isGoogleClientExisting() {

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        googleUpdateUI(account)
    }

    private fun googleUpdateUI(account: GoogleSignInAccount?) {
        socialMediaUserEmail = account!!.email
        val displayName = account.displayName
        val idToken = account.idToken;
        val familyName = account.familyName
        val firstnameName = account.givenName;
        socialMediaUserId = account.id;
        val x = 0

        if (socialMediaUserId != null) {
            if (isLogin)
                viewModel!!.performLogin(
                    socialMediaUserEmail.toString(),
                    "",
                    LoginRegisterTypeEnum.GMAIL,
                    socialMediaUserId.toString(),
                    LoginRegisterTypeEnum.ANDROID
                )
            else
                viewModel!!.performRegister(
                    "",
                    socialMediaUserEmail.toString(),
                    "",
                    LoginRegisterTypeEnum.GMAIL,
                    socialMediaUserId.toString(),
                    LoginRegisterTypeEnum.ANDROID
                )

        }


//        googleLogin(email, id, familyName, firstnameName)
    }


    private fun popupMessage(toastMessage: String) =
        Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show()

    private fun openMainActivity(userData: SocialMediaLoginResponse.Data) {


        SharedPreferenceManager.getInstance(activity).saveUserToken(userData.token)
        SharedPreferenceManager.getInstance(activity).saveUserName(userData.name)
        SharedPreferenceManager.getInstance(activity).saveUserEmail(userData.email)

        startActivity(
            Intent(
                activity,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }


    // this method to logout  the user if he is  logout his account

    fun signOut() {

        if (mGoogleSignInClient != null) {
            mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(this) {
                    var x = 0
                }
        }


        if (callbackManager != null) {
            LoginManager.getInstance().logOut();

        }


    }


    // this method to remove the user if the delete his account
    fun revokeAccess() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient!!.revokeAccess()
                .addOnCompleteListener(this) {

                }
        }
    }
}
