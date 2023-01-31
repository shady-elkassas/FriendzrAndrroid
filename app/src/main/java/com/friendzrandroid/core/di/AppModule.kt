package com.friendzrandroid.core.di


import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.friendzrandroid.auth.presentation.view.activity.AuthActivity
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.MainActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp
import org.json.JSONException
import org.json.JSONObject
import java.util.*


@HiltAndroidApp
class AppModule : Application() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val LOG_TAG = "AppsFlyerOneLinkSimApp"
    val DL_ATTRS = "dl_attrs"
    val appsFlayerKey: String = "vsg4WBcUHeJBLTpcHDpuJ"
    var conversionData: Map<String, Any>? = null

    override fun onCreate() {
        super.onCreate()



        UserSessionManagement.init(this)

        //MobileAds.initialize(this)
        MobileAds.initialize(this) { initializationStatus ->
            val statusMap =
                initializationStatus.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d(
                    "MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status!!.description, status.latency


                    )
                )
            }


//            AppsFlyerLib.getInstance().init(appsFlayerKey, null, this)
//            AppsFlyerLib.getInstance().start(this)
//            AppsFlyerLib.getInstance().setDebugLog(true)

//            initAppsFlyer()

            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics


//            AppsFlyerLib.getInstance().start(this, "vsg4WBcUHeJBLTpcHDpuJ", object : AppsFlyerRequestListener {
//            override fun onSuccess() {
//                print("Launch sent successfully")
//                Log.d("LOG_TAG", "Launch sent successfully")
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String) {
//                print("Launch failed to be sent:")
//
//                Log.d("LOG_TAG", "Launch failed to be sent:\n" +
//                        "Error code: " + errorCode + "\n"
//                        + "Error description: " + errorDesc)
//            }
//        })


//        AppLovinSdk.getInstance(this).mediationProvider = "max"
//        AppLovinSdk.getInstance(this).initializeSdk { configuration: AppLovinSdkConfiguration ->
//            // Initialize Adjust SDK
//
//        }
//        AdSettings.setDataProcessingOptions(arrayOf<String>())
//
//        //AppLovinSdk.getInstance(this).showMediationDebugger()
//        AppLovinSdk.getInstance(this).settings.isCreativeDebuggerEnabled = true


        }


    }

    private fun initAppsFlyer() {


        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setDebugLog(true)

//        appsflyer.setMinTimeBetweenSessions(0)
//        AppsFlyerLib.getInstance().setAppInviteOneLink("59hw")


        appsflyer.subscribeForDeepLink(DeepLinkListener { deepLinkResult ->
            val dlStatus = deepLinkResult.status
            if (dlStatus == DeepLinkResult.Status.FOUND) {
                Log.d(LOG_TAG, "Deep link found")
            } else if (dlStatus == DeepLinkResult.Status.NOT_FOUND) {
                Log.d(LOG_TAG, "Deep link not found")
                return@DeepLinkListener
            } else {
                // dlStatus == DeepLinkResult.Status.ERROR
                val dlError = deepLinkResult.error
                Log.d(LOG_TAG, "There was an error getting Deep Link data: $dlError")
                return@DeepLinkListener
            }


            val deepLinkObj = deepLinkResult.deepLink
            try {
                Log.d(LOG_TAG, "The DeepLink data is: $deepLinkObj")
            } catch (e: Exception) {
                Log.d(LOG_TAG, "DeepLink data came back null")
                return@DeepLinkListener
            }
            // An example for using is_deferred
            if (deepLinkObj.isDeferred!!) {
                Log.d(LOG_TAG, "This is a deferred deep link")
            } else {
                Log.d(LOG_TAG, "This is a direct deep link")
            }
            // An example for getting deep_link_value
            var deepLinkValue: String? = ""
            try {
                deepLinkValue = deepLinkObj.deepLinkValue
                var referrerId: String? = ""
                var deepLinkSub1: String? = ""
                var deepLinkSub2: String? = ""
                val dlData = deepLinkObj.clickEvent


                if (deepLinkValue == null || deepLinkValue == "") {
                    Log.d(LOG_TAG, "deep_link_value returned null")
                    deepLinkValue = deepLinkObj.getStringValue("checkOut")
                    Log.d(LOG_TAG, "deepLinkValue is :$deepLinkValue")


                    if (deepLinkValue == null || deepLinkValue == "") {
                        Log.d(LOG_TAG, "could not find fruit name")
                        return@DeepLinkListener
                    }
                    Log.d(LOG_TAG, "fruit_name is $deepLinkValue. This is an old link")
                } else {

                    deepLinkSub1 = deepLinkObj!!.getStringValue("deep_link_sub1")

                    if (deepLinkSub1 != null) {
                        if (UserSessionManagement.getKeyAuthToken() != null) {

//                            if (deepLinkValue.equals("directionalFiltering")) {
//
//                                goToFragment(deepLinkSub1, "directionalFiltering")
//
//
//                            }

                /*            if (deepLinkValue.equals("checkOut")) {

                                handleCheckOutLink(dlData, deepLinkValue, deepLinkObj)


                            } else if (deepLinkValue.equals("event")) {
                                goToFragment(deepLinkSub1, "eventDetails")
                            } else if (deepLinkValue.equals("createEvent")) {

                                goToFragment(deepLinkSub1, "createEvent")
                            } else if (deepLinkValue.equals("eventFilter")) {
                                goToFragment(deepLinkSub1, "eventFilter")
                            } else if (deepLinkValue.equals("feed")) {

                                goToFragment(deepLinkSub1, "feed")


                            } else if (deepLinkValue.equals("directionalFiltering")) {

                                goToFragment(deepLinkSub1, "directionalFiltering")


                            } else if (deepLinkValue.equals("login")) {

                                if (UserSessionManagement.getKeyAuthToken() != null) {
                                    goToFragment(deepLinkSub1, "feed")

                                } else {
                                    val intent =
                                        Intent(applicationContext, AuthActivity::class.java)
                                    intent.putExtra("directionName", "login");
                                    intent.putExtra("directionId", "login");

                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }


                            } else if (deepLinkValue.equals("interests")) {

                                goToFragment(deepLinkSub1, "interests")


                            } else if (deepLinkValue.equals("editProfile")) {

                                goToFragment(deepLinkSub1, "editProfile")


                            } else if (deepLinkValue.equals("personalSpace")) {

                                goToFragment(deepLinkSub1, "personalSpace")


                            } else if (deepLinkValue.equals("ageFilter")) {
                                goToFragment(deepLinkSub1, "ageFilter")


                            } else if (deepLinkValue.equals("privateMode")) {
                                goToFragment(deepLinkSub1, "privateMode")


                            } else if (deepLinkValue.equals("distanceFilter")) {
                                goToFragment(deepLinkSub1, "distanceFilter")


                            } else if (deepLinkValue.equals("eventFilter")) {
                                goToFragment(deepLinkSub1, "eventFilter")


                            }*/


                        } else {
                            val intent =
                                Intent(applicationContext, AuthActivity::class.java)
                            intent.putExtra("directionName", "login");
                            intent.putExtra("directionId", "login");

                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

//                            if (deepLinkValue.equals("login")) {
//
//                                if (UserSessionManagement.getKeyAuthToken() != null) {
//                                    goToFragment(deepLinkSub1, "feed")
//
//                                } else {
//
//                                }
//
//
//                            }
                        }


                    }
                }

                if (dlData.has("deep_link_sub1")) {

                    deepLinkSub1 = deepLinkObj.getStringValue("deep_link_sub1")
                    Log.d(LOG_TAG, "The referrerID is: $referrerId")

                }
                if (dlData.has("deep_link_sub2")) {

                    deepLinkSub2 = deepLinkObj.getStringValue("deep_link_sub2")
                    Log.d(LOG_TAG, "The referrerID is: $referrerId")
                } else {
                    Log.d(LOG_TAG, "deep_link_sub2/Referrer ID not found")
                }





                Log.d(LOG_TAG, "The DeepLink will route to: $deepLinkValue")
            } catch (e: Exception) {
                Log.d(LOG_TAG, "There's been an error: $e")
                return@DeepLinkListener
            }
//            goToFruit(deepLinkValue, deepLinkObj)
        })

        val conversionListener: AppsFlyerConversionListener =
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>) {
                    for (attrName in conversionDataMap.keys) Log.d(
                        LOG_TAG,
                        "Conversion attribute keys: " + attrName + " = " + conversionDataMap[attrName]
                    )
                    val status =
                        Objects.requireNonNull(conversionDataMap["af_status"]).toString()
                    if (status == "Non-organic") {
                        if (Objects.requireNonNull(conversionDataMap["is_first_launch"])
                                .toString() == "true"
                        ) {

                            //Deferred deep link in case of a legacy link
                            if (conversionDataMap.containsKey("fruit_name")) {
                                if (conversionDataMap.containsKey("deep_link_value")) { //Not legacy link
                                    Log.d(
                                        LOG_TAG,
                                        "onConversionDataSuccess: Link contains deep_link_value, deep linking with UDL"
                                    )
                                } else {

                                    //Legacy link
                                    conversionDataMap["deep_link_value"] =
                                        conversionDataMap["event"]!!

                                    val deepLinkValueStr =
                                        conversionDataMap["event"] as String?

                                    val deepLinkData: DeepLink =
                                        mapToDeepLinkObject(conversionDataMap)!!
//                                    val deepLinkValue = deepLinkData.deepLinkValue
//                                    val deepLinkSub1 = deepLinkData.afSub1


                                }


                            }


                        } else {
                            Log.d(LOG_TAG, "Conversion: Not First Launch")
                        }
                    } else {
                        Log.d(LOG_TAG, "Conversion: This is an organic install.")
                    }
                    conversionData = conversionDataMap
                }

                override fun onConversionDataFail(errorMessage: String) {
                    Log.d(LOG_TAG, "error getting conversion data: $errorMessage")
                }

                override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                    Log.d(LOG_TAG, "onAppOpenAttribution: This is fake call.")
                }

                override fun onAttributionFailure(errorMessage: String) {
                    Log.d(LOG_TAG, "error onAttributionFailure : $errorMessage")
                }
            }

        appsflyer.init("vsg4WBcUHeJBLTpcHDpuJ", conversionListener, this)
        appsflyer.start(this)
        appsflyer.setDebugLog(true)

    }

    private fun handleInterestsLink(
        dlData: JSONObject?,
        deepLinkValue: String,
        deepLinkObj: DeepLink?
    ) {
        TODO("Not yet implemented")
    }

    private fun handleEventLink(
        dlData: JSONObject?,
        deepLinkValue: String,
        deepLinkObj: DeepLink?
    ) {
        if (dlData!!.has("deep_link_sub1")) {
            var deepLinkSub1 = deepLinkObj!!.getStringValue("deep_link_sub1")

            if (deepLinkSub1 != null) {

                goToFragment(deepLinkSub1, "eventDetails")

//                val intent = Intent(applicationContext, MainActivity::class.java)
//                intent.putExtra("goTo", "eventDetails");
//                intent.putExtra("eventId", deepLinkSub1);
//
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)


            }


        }
    }

    private fun handleCheckOutLink(
        dlData: JSONObject?,
        deepLinkValue: String,
        deepLinkObj: DeepLink?
    ) {

        if (dlData!!.has("deep_link_sub1")) {
            var deepLinkSub1 = deepLinkObj?.getStringValue("deep_link_sub1")


            if (deepLinkSub1 != null) {
                if (deepLinkSub1.equals("editProfile")) {
                    goToFragment(deepLinkSub1, "editProfile")

                } else if (deepLinkSub1.equals("interests")) {
                    goToFragment(deepLinkSub1, "interests")


                } else if (deepLinkSub1.equals("personalSpace")) {

                    goToFragment(deepLinkSub1, "personalSpace")

                } else if (deepLinkSub1.equals("ageFilter")) {

                    goToFragment(deepLinkSub1, "ageFilter")


                } else if (deepLinkSub1.equals("privateMode")) {
                    goToFragment(deepLinkSub1, "privateMode")


                } else if (deepLinkSub1.equals("distanceFilter")) {
                    goToFragment(deepLinkSub1, "distanceFilter")


                } else if (deepLinkSub1.equals("eventFilter")) {
                    goToFragment(deepLinkSub1, "eventFilter")


                } else {
                    goToFragment(deepLinkSub1, "feed")


                }


            }


        }

    }


    private fun goToFragment(directionId: String, directionName: String) {

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("directionName", directionName);
        intent.putExtra("directionId", directionId);

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

    }


    fun mapToDeepLinkObject(conversionDataMap: MutableMap<String, Any>): DeepLink? {
        try {
            val objToStr = Gson().toJson(conversionDataMap)
            val deepLink: DeepLink = DeepLink.AFInAppEventParameterName(JSONObject(objToStr))



            return deepLink
        } catch (e: JSONException) {
            Log.d(
                LOG_TAG,
                "Error when converting map to DeepLink object: $e"
            )
        }

        return null
    }


}