package com.craftsilicon.weather.app.sharedprefs;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SecurePrefs {

    private static final String MIDDLE_NAME = "MIDDLE_NAME";
    private final SharedPreferences mSharedPreferences;
    public static final String KEY_TOKEN = "TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String NATIONAL_ID = "NATIONAL_ID";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String MEMBER_NO = "MEMBER_NO";

    public static final String M_PIN = "M_PIN";
    public static final String M_ID = "M_ID";


    public static final String EMAIL = "EMAIL";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String TRANSACTION_LIMIT = "TRANSACTION_LIMIT";
    public static final String ACCOUNT_NUMBER ="ACCOUNT_NUMBER";
    public static final String BIOMETRIC ="BIOMETRIC";



    @Inject
    public SecurePrefs(Application application) {
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public String loadString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void saveToken(String token) throws GeneralSecurityException, IOException {
        saveString(KEY_TOKEN, token);
    }


    public String getToken() throws GeneralSecurityException, IOException {

        return loadString(KEY_TOKEN, null);
    }

    public void saveUserId(String userId) throws GeneralSecurityException, IOException {
        saveString(USER_ID, userId);
    }

    public String getUserId() throws GeneralSecurityException, IOException {
        return loadString(USER_ID, null);
    }

    public void savePhone(String phone) {
        saveString(PHONE_NUMBER, phone);
    }

    public String getPhone() throws GeneralSecurityException, IOException {
        return loadString(PHONE_NUMBER, null);
    }

    public void saveNationalId(String nationalId) throws GeneralSecurityException, IOException {
        saveString(NATIONAL_ID, nationalId);
    }

    public String getNationalId() throws GeneralSecurityException, IOException {
        return loadString(NATIONAL_ID, null);
    }

    public void saveMemberNo(String memberNo) throws GeneralSecurityException, IOException {
        saveString(MEMBER_NO, memberNo);
    }

    public String getMemberNo() throws GeneralSecurityException, IOException {
        return loadString(MEMBER_NO, null);
    }

    public void saveEmail(String email) throws GeneralSecurityException, IOException {
        saveString(EMAIL, email);
    }

    public String getEmail() throws GeneralSecurityException, IOException {
        return loadString(EMAIL, null);
    }

    public void saveFirstName(String firstName) throws GeneralSecurityException, IOException {
        saveString(FIRST_NAME, firstName);
    }

    public String getFirstName() throws GeneralSecurityException, IOException {
        return loadString(FIRST_NAME, null);
    }

    public void saveMiddleName(String middleName) throws GeneralSecurityException, IOException {
        saveString(MIDDLE_NAME, middleName);
    }

    public String getMiddleName() throws GeneralSecurityException, IOException {
        return loadString(MIDDLE_NAME, null);
    }

    public void saveLastName(String lastName) throws GeneralSecurityException, IOException {
        saveString(LAST_NAME, lastName);
    }

    public String getLastName() throws GeneralSecurityException, IOException {
        return loadString(LAST_NAME, null);
    }

    public void saveTransactionLimit(String transactionLimit) throws GeneralSecurityException, IOException {
        saveString(TRANSACTION_LIMIT, transactionLimit);
    }

    public String getTransactionLimit() throws GeneralSecurityException, IOException {
        return loadString(TRANSACTION_LIMIT, "100000");
    }


    public void saveAccountNumber(String accountNumber) throws GeneralSecurityException, IOException {
        saveString(ACCOUNT_NUMBER, accountNumber);
    }

    public String getAccountNumber() throws GeneralSecurityException, IOException {
        return loadString(ACCOUNT_NUMBER, null);
    }

    public void saveMPIN(String mPin) throws GeneralSecurityException, IOException {
        saveString(M_PIN, mPin);
    }

    public String getMPIN() throws GeneralSecurityException, IOException {
        return loadString(M_PIN, null);
    }

    public void saveMID(String mID) throws GeneralSecurityException, IOException {
        saveString(M_ID, mID);
    }

    public String getMID() throws GeneralSecurityException, IOException {
        return loadString(M_ID, null);
    }

    public void saveBiometric(String biometric) throws GeneralSecurityException, IOException {
        saveString(BIOMETRIC, biometric);
    }

    public String getBiometric() throws GeneralSecurityException, IOException {
        return loadString(BIOMETRIC, null);
    }
}
