package com.ellen.dhcsqlitelibrary.table.Proxy;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ellen.dhcsqlitelibrary.table.exception.NoPrimaryKeyException;
import com.ellen.dhcsqlitelibrary.table.operate.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.SearchByMajorKey;
import com.ellen.dhcsqlitelibrary.table.operate.TotalSearchSql;
import com.ellen.dhcsqlitelibrary.table.operate.Search;
import com.ellen.dhcsqlitelibrary.table.operate.TotalSql;
import com.ellen.dhcsqlitelibrary.table.operate.TotalUpdateSql;
import com.ellen.dhcsqlitelibrary.table.operate.Update;
import com.ellen.dhcsqlitelibrary.table.operate.Value;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyTable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoOperateProxy implements InvocationHandler {

    private ZxyTable zxyReflectionTable;

    public AutoOperateProxy(ZxyTable zxyTable) {
        this.zxyReflectionTable = zxyTable;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Map<String, Object> getArgValue(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Value value = parameters[i].getAnnotation(Value.class);
                map.put(value.value(), args[i]);
            }
        }
        return map;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String newSql(String sql, Method method, Object[] args) {
        if (args != null && args.length > 0) {
            Map<String, Object> valueMap = getArgValue(method, args);
            Set<String> stringSet = valueMap.keySet();
            for (String name : stringSet) {
                sql = sql.replace("@" + name, valueMap.get(name).toString());
            }
        }
        return sql;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //查询
        Search search = method.getAnnotation(Search.class);
        if (search != null) {
            String whereSql = search.whereSql();
            String orderSql = search.orderSql();
            if(orderSql != null && orderSql.equals("")){
                orderSql = null;
            }
            return zxyReflectionTable.search(newSql(whereSql, method, args), orderSql);
        }
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null) {
            String deleteSql = delete.value();
            zxyReflectionTable.delete(newSql(deleteSql, method, args));
            return null;
        }
        TotalSearchSql totalSearchSql = method.getAnnotation(TotalSearchSql.class);
        if (totalSearchSql != null) {
            String totalSearchSqlString = totalSearchSql.value();
            return zxyReflectionTable.searchDataBySql(newSql(totalSearchSqlString, method, args));
        }
        TotalSql totalSql = method.getAnnotation(TotalSql.class);
        if (totalSql != null) {
            String totalSqlString = totalSql.value();
            zxyReflectionTable.exeSQL(newSql(totalSqlString, method, args));
            return null;
        }
        Update update = method.getAnnotation(Update.class);
        if (update != null) {
            String valueSql = update.valueSql();
            String whereSql = update.whereSql();
            valueSql = newSql(valueSql,method,args);
            whereSql = newSql(whereSql,method,args);
            StringBuilder stringBuilder = new StringBuilder("UPDATE "+zxyReflectionTable.getTableName()+" SET ");
            stringBuilder.append(valueSql);
            stringBuilder.append(" WHERE ");
            stringBuilder.append(whereSql);
            stringBuilder.append(";");
            String updateSql  = stringBuilder.toString();
            zxyReflectionTable.exeSQL(updateSql);
            return null;
        }
        TotalUpdateSql totalUpdateSql = method.getAnnotation(TotalUpdateSql.class);
        if(totalUpdateSql != null){
            String totalUpdateSqlString = totalUpdateSql.value();
            String updateSql  =newSql(totalUpdateSqlString,method,args);
            Log.e("Ellen2018","update语句:"+updateSql);
            zxyReflectionTable.exeSQL(updateSql);
            return null;
        }
        SearchByMajorKey searchByMajorKey = method.getAnnotation(SearchByMajorKey.class);
        if(searchByMajorKey != null){
            String whereSql = searchByMajorKey.whereSql();
            String orderSql = searchByMajorKey.orderSql();
            if(orderSql != null && orderSql.equals("")){
                orderSql = null;
            }
            String majorKeyName = zxyReflectionTable.getMajorKeyName();
            if(!TextUtils.isEmpty(majorKeyName)) {
                if(whereSql.contains("{}")) {
                    whereSql = whereSql.replace("{}", zxyReflectionTable.getMajorKeyName());
                }else {
                    whereSql = zxyReflectionTable.getMajorKeyName() + " "+whereSql;
                }
            }else {
                throw new NoPrimaryKeyException("没有主键,无法根据主键查询数据!");
            }
            return zxyReflectionTable.search(newSql(whereSql,method,args),orderSql);
        }
        return null;
    }

    public static <T> T newMapperProxy(Class<T> mapperInterface, ZxyTable zxyTable) {
        ClassLoader classLoader = mapperInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{mapperInterface};
        AutoOperateProxy proxy = new AutoOperateProxy(zxyTable);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

}
