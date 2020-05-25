package com.ellen.dhcsqlitelibrary.table.exception;

public class JsonNoCanFormatException extends RuntimeException {


    private String errorMessage;

    public JsonNoCanFormatException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
