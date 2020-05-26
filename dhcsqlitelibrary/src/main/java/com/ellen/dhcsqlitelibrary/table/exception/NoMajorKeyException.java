package com.ellen.dhcsqlitelibrary.table.exception;

public class NoMajorKeyException extends RuntimeException {

    private String errorMessage;

    public NoMajorKeyException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
