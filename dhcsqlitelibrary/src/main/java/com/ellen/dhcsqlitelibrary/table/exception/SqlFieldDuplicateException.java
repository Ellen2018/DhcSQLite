package com.ellen.dhcsqlitelibrary.table.exception;

public class SqlFieldDuplicateException extends RuntimeException{

    private String errorMessage;

    public SqlFieldDuplicateException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
