
# 1.简介

&emsp;&emsp;因为笔者考虑掉导入此框架后代码简洁优雅性问题，所以加入了一个动态代理的机制，这个接口一方面为了让代码更加简洁，另一方面让您的数据库操纵业务化，那么怎样业务化呢？就是笔者提供了一系列注解，帮您完成很多数据库表增删改查乃至更多的操作问题，而且代码写起来非常简洁。

# api介绍  

    public interface MyAutoDesignOperate extends AutoDesignOperate {

        /**
         * 查找
         * @return
         */
        @Search(whereSql = "your_age > @ageValue and my_name = '@name'",orderSql = "id ASC")
        List<Student> getSearchList1(@Value("ageValue") int ageValue, @Value("name") String name);

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

        /**
         * 根据主键查询数据
         * @param id_value
         * @return
         */
        @SearchByMajorKey(whereSql = "> @id_value")
        List<Student> searchByMajorKey(@Value("id_value") int id_value);

    } 

使用方法:  

        ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
        SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();
        StudentTable studentTable = new StudentTable(sqLiteDatabase);

        //获取代理对象
        MyAutoDesignOperate myAutoDesignOperate = studentTable.getAutoDesignOperate();
        

        //以下是通过动态代理直接调用，无需实现这个接口
        List<Student> studentList1 = myAutoDesignOperate.getSearchList1(3,"Ellen2018");
        List<Student> studentList2 = myAutoDesignOperate.getSearchList2();
        myAutoDesignOperate.deleteData(3,"Ellen2018");
        List<Student> studentList3 = myAutoDesignOperate.search();
        myAutoDesignOperate.update("新的名字","Ellen2018");
        List<Student> studentList4 = myAutoDesignOperate.searchByMajorKey(0);


