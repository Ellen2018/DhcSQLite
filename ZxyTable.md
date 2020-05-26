
# 1.简介  

&emsp;&emsp;你使用此库的第二步就是创建一个ZxyTable类，在继承它的时候需要传入2个泛型，第一个泛型指的是映射的bean类型，第二个参数就是您自定义的元操作角色对应的接口[**AutoDesignOperate**](https://github.com/Ellen2018/DhcSQLite/blob/master/AutoDesignOperate.md)。
这个类能帮你做到很多操作表相关的事情也是您最需要仔细了解的一个类，里面封装了很多api,增删改查功能非常健全，此外您还可以自定义AutoDesignOperate来自定义您的元操作(更多的是业务操作)。

# 2.api介绍

## 2.1 Bean类声明时注解介绍

bean类代码：  

    public class Student {

        //主键
        @MajorKey(isAutoIncrement = true)
        private int id;
        @DhcSqlFieldName(sqlFieldName = "my_name") //映射数据库中字段名字为my_name
        private String name;
        @DhcSqlFieldName(sqlFieldName = "your_age")
        private int age;
        private String phoneNumber;
        private String address;
        @Ignore //不映射这个属性到数据库中
        private String ingoreString;
        private boolean isMan;
        @NoBasicType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
        @Operate(operate = OperateEnum.JSON)
        @DhcSqlFieldName(sqlFieldName = "your_father")
        private Father father;
        @DataStructure //表示这个属性是数据类型属性，需要用注解区分，才能正确的进行json映射，否则会报错
        @DhcSqlFieldName(sqlFieldName = "Dads")
        private Father[] fathers;

        ......

    }

关于这些注解的说明

- @Ignore 
 
&emsp;&emsp;此属性不映射到数据库表中。

- @DhcSqlFieldName 
 
&emsp;&emsp;需要传入一个String类型的值，此注解的作用就是设置该属性映射到数据库中的字段名。当你不写的时候，映射到数据库中的字段名为属性本身的名字，例如int age它在数据库表中的字段名为age,如果你为它指定@DhcSqlFieldName(sqlFieldName = "age\_student"),那么数据库表中的字段会被映射为age\_student。 

- @DataStructure 
 
&emsp;&emsp;这个注解专门用于映射数组,List,Set,Map,Stack数据结构类型数据的，目前仅仅支持这些,如果您想要扩展其它数据结构，您需要了解原理进行修改，数据结构属性的数据框架自动帮您以JSON的方式进行存储。注意的是你可以不需要添加此注解，框架内部自动判断当前属性是否为数据结构类型，如果是数据结构类型，则自行按照数据结构类型进行处理。还需要注意的是存的操作基本又框架自动帮您完成，但是取的操作您需要在ZxyTable中实现resumeDataStructure方法进行去恢复这个数据结构的数据。

- @MajorKey 
 
&emsp;&emsp;这个注解的作用就是用来指定主键的,它可以传递一个booleane类型的参数进去，这个参数代表主键是否自增，如果不传参数，默认值为false。注意当您在bean类中指定了多个@MajorKey就会报异常。

- @Operate 
 
&emsp;&emsp;此注解为不是基本类型数据(Sttring除外)而使用的，比如上述代码中的father属性，因为框架本身不知道您要以怎样的方式去存储它，所以此注解的作用就是告诉框架以怎样的方式来存储它，笔者封装了2种方式，一种是JSON方式,一种是VALUE存储方式，json方式我无需解释，就是将它以json进行映射，value的方式我必须要着重讲一讲，它是通过反射将属性中的某个属性进行存储，比如Father中有个id的属性，那么如你为它指定注解 @Operate(operate = OperateEnum.VALUE,valueName = "id")，那么映射的时候father最终会以id的值进行存储，恢复数据的时候father中只有id是读取的，father其他的属性值由于没有映射到数据库表中，因此其他属性皆为初始化状态，目前VALUE方式仅仅支持基本类型和String类型，代码示例如下：  

Father类： 

    public class Father {
 
        private String name;
        //@Operate(operate = OperateEnum.VALUE,valueName = "id")保存的是这个id的数据
        private String id;

        ......
    }

@Operate注解的使用:

    //以JSON方式完成存储
    @Operate(operate = OperateEnum.JSON)
    private Father father;

    //以VALUE方式完成存储,这里必须指定valueName
    //还需要注意的是这种方式下必须指定@SqlType
    @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
    @Operate(operate = OperateEnum.VALUE,valueName = "id")
    private Father father2;

- @SqlType 
 
&emsp;&emsp;此注解与@Operate注解搭配使用，它的作用就是告诉框架,这个属性映射到数据库表中的字段类型以及数据长度，当数据长度为0或者负值时，长度不受限制，当您不传入length时它默认为-1，注意如果您将它用于基本类型以及String，数据结构类型的属性上没有任何作用。示例代码如下

    //指定father2映射到数据库字段的类为TEXT类型，长度为100
    @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
    @Operate(operate = OperateEnum.VALUE,valueName = "id")
    private Father father2;

&emsp;&emsp;SQLFieldTypeEnum 有哪些类型?无须笔者啰嗦，对数据库很懂的您懂的。  

    INTEGER("integer"),
    BIG_INT("bigint"),
    TEXT("text"),
    REAL("real"),
    BLOB("blob"),
    NUMERIC("numeric"),
    DATE("date");

- @NotNull 

&emsp;&emsp;它的左右就是映射到数据库中的字段添加一个不能为nll的特性。意思就是这个数据不能填塞nll,否则会报错。

## 2.2 声明ZxyTable类介绍
 
代码：

    public class StudentTable extends ZxyTable<Student, MyAutoDesignOperate> {


        /**
         *
         * 调用此构造器表名为类名：Student
         * db：SQLiteDatabase对象，可以通过ZxyLibrary传入
         * dataClass:bean类对应的字节码对象: Student.calss
         * autoClass:元操作的接口字节码对象：MyAutoDesignOperate.class
         */ 
        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, Class<? extends AutoDesignOperate> autoClass) {
            super(db, dataClass, autoClass);
        }

        /**
         *
         * 调用此构造器表名传入的：autoTableName
         * db：SQLiteDatabase对象，可以通过ZxyLibrary传入
         * dataClass:bean类对应的字节码对象: Student.calss
         * autoClass:元操作的接口字节码对象：MyAutoDesignOperate.class
         */ 
        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, String autoTableName, Class<? extends AutoDesignOperate> autoClass) {
            super(db, dataClass, autoTableName, autoClass);
        }

        /**
         * 设置属性对应的数据库字段的类型
         * 如果没有特殊的更改，此方法无须重写
         *
         * 但是笔者有特殊的要求，就是将isMan字段在数据库中存"男' & "女" 的需求
         * 那么这个地方我就只能将isMan属性的字段映射为数据库中TEXT类型，并且数据位数调整为1位
         *
         * @param classFieldName
         * @param typeClass
         * @return
         */
        @Override
        protected SQLFieldType getSqlFieldType(String classFieldName, Class typeClass) {
            if(classFieldName.equals("isMan")){
                //将isMan的boolean映射为TEXT类型，且长度为1位
                return new SQLFieldType(SQLFieldTypeEnum.TEXT,1);
            }else {
                return super.getSqlFieldType(classFieldName,typeClass);
            }
        }

        /**
         * boolean类型值转化为数据库中的存储
         * 此方法默认情况下无须重写
         *
         * 如果你的bean类中没有boolean类型的存储，此方法返回null即可
         *
         * 如果有:
         * 默认的没有实现getSqlFieldType方法将它映射成你想要的类型的情况下:需要返回 0 和 1，或者 其他的int类型即可，只要不相同
         * 如果有实现getSqlFieldType方法并将它映射成你想要的类型的情况下:需要返回true和false存储的值，例如笔者这里返回"男" 和 "女"
         *
         * 这么实现有什么意义呢？为的是让数据库中的数据变得更加易懂，通常清空下没有这种需求，可万一有这个需求呢
         * @param classFieldName
         * @param value
         * @return
         */
        @Override
        protected Object setBooleanValue(String classFieldName, boolean value) {
            if(classFieldName.equals("isMan")) {
                if (value) {
                    return "男";
                } else {
                    return "女";
                }
            }else {
                //不要忘记了调用super.setBooleanValue否则可能出错(其它的映射不了转换值导致出错)
                return super.setBooleanValue(classFieldName,value);
            }
        } 

        /**
         * 注意，你映射的bean类必须要有空的构造器，否则就会映射失败
         * 其原因是FastJson无法映射到没有空构造器的bean类
         *
         *
         * JsonLibraryType.Gson --> 使用Gson进行json映射
         *
         * JsonLibraryType.FastJson --> 使用FastJson进行json映射
         *
         * 当你没有重写此方法时候，默认会使用Gson
         *
         * 后期还可以加入其它的json映射类型
         *
         * 非常注意:如果你的项目中没有导入Json解析库:Gson或者是FastJson,那么在映射的时候就会抛出JsonNoCanFormatException
         *  一旦出现这个异常，你需要导入Gson或者FastJson库即可解决这个异常
         *
         * @return
         */
        @Override
        protected JsonLibraryType getJsonLibraryType() {
            return JsonLibraryType.Gson;
        }

        /**
         * 将json恢复成成数据结构的形式
         *
         * @param classFieldName
         * @param json
         * @return
         */
        @Override
        protected Object resumeDataStructure(String  classFieldName, String json) {
          if(classFieldName.equals("fathers")){
                Type founderSetType = new TypeToken<List<Father>>() {}.getType();
                List<Father> fathers = new Gson().fromJson(json, founderSetType);
                Father[] fathers1 = new Father[fathers.size()];
                for(int i=0;i<fathers.size();i++){
                    fathers1[i] = fathers.get(i);
                }
                return fathers1;

            }
            return null;
        }
    }

