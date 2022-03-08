package com.connectus.mobile.common;

import android.util.Base64;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class Validate {
    // TODO Check if its valid INTERNATIONAL number
    public static boolean isValidMobileNumber(String phoneNumber) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
            return phoneNumberUtil.isPossibleNumberForType(phone, PhoneNumberUtil.PhoneNumberType.MOBILE);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static void isJwtValid(String token) {
        String[] split = token.split("\\.");
        byte[] decodedBytes = Base64.decode(split[1], Base64.URL_SAFE);
        // TODO Decode JWT
    }
}
