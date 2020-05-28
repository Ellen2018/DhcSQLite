package com.ellen.dhcsqlitelibrary.table.exception;

public class BoolNoCanSaveException extends RuntimeException {


    private String errorMessage;

    public BoolNoCanSaveException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