如何使用?

    //先声明一个ZxyLibrary对象
    ZxyLibrary zxyLibrary = new AppLibrary(this, "数据库名", 1);
    SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();
    
    //再声明要给ZxyTable对象
    StudentTable StudentTable = new StudentTable(sqLiteDatabase, Student.class,MyAutoDesignOperate.class);

    //进行详细的数据库表的操作
    ......(通过StudentTable引用进行对表的操作)

&emsp;&emsp;在继承这个的类的时候，很多方法都不需要重写，但是如果你的bean类中存在比较复杂的属性，例如Father[]，List<Father>等数据结构类型的属性，那么就需要重写一些方法以便帮您完成映射，再比如如果您的boolean类型不想在数据库表中仅存储1或者0,比如isMan字段，您想存储为"男"或者"女"，您也需要对某些方法进行重写以达到目的，下面我对这些方法进行一一介绍:

- StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, Class<? extends AutoDesignOperate> autoClass)

&emsp;&emsp;调用此构造器它的表名为它绑定的bean类的类名。db为操作数据库的SQLiteDatabase，可由ZxyLibrary获取，也可以通过原生的SQLiteOpenHelper获取，dataClass为绑定的映射类的字节码对象，autoClass为元操作代理接口字节码对象，关于元操作代理接口使用请查阅[**AutoDesignOperate**](https://github.com/Ellen2018/DhcSQLite/blob/master/AutoDesignOperate.md)。

-  StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, String autoTableName, Class<? extends AutoDesignOperate> autoClass)

