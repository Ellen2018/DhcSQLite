package com.ellen.dhcsqlitelibrary.table.exception;

public class NoCanSaveToSqlException extends RuntimeException {


    private String errorMessage;

    public NoCanSaveToSqlException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