这个接口就是笔者在[ZxyTable](https://github.com/Ellen2018/DhcSQLite/blob/master/ZxyTable.md)中提到的元操作接口AutoDesignOperate，这么写有什么好处呢，就是让您的关于数据库操作的业务代码非常清晰，维护性非常好，任性修改，这也是笔者设计它的初衷。下面笔者来着重讲一讲元操作接口AutoDesignOperate中的注解的用法:

- @Search

&emsp;&emsp;此注解用来完成搜素功能的，因此您使用的方法的返回值一定是个List集合，他需要传递2个String类型的参数值，一个参数值为whereSql(定义搜素条件)，另外一个就是orderSql(定义结果的排序方式)，whereSql和orderSql必须以Sql语句的方式传递，order不传值的时候啊，它不具备排序效果,如果您的sql语句中需要有变动的值，那么您可以结合@Value注解加上 “@值的名字” 进行动态sql语句定义，什么意思呢？比如我现在有个需求就是查询到年龄大于某个值的数据，查询的结果按照id进行升序，那么您的方法应该这么定义:

        @Search(whereSql = "your_age > @ageValue and my_name = '@name'",orderSql = "id ASC")
        List<Student> getSearchList1(@Value("ageValue") int ageValue, @Value("name") String name);

&emsp;&emsp;您可以看到在whereSql中有个@ageValue,它的意思就是会被方法参数列表的int ageValue所取代，因为这个int ageValue有一个@Value注解，且它的名字为"ageValue"，用过Retrofit库的人应该这里一下子就能看懂。比如现在我这样调用getSearchList1(3,"周杰伦")，那么它执行的SQL语句为："SELECT * FROM Student WHERE your_age > 3 and my_name = '周杰伦' ORDER BY id ASC;"，如果我现在不想它按照id 进行排序，我想传递一个String的参数，让它按照这个传递参数值对应的字段进行排序呢，那么您应该这么改造您的方法:  



        @Search(whereSql = "your_age > @ageValue and my_name = '@name'",orderSql = "@sortName ASC")
        List<Student> getSearchList1(@Value("ageValue") int ageValue, @Value("name") String name,@Value("sortName") String sortName);

你在调用时:

        //相当于执行了：SELECT * FROM Student WHERE your_age > 3 and my_name = '周杰伦' ORDER BY your_age ASC;
        myAutoDesignOperate.getSearchList1(3,"周杰伦","your_age");

        //相当于执行了：SELECT * FROM Student WHERE your_age > 3 and my_name = '周杰伦' ORDER BY address ASC;
        myAutoDesignOperate.getSearchList1(3,"周杰伦","address");

- @SearchByMajorKey

&emsp;&emsp;这个注解就是用来和主键相关的查询，它的使用方式除了具备@Serach所有的功能之外，他还有在字符串中以"{}"替代主键字段名，啥啥意思呢，我们来个例子： 

        @SearchByMajorKey(whereSql = "{} > @id_value")
        List<Student> searchByMajorKey(@Value("id_value") int id_value);

调用时

        //相当于执行了SELECT * FROM Student WHERE id > 6;
        myAutoDesignOperate.searchByMajorKey(6);

从代码中可以看出"{}"被"id"进行了取代，因为主键的字段名为"id",如果您的条件仅有一个且与主键相关，那么您连"{}"这个也不需要写，示例代码如下:  

        @SearchByMajorKey(whereSql = "> @id_value")
        List<Student> searchByMajorKey(@Value("id_value") int id_value);

这和上面是一样的效果，注意下面这种方式是错误的：

        //这是一种错误的定义方式，因为“{}”不写的前提是您的条件仅仅且一个
        @SearchByMajorKey(whereSql = "> @id_value && {} < 27")
        List<Student> searchByMajorKey(@Value("id_value") int id_value);

- @Update  

&emsp;&emsp;从名字上来看它就是用来更新数据的，它规定的返回值为null(笔者后期可以改成int类型来记录您修改的数据个数,后面版本再修改吧),代码示例:

        @Update(valueSql = "name = '@newName'", whereSql = "sid = @sid")
        void updateStudentNameById(@Value("sid") int sid, @Value("newName") String newName);

调用时:  

        //相当于执行了：UPDATE Student SET  name = 'Ellen2020' WHERE sid = 3;
        myAutoDesignOperate.updateStudentNameById(3,"Ellen2020");

- @Delete 

&emsp;&emsp;从名字上看就可以看出它是用来删除数据的，它规定的返回值也是void(笔者后期可以改成int类型来记录您删除的数据个数,后面版本再修改吧),代码示例:

        /**
        * 删除
         */
        @Delete("id = @id and my_name = '@name'")
        void deleteData(@Value("id") int id,@Value("name") String name);  

调用时: 

        //相当于执行了：DELETE FROM Student WHERE id = 3 and my_name = 'Ellen2018';
        myAutoDesignOperate.deleteData(3,"Ellen2018");

- @TotalSql  

&emsp;&emsp;这个注解是用来完成整段sql执行的，也就是说通过它，您可以完成任何sql语句的执行，能能和@Value配合使用，除此之外呢需要注意的是执行sql语句无非是有2种结果，第一种就是无返回值的sql语句执行，第二种就是有返回值的sql语句执行，所以在此注解种笔者封装了一个isReturnValue(默认值为false)的参数去记录是否有返回值，如果您将它设置为true,那么你最好是接收一个List的返回值，否则为false的话，返回值为void即可，下面来代码演示一下：  

        @TotalSql(sql = "SELECT * FROM Student WHERE my_name = '@name'",isReturnValue = true)
        List<Student> getStudentByName(@Value("name") String name);

        @TotalSql(sql = "DELETE FROM Student WHERE id = @id_value and my_name = '@name';")
        void delete(@Value("id_value")int id,@Value("name") String name);

调用时:  

        //相当于执行了:SELECT * FROM Student WHERE my_name = 'Ellen2018'
        myAutoDesignOperate.getStudentByName("Ellen2018");

        //相当于执行了:DELETE FROM Student WHERE id = 3 and my_name = '周杰伦';
        myAutoDesignOperate.delete(3,"周杰伦");

- @Value 

&emsp;&emsp;这个注解的作用就不用笔者解释了吧，看懂上面就知道了它的用法。