&emsp;&emsp;调用此构造器它的表名为传入的autoTableName。db为操作数据库的SQLiteDatabase，可由ZxyLibrary获取，也可以通过原生的SQLiteOpenHelper获取，dataClass为绑定的映射类的字节码对象，autoClass为元操作代理接口字节码对象，关于元操作代理接口使用请查阅[**AutoDesignOperate**](https://github.com/Ellen2018/DhcSQLite/blob/master/AutoDesignOperate.md)。  

- SQLFieldType getSqlFieldType(String classFieldName, Class typeClass)  

&emsp;&emsp;此方法的作用就是根据classFieldName以及typeClass来确定数据库映射的字段类型,默认清情况下无需重写。比如我想将isMan的数据库字段类型映射为TEXT且长度为1的类型，因为我需要存储"男"或者"女"，那么代码如下所示:

    protected SQLFieldType getSqlFieldType(String classFieldName, Class typeClass) {
        if(classFieldName.equals("isMan")){
            //将isMan的boolean映射为TEXT类型，且长度为1位
            return new SQLFieldType(SQLFieldTypeEnum.TEXT,1);
        }else {
            //不要忘记了调用super.getSqlFieldType否则就会出错(其它的映射不了导致出错)
            return super.getSqlFieldType(classFieldName,typeClass);
        }
    }

- Object setBooleanValue(String classFieldName, boolean value)

&emsp;&emsp;此方法作用就是将bean对象的boolean类型值转换为数据库中能存储的值,无需重写，默认情况下以int类型0(false),1(true)的方式保存如果您在getSqlFieldType方法中修改了某个字段的boolean类型映射的数据库字段类型，那么此处您也需要重写该方法的逻辑，例如：我现在要配合上面isMan的数据库字段类型映射为TEXT且长度为1的类型，那么代码如下： 

    protected Object setBooleanValue(String classFieldName, boolean value) {
        if(classFieldName.equals("isMan")) {
            if (value) {
                return "男";
            } else {
                return "女";
            }
        }else {
            //不要忘记了调用super.setBooleanValue否则可能出错(其它的映射不了转换值导致出错)
            return super.setBooleanValue(classFieldName,value);
        }
    } 

- JsonLibraryType getJsonLibraryType()

&emsp;&emsp;此方法的作用就是指定此框架使用的JSON库是哪种，因为此框架内部用到了json映射，因此需要指定使用何种方式的JSON库，目前仅支持2中JSON库：Gson(谷歌)和FastJson(阿里)，注意如果您的项目中没有导入Gson(谷歌)或者FastJson(阿里),那么当框架进行Json映射时就会抛出JsonNoCanFormatException的运行时异常，您无需担心，只要导入对应要使用的库即可，示例代码如下:  

    protected JsonLibraryType getJsonLibraryType() {
        //JsonLibraryType.FastJson; ->FastJson方式
        return JsonLibraryType.Gson; //Gson方式
    }

- Object resumeDataStructure(String  classFieldName, String json)

&emsp;&emsp;此方法的作用从名字上就可以看出，它是将数据库中的json数据转化为数据结构的，由于笔者不知道你要恢复的数据结构，比如您的属性为List<Father>,虽然笔者知道您的数据结构类型为List,但我无法知道你要将它恢复成ArrayList还是LikedList等，所以此处的逻辑交给您来完成比较好，逻辑也很简单，只需要根据classFieldName和json进行解析，解析成你想要的数据结构类型，然后进行返回即可。比如bean类属性中有个Father[] fathers的数组数据类型结构的数据，那么恢复的示例代码如下:  

    protected Object resumeDataStructure(String  classFieldName, String json) {
      if(classFieldName.equals("fathers")){
            Type founderSetType = new TypeToken<List<Father>>() {}.getType();
            List<Father> fathers = new Gson().fromJson(json, founderSetType);
            Father[] fathers1 = new Father[fathers.size()];
            for(int i=0;i<fathers.size();i++){
                fathers1[i] = fathers.get(i);
            }
            return fathers1;

        }
        return null;
    }


# 3.详细操作

如何新建ZxyTable对象？ 

    ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
    SQLiteDatabase sqLiteDatabase = appLibrary.getWriteDataBase();
    StudentTable studentTable = new StudentTable(sqLiteDatabase, Student.class,MyAutoDesignOperate.class);

## 3.1 创建表

- 创建表(4种方式:2种不带回调，2种带回调)

        //创建表带回调
        studentTable.onCreateTableIfNotExits(new ZxyTable.OnCreateSQLiteCallback() {
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {
               
            }
        });

        //创建表不带回调-->建议使用这种
        studentTable.onCreateTableIfNotExits();

        //不建议使用这种
        studentTable.onCreateTable();

        //不建议使用这种
        studentTable.onCreateTable(new ZxyTable.OnCreateSQLiteCallback() {
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {
                
            }

            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }
        });


