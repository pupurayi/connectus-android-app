package com.connectus.mobile.api.dto.transaction;

import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.database.SharedPreferencesManager;

import java.util.Date;
import java.util.HashMap;

import static com.connectus.mobile.utils.LocalStrings.CHANNEL;
import static com.connectus.mobile.utils.LocalStrings.WALLET;

public class TransactionUtil {
    public static TransactionDto prepareTransaction(SharedPreferencesManager sharedPreferencesManager,
                                                    String transactionType,
                                                    String extendedType, String extendedTranType) {
        ProfileDto profile = sharedPreferencesManager.getProfile();
        String msisdn = profile.getMsisdn();
        TransactionDto transactionDto = new TransactionDto(
                CHANNEL,
                transactionType,
                msisdn, new TransactionDtoAccount(WALLET, msisdn), extendedType
        );
        transactionDto.setAdditionalData(new HashMap<>());
        transactionDto.getAdditionalData().put(AdditionalDataTags.EXTENDED_TRAN_TYPE, extendedTranType);
        transactionDto.setCreated(new Date());
        return transactionDto;
    }
}
