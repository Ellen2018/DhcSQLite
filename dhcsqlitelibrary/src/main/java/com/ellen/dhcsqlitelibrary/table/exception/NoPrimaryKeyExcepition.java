package com.ellen.dhcsqlitelibrary.table.exception;

public class NoPrimaryKeyExcepition extends RuntimeException {

    private String errorMessage;

    public NoPrimaryKeyExcepition(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