## 3.2 表本身相关操作

- 重命名表  

        //重命名表不带回调  
        studentTable.reNameTable("修改的表名");
        
        //修改表名带回调
        studentTable.reNameTable("my_student", new ZxyTable.OnRenameTableCallback() {
            @Override
            public void onRenameFailure(String errMessage, String currentName, String newName, String reNameTableSQL) {
                //修改失败回调这里
            }

            @Override
            public void onRenameSuccess(String oldName, String newName, String reNameTableSQL) {
                //修改成功回调这里
            }
        });

- 删除表

        //删除表
        studentTable.deleteTable();

        //删除表带回调
        studentTable.deleteTable(new ZxyTable.OnDeleteTableCallback() {
            @Override
            public void onDeleteTableFailure(String errMessage, String deleteTableSQL) {
                
            }

            @Override
            public void onDeleteTableSuccess(String deleteTableSQL) {

            }
        });

- 清空表

        //清空数据
        studentTable.clear();

- 获取表名

        //获取表名字
        String tableName = studentTable.getTableName();

- 获取主建字段名(没有主建时返回null)

        //获取主键字段名
        String majorKeyName = studentTable.getMajorKeyName();
        if(majorKeyName == null){
            //说明无主键
        }

## 3.3 添加数据

- 添加单条数据

        Student student = new Student(-1, "Ellen2018", 19, "18272167574", "火星");
        student.setMan(true);
        Father father = new Father("Ellen2019", "1");
        student.setFather(father);

        //单条数据添加
        studentTable.saveData(student);

