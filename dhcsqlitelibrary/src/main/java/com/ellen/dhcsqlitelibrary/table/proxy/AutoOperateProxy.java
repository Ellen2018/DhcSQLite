package com.ellen.dhcsqlitelibrary.table.proxy;

import android.text.TextUtils;

import com.ellen.dhcsqlitelibrary.table.exception.NoMajorKeyException;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.Delete;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.SearchByMajorKey;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.Search;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.TotalSql;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.Update;
import com.ellen.dhcsqlitelibrary.table.annotation.auto.Value;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoOperateProxy implements InvocationHandler {

    private ZxyTable zxyTable;

    public AutoOperateProxy(ZxyTable zxyTable) {
        this.zxyTable = zxyTable;
    }

    private Map<String, Object> getArgValue(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>();
        Annotation[][] annotations = method.getParameterAnnotations();
        if(annotations.length > 0){
            for(int i= 0;i<annotations.length;i++){
                Value value = (Value) annotations[i][0];
                map.put(value.value(), args[i]);
            }
        }
        return map;
    }

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
            if(orderSql != null){
                orderSql = newSql(orderSql,method,args);
            }
            return zxyTable.search(newSql(whereSql, method, args), orderSql);
        }
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null) {
            String deleteSql = delete.value();
            zxyTable.delete(newSql(deleteSql, method, args));
            return null;
        }
        TotalSql totalSql = method.getAnnotation(TotalSql.class);
        if (totalSql != null) {
            String totalSqlString = totalSql.sql();
            boolean isReturnValue = totalSql.isReturnValue();
            if(isReturnValue){
                //具有返回值
                String sql = newSql(totalSqlString, method, args);
                return zxyTable.searchDataBySql(sql);
            }else {
                //不具有返回值
                String sql = newSql(totalSqlString, method, args);
                zxyTable.exeSql(sql);
                return null;
            }
        }
        Update update = method.getAnnotation(Update.class);
        if (update != null) {
            String valueSql = update.valueSql();
            String whereSql = update.whereSql();
            valueSql = newSql(valueSql,method,args);
            whereSql = newSql(whereSql,method,args);
            StringBuilder stringBuilder = new StringBuilder("UPDATE "+ zxyTable.getTableName()+" SET ");
            stringBuilder.append(valueSql);
            stringBuilder.append(" WHERE ");
            stringBuilder.append(whereSql);
            stringBuilder.append(";");
            String updateSql  = stringBuilder.toString();
            zxyTable.exeSql(updateSql);
            return null;
        }
        SearchByMajorKey searchByMajorKey = method.getAnnotation(SearchByMajorKey.class);
        if(searchByMajorKey != null){
            String whereSql = searchByMajorKey.whereSql();
            String orderSql = searchByMajorKey.orderSql();
            if(orderSql != null && orderSql.equals("")){
                orderSql = null;
            }
            if(orderSql != null){
                orderSql = newSql(orderSql,method,args);
            }
            String majorKeyName = zxyTable.getMajorKeyName();
            if(!TextUtils.isEmpty(majorKeyName)) {
                if(whereSql.contains("{}")) {
                    whereSql = whereSql.replace("{}", zxyTable.getMajorKeyName());
                }else {
                    whereSql = zxyTable.getMajorKeyName() + " "+whereSql;
                }
            }else {
                throw new NoMajorKeyException("没有主键,无法根据主键查询数据!");
            }
            return zxyTable.search(newSql(whereSql,method,args),orderSql);
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
