package com.ellen.dhcsqlitelibrary.table.operate.search;

import java.util.List;

public interface Search<T> {
    List<T> search(String whereSQL, String orderSQL);
    List<T> getAllData();
    List<T> getAllData(String orderSql);
    List<T> searchDataBySql(String sql);
    boolean isContainsByMajorKey(T t);
    T getDataByMajorKey(Object value);

    /**
     * 判断表是否存在
     *
     * @return
     */
    boolean isExist();
}
