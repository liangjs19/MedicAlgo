package com.mbaxajl3.medicalgo;

public interface Callback {
    void onSuccess(Object value);

    void onError(String result) throws Exception;
}