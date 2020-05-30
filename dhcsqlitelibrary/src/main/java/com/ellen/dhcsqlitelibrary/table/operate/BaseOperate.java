package com.ellen.dhcsqlitelibrary.table.operate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ellen.dhcsqlitelibrary.table.helper.CursorHelper;
import com.ellen.dhcsqlitelibrary.table.helper.ReflectHelper;
import com.ellen.dhcsqlitelibrary.table.impl.TotalListener;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyLibrary;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.type.BasicTypeSupport;
import com.ellen.dhcsqlitelibrary.table.type.DataStructureSupport;
import com.ellen.dhcsqlitelibrary.table.type.Intercept;
import com.ellen.dhcsqlitelibrary.table.type.ObjectTypeSupport;
import com.ellen.dhcsqlitelibrary.table.type.TypeSupport;
import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseOperate<T> extends ZxySqlCreate {

    protected SQLiteDatabase db;

    protected Class dataClass;
    protected List<SQLField> sqlFieldList;
    protected HashMap<SQLField, Field> sqlNameMap;
    protected CursorHelper<T> cursorHelper;
    protected ReflectHelper<T> reflectHelper = null;
    protected DebugListener debugListener;

    //数据类型支持
    protected BasicTypeSupport basicTypeSupport;
    protected DataStructureSupport dataStructureSupport;
    protected ObjectTypeSupport objectTypeSupport;

    protected List<Intercept> interceptList;
    private static TotalListener totalListener;
    private ZxyTable zxyTable;

    public static void setTotalListener(TotalListener totalListener) {
        BaseOperate.totalListener = totalListener;
    }

    public void setDebugListener(DebugListener debugListener) {
        this.debugListener = debugListener;
    }

    public void addIntercept(Intercept intercept) {
        if (interceptList == null) {
            interceptList = new ArrayList<>();
        }
        interceptList.add(0, intercept);
    }

    public void removeIntercept(Intercept intercept){
        if(interceptList == null){
            return;
        }else {
            interceptList.remove(intercept);
        }
    }

    public BaseOperate(SQLiteDatabase db,ZxyTable zxyTable) {
        this.db = db;
        this.zxyTable = zxyTable;
    }

    protected void exeSql(String sql) {
        if (debugListener != null) {
            debugListener.exeSql(sql);
        }
        if(totalListener != null){
            totalListener.exeSql(zxyTable.getTableName(),sql);
        }
        db.execSQL(sql);
    }

    protected List<T> search(String sql) {
        if (debugListener != null) {
            debugListener.exeSql(sql);
        }
        if(totalListener != null){
            totalListener.exeSql(zxyTable.getTableName(),sql);
        }
        return getDataByCursor(db.rawQuery(sql, null));
    }

    private List<T> getDataByCursor(Cursor cursor) {
        List<T> dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            T t = null;
            try {
                t = reflectHelper.getT(dataClass);
                for (int i = 0; i < sqlFieldList.size(); i++) {
                    Field field = sqlNameMap.get(sqlFieldList.get(i));
                    String sqlDataType = null;
                    TypeSupport typeSupport = getTypeSupport(field);
                    sqlDataType = typeSupport.setSQLiteType(field).getTypeString();
                    Object value = cursorHelper.readValueFromCursor(cursor, field, sqlFieldList.get(i), sqlDataType);
                    field.set(t, typeSupport.toObj(field, value));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            dataList.add(t);
        }
        if (cursor != null) {
            cursor.close();
        }
        return dataList;
    }

    protected TypeSupport getTypeSupport(Field field) {
        if (interceptList != null) {
            TypeSupport typeSupportResult = null;
            for (TypeSupport typeSupport : interceptList) {
                if (typeSupport.isType(field)) {
                    typeSupportResult = typeSupport;
                    break;
                }
            }
            if(typeSupportResult != null) {
                return typeSupportResult;
            }
        }
        if (basicTypeSupport.isType(field)) {
            return basicTypeSupport;
        }
        if (dataStructureSupport.isType(field)) {
            return dataStructureSupport;
        }
        if (objectTypeSupport.isType(field)) {
            return objectTypeSupport;
        }
        return null;
    }


}
