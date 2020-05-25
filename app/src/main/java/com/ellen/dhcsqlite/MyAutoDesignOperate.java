package com.ellen.dhcsqlite;

import com.ellen.dhcsqlitelibrary.table.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.operate.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.Search;
import com.ellen.dhcsqlitelibrary.table.operate.TotalSearchSql;
import com.ellen.dhcsqlitelibrary.table.operate.TotalUpdateSql;
import com.ellen.dhcsqlitelibrary.table.operate.Value;

import java.util.List;

public interface MyAutoDesignOperate extends AutoDesignOperate {

    /**
     * 查找
     * @return
     */
    @Search(whereSql = "your_age > @ageValue and my_name = '@name'",orderSql = "id ASC")
    List<Student> getSearchList1(@Value("ageValue") int ageValue,@Value("name") String name);

    /**
     * 查找且排序
     * @return
     */
    @Search(whereSql = "id > 80",orderSql = "id DESC")
    List<Student> getSearchList2();

    /**
     * 删除
     */
    @Delete("id = @id and my_name = '@name'")
    void deleteData(@Value("id") int id,@Value("name") String name);

    @TotalSearchSql("SELECT * FROM Student WHERE your_age > 50;")
    List<Student> search();

    @TotalUpdateSql("UPDATE Student SET my_name = '@newName' WHERE my_name = '@oldName';")
    void update(@Value("newName") String newName,@Value("oldName") String oldName);

}

