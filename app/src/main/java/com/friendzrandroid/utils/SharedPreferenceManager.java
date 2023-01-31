package com.friendzrandroid.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;


public class SharedPreferenceManager {

    private static final String USER_POINTS = "userPoints";
    private static final String USER_VERIFIED = "userVerified";
    private static final String USER_ROLE = "userRole";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_COUNTRY_ID = "countryId";
    private static final String STREET_NAME = "streetName";
    private static final String PRODUCT_ID = "productId";
    private static final String CATEGORY_ID = "categoryId";
    private static final String SUB_CATEGORY_ID = "subCategoryId";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String MAIN_ADDRESS_ID = "mainAddressId";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_PHONE_KEY = "user_phone_key";
    private static final String USER_CITY = "userCity";
    private static final String USER_INFO = "userInfo";
    private static final String FIRST_TIME = "firstTime";
    private static final String ABOUT_TAMA = "about_tamam";
    private static final String CART_OBJECT = "cartObject";
    private static final String NOTIFICATION_TOKEN = "notification_token";
    private static String USER_ID = "userId";
    private static String USER_TYPE = "userType";
    private static String USER_NAME = "userName";
    public static String USER_SIGNED_IN = "userSignedIn";
    public static String USER_COUNTRY = "userCountry";
    public static String USER_CURRENCY = "userCurrency";
    public static String USER_TOKEN = "userToken";
    private static String RESTRAUNT_ID = "restraunt_id";

    private static String LANGUAGE = "selectedLanguage";
    public static String PREFS = "PREFS";
    private static String USER_IMAGE = "userImage";

    private static final String USER_ADDRESS = "userAddress";
    private static final String City_NAME = "cityName";
    private static final String COUNTRY_NAME = "cityName";
    private static final String DISTRICT_NAME = "districtName";

    private static final String CATEGORY_NAME = "categoryName";


    private static final String fcmToken = "fcm_token";


    private static final String USER_MAPADDRESS = "userMapAddress";
    private static final String PAYMENT_id = "paymentId";
    private static final String USER_LAT = "userLat";
    private static final String USER_LONG = "userLong";
    public static SharedPreferences sharedPreferences;
    private static final String ORDER_ID = "orderId";


    private static SharedPreferenceManager instance;

    public static SharedPreferenceManager getInstance(Activity context) {
        if (instance == null) {
            instance = new SharedPreferenceManager();
            sharedPreferences = context.getSharedPreferences(SharedPreferenceManager.PREFS, context.MODE_PRIVATE);

        }
        return instance;
    }

