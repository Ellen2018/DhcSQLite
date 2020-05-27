package com.ellen.dhcsqlite.sql;

import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.operate.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.operate.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.Search;
import com.ellen.dhcsqlitelibrary.table.operate.SearchByMajorKey;
import com.ellen.dhcsqlitelibrary.table.operate.TotalSql;
import com.ellen.dhcsqlitelibrary.table.operate.Update;
import com.ellen.dhcsqlitelibrary.table.operate.Value;

import java.util.List;

public interface MyAutoDesignOperate extends AutoDesignOperate {

    /**
     * 查找
     * @return
     */
    @Search(whereSql = "your_age > @ageValue and my_name = '@name'",orderSql = "@sortName ASC")
    List<Student> getSearchList1(@Value("ageValue") int ageValue, @Value("name") String name,@Value("sortName") String sortName);

    /**
     * 查找且排序
     * @return
     */
    @Search(whereSql = "id > 3",orderSql = "id DESC")
    List<Student> getSearchList2();

    /**
     * 删除
     */
    @Delete("id = @id and my_name = '@name'")
    void deleteData(@Value("id") int id,@Value("name") String name);

    @Update(valueSql = " my_name = '@newName'",whereSql = "my_name = '@oldName'")
    void update1(@Value("newName") String newName,@Value("oldName") String oldName);

    /**
     * 根据主键查询数据
     * @param id_value
     * @return
     */
    @SearchByMajorKey(whereSql = "{} > @id_value")
    List<Student> searchByMajorKey(@Value("id_value") int id_value);

    @TotalSql(sql = "SELECT * FROM Student WHERE my_name = '@name'",isReturnValue = true)
    List<Student> getStudentByName(@Value("name") String name);

    @TotalSql(sql = "DELETE FROM Student WHERE id = @id_value and my_name = '@name';")
    void delete(@Value("id_value")int id,@Value("name") String name);

}

