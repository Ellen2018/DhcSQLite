package com.ellen.dhcsqlitelibrary.table.exception;

public class NoPrimaryKeyException extends RuntimeException {

    private String errorMessage;

    public NoPrimaryKeyException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