    public void saveUserToken(String userToken) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_TOKEN, userToken);
        myEditor.apply();
        myEditor.commit();
    }

    public void saveUserEmail(String mail) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_EMAIL, mail);
        myEditor.apply();
    }

    public void saveUserName(String name) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_NAME, name);
        myEditor.apply();
    }

    public void saveUserPhoneKey(String phoneKey) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_PHONE_KEY, phoneKey);
        myEditor.apply();
    }

    public void saveUserPhone(String phone) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_PHONE, phone);
        myEditor.apply();
    }

    public String getUserPhoneKey() {
        return sharedPreferences.getString(USER_PHONE_KEY, null);
    }
    public String getUserPhone() {
        return sharedPreferences.getString(USER_PHONE, null);
    }
    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, null);
    }
    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, null);
    }



    public void saveFcmToken(String fcmTokens) { // used to save in fcm token

        sharedPreferences.edit().putString(fcmToken, fcmTokens).apply();
    }

    public String loadFcmToken() { // used to access firebase
        return sharedPreferences.getString(fcmToken, "ddasdsadasd");
    }

    public void saveLanguage(String code) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(LANGUAGE, code);
        myEditor.apply();
    }
    public Boolean loadLanguage() {
        String lang = sharedPreferences.getString(LANGUAGE, "English");


        if (lang.equals("English")) {
            return false;
        } else if (lang.equals("عربى")) {
            return true;
        } else {
            return true;
        }

    }


    public void saveUserIsSigned(Boolean isSigned) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putBoolean(USER_SIGNED_IN, isSigned);
        myEditor.apply();
        myEditor.commit();
    }

    public void saveMainAddressID(String id) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(MAIN_ADDRESS_ID, id);
        myEditor.apply();

    }

    public String loadOrderId() {
        return sharedPreferences.getString(ORDER_ID, null);
    }

    public void saveOrderId(String orderId) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(ORDER_ID, orderId);
        myEditor.apply();
    }

    public void saveCountry(String country) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_COUNTRY, country);
        myEditor.apply();
    }

    public void saveCountryId(String countryId) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_COUNTRY_ID, countryId);
        myEditor.apply();
    }

    public void saveCurrency(String currency) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_CURRENCY, currency);
        myEditor.apply();
    }

    public String loadMainAddressId() {
        return sharedPreferences.getString(MAIN_ADDRESS_ID, null);
    }



    public String loadCountry() {
        return sharedPreferences.getString(USER_COUNTRY, null);
    }

    public String loadCountryId() {
        return sharedPreferences.getString(USER_COUNTRY_ID, null);
    }

    public String loadCurrency() {
        return sharedPreferences.getString(USER_CURRENCY, null);
    }

    public void saveRestrauntId(String restrauntId) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(RESTRAUNT_ID, restrauntId);
        myEditor.apply();
    }

    public String loadRestrauntId() {
        return sharedPreferences.getString(RESTRAUNT_ID, "1");
    }




    public void saveNotificationToken(String notificationToken) {


        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putString(NOTIFICATION_TOKEN, notificationToken);


        myEditor.apply();
        myEditor.commit();


    }

    public String loadUserId() {
        return sharedPreferences.getString(USER_ID, null);
    }

    public String loadUserInfo() {
        return sharedPreferences.getString(USER_INFO, null);
    }

    public void saveCategoryId(String productId) {


        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putString(CATEGORY_ID, productId);


        myEditor.apply();
        myEditor.commit();


    }

    public String loadCategoryId() {
        return sharedPreferences.getString(CATEGORY_ID, null);
    }


    public void saveSubCategoryId(String subCategoryId) {


        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putString(SUB_CATEGORY_ID, subCategoryId);


        myEditor.apply();
        myEditor.commit();


    }

    public String loadSubCategoryId() {
        return sharedPreferences.getString(SUB_CATEGORY_ID, null);
    }

    public void savePaymentId(String paymentId) {


        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putString(PAYMENT_id, paymentId);


        myEditor.apply();
        myEditor.commit();


    }

    public String loadPaymentId() {
        return sharedPreferences.getString(PAYMENT_id, null);
    }


    public void saveProductId(String productId) {


        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putString(PRODUCT_ID, productId);


        myEditor.apply();
        myEditor.commit();


    }

    public String loadProductId() {
        return sharedPreferences.getString(PRODUCT_ID, null);
    }


    public String loadUserName() {
        return sharedPreferences.getString(USER_NAME, null);
    }

    public Boolean loadIsUserVerfied() {
        return sharedPreferences.getBoolean(USER_VERIFIED, false);
    }

    public String loadUserToken() {
        return sharedPreferences.getString(USER_TOKEN, null);
    }

    public String loadNotificationToken() {
        return sharedPreferences.getString(NOTIFICATION_TOKEN, null);
    }

    public int loadUserPoints() {
        return sharedPreferences.getInt(USER_POINTS, 0);
    }

    public String loadUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, null);
    }

    public String loadUserRole() {
        return sharedPreferences.getString(USER_ROLE, null);
    }

    public String loadStreetName() {
        return sharedPreferences.getString(STREET_NAME, null);
    }

    public void saveUserCoordinates(String userAddress, String countryName, String cityName, String districtName, LatLng userLatLong, String street, String countryCode) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_MAPADDRESS, userAddress);
        myEditor.putString(COUNTRY_NAME, countryName);
        myEditor.putString(COUNTRY_CODE, countryCode);
        myEditor.putString(STREET_NAME, street);
        myEditor.putString(City_NAME, cityName);
        myEditor.putString(DISTRICT_NAME, districtName);
        myEditor.putFloat(USER_LAT, (float) userLatLong.latitude);
        myEditor.putFloat(USER_LONG, (float) userLatLong.longitude);

        myEditor.apply();
        myEditor.commit();
    }

    public void saveCategoryName(String categoryName) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(CATEGORY_NAME, categoryName);


        myEditor.apply();
        myEditor.commit();
    }


    public String loadCountryName() {
        return sharedPreferences.getString(COUNTRY_NAME, null);
    }

    public String loadCountryCode() {
        return sharedPreferences.getString(COUNTRY_CODE, "EG");
    }

    public String loadCityName() {
        return sharedPreferences.getString(City_NAME, null);
    }

    public String loadDistrictName() {
        return sharedPreferences.getString(DISTRICT_NAME, null);
    }


    public String loadCategoryName() {
        return sharedPreferences.getString(CATEGORY_NAME, null);
    }

    public String loadUserAddress() {
        return sharedPreferences.getString(USER_ADDRESS, null);
    }

    public String loadUserType() {
        return sharedPreferences.getString(USER_TYPE, null);
    }


    public float loadUserLat() {
        return sharedPreferences.getFloat(USER_LAT, 25.5f);
    }

    public float loadUserLong() {
        return sharedPreferences.getFloat(USER_LONG, 27.5f);
    }

    public void saveUserLatLong(float userLat, float userLong) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putFloat(USER_LAT, userLat);
        myEditor.putFloat(USER_LONG, userLong);


        myEditor.apply();
        myEditor.commit();
    }


    public void deleteUserInfo() {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.remove(USER_ID);
        myEditor.remove(USER_TYPE);
        myEditor.remove(USER_NAME);
        myEditor.putBoolean(USER_SIGNED_IN, false);
        myEditor.remove(USER_TOKEN);
        myEditor.apply();
        myEditor.commit();
    }

    public void clean() {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.remove(USER_TOKEN);
        myEditor.clear();
        myEditor.apply();
        myEditor.commit();
    }

    public void cleanUserLocation() {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.remove(USER_ADDRESS);
        myEditor.remove(USER_MAPADDRESS);
        myEditor.remove(USER_LAT);
        myEditor.remove(USER_LONG);
        myEditor.remove(COUNTRY_CODE);
        myEditor.remove(COUNTRY_NAME);
        myEditor.remove(STREET_NAME);
        myEditor.remove(City_NAME);
        myEditor.remove(DISTRICT_NAME);


        myEditor.commit();
    }


    public String loadUserMapAddress() {
        return sharedPreferences.getString(USER_MAPADDRESS, null);
    }

    public void changeUserImage(String userImage, String notNull) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(USER_IMAGE, userImage);
        myEditor.apply();
        myEditor.commit();
    }

    public String loadUserImage() {
        return sharedPreferences.getString(USER_IMAGE, null);
    }


    public void cleanSignUpImages() {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.remove(USER_IMAGE);

        myEditor.apply();
        myEditor.commit();
    }

    public void saveFirstTime(boolean firstTime) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putBoolean(FIRST_TIME, firstTime);
        myEditor.apply();
    }

    public boolean loadIsFirstTime() {
        return sharedPreferences.getBoolean(FIRST_TIME, true);
    }

    public void saveAboutTamam(String aboutTamam) {
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        myEditor.putString(ABOUT_TAMA, aboutTamam);
        myEditor.apply();
    }

    public String loadAboutTamam() {
        return sharedPreferences.getString(ABOUT_TAMA, null);
    }


}
