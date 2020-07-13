package com.ellen.dhcsqlitelibrary.table.impl;

import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;

public class CommonSetting {

    private JsonFormat jsonFormat = null;
    private JsonLibraryType jsonLibraryType = JsonLibraryType.Gson;
    private boolean isMultiThreadSafety = false;

    public JsonFormat getJsonFormat() {
        return jsonFormat;
    }

    public void setJsonFormat(JsonFormat jsonFormat) {
        this.jsonFormat = jsonFormat;
    }

    public JsonLibraryType getJsonLibraryType() {
        return jsonLibraryType;
    }

    public void setJsonLibraryType(JsonLibraryType jsonLibraryType) {
        this.jsonLibraryType = jsonLibraryType;
    }

    public boolean isMultiThreadSafety() {
        return isMultiThreadSafety;
    }

    public void setMultiThreadSafety(boolean multiThreadSafety) {
        isMultiThreadSafety = multiThreadSafety;
    }
}