- 添加多条数据

        //多条数据添加
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            student = new Student(i, "Ellen2018", i, "18272167574", "火星");
            father = new Father("Ellen2019", ""+i);
            student.setFather(father);
            student.setMan(true);
            studentList.add(student);
        }

        studentTable.saveData(studentList);

- 添加或者更新(根据主键进行判断)

        //save or update 根据主键判断,根据主键查询没有该数据就存储，有就进行更新
        studentTable.saveOrUpdateByMajorKey(student);
        studentTable.saveOrUpdateByMajorKey(studentList);

- 添加之前清空表  

        //多条数据 
        studentTable.saveDataAndDeleteAgo(studentList);

        //单条数据
        studentTable.saveDataAndDeleteAgo(student);

## 3.4 删除数据

- 删除数据(根据主建判断)

        //根据主键删除数据(删除主键为3的数据)
        studentTable.deleteByMajorKey(3);

- 删除数据(根据WHERE SQL语句进行判断)

        //删除my_name为Ellen2018 且age > 20 的数据
        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.EQUAL, "Ellen2018")
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();

        studentTable.delete(whereSql);

- 清空表

        //清空数据
        studentTable.clear();

## 3.5 修改数据

- 修改数据(根据主建判断)

        //注意如果你的bean类没有声明主键，那么调用此方法就会抛 NoPrimaryKeyException
        Student student = new Student(-1, "Ellen2018", 19, "18272167574", "火星");
        Father father = new Father("Ellen2019", "1");
        student.setFather(father);

        //根据主键进行修改
        studentTable.updateByMajorKey(student);

- 修改数据(根据WHERE SQL进行判断,注意这里是整个对象进行映射)

        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.EQUAL, "Ellen2018")
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();
        //注意这种修改方式为全映射修改，如果只修改部分数据，请使用下面的方式
        //什么是全映射修改？就是将对象的整个属性数据覆盖在whereSql满足的条件里
        studentTable.update(student, whereSql);

- 修改数据(只修改表中的部分值)

        //自定义updateSql进行修改数据
        //将your_age > 20岁年龄的数据的 age 的值全部修改为 your_age = 18,my_name = "永远18岁"
        String whereSqlByAge =
                Where.getInstance(false)
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();

        String updateSql = UpdateTableDataRow.getInstance()
                .setTableName(studentTable.getTableName())
                .addSetValue("your_age", 18)
                .addSetValue("my_name", "永远18岁")
                .createSQLAutoWhere(whereSqlByAge);

        studentTable.exeSQL(updateSql);

## 3.6 查询语句

- 查询单数据(根据主建进行查询)
        
        //查询主键为3的数据，没有返回为null
        Student student = studentTable.searchByMajorKey(3);

- 查询多数据(根据WHERE SQL语句进行查询，结果可排序->根据传入的ORDER SQL语句进行排序)

        //查询my_name字段中含有"Ellen"的数据,然后根据your_age进行排序(Desc方式)
        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.LIKE, "%Ellen%")
                        .createSQL();

        String orderSql = Order.getInstance(false)
                .setFirstOrderFieldName("your_age")
                .setIsDesc(true)
                .createSQL();

        List<Student> studentList = studentTable.search(whereSql, orderSql);

- 获取表中所有数据(结果可排序->根据传入的ORDER SQL语句进行排序)

        String orderSql = Order.getInstance(false)
                .setFirstOrderFieldName("your_age")
                .setIsDesc(true)
                .createSQL();

        //查询表中所有数据，没有排列顺序
        List<Student> studentList1 = studentTable.getAllData(null);
        //查询表中所有数据，根据your_age进行排序(Desc方式)
        List<Student> studentList2 = studentTable.getAllData(orderSql);
