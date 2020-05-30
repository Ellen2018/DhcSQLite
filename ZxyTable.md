
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
        @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
        @Operate(operate = OperateEnum.JSON)
        @DhcSqlFieldName(sqlFieldName = "your_father")
        private Father father;
        @DataStructure //表示这个属性是数据类型属性，需要用注解区分，才能正确的进行json映射，否则会报错
        @DhcSqlFieldName(sqlFieldName = "Dads")
        private Father[] fathers;

        ......

    }

目前支持的属性类型:

- 基本数据类型 & 装箱类型
- String类型
- 数据结构类型  
&emsp;&emsp;所有类型数组  
&emsp;&emsp;ArrayList  
&emsp;&emsp;LinkedList  
&emsp;&emsp;Vector  
&emsp;&emsp;HashSet  
&emsp;&emsp;TreeSet  
&emsp;&emsp;HashMap  
&emsp;&emsp;TreeMap
- 其它任意非数据结构类型的引用类型(例如上面代码中的Father)

关于这些注解的说明

- @Ignore 
 
&emsp;&emsp;此属性不映射到数据库表中。

- @DhcSqlFieldName 
 
&emsp;&emsp;需要传入一个String类型的值，此注解的作用就是设置该属性映射到数据库中的字段名。当你不写的时候，映射到数据库中的字段名为属性本身的名字，例如int age它在数据库表中的字段名为age,如果你为它指定@DhcSqlFieldName(sqlFieldName = "age\_student"),那么数据库表中的字段会被映射为age\_student。 

- @DataStructure 
 
&emsp;&emsp;这个注解专门用于映射数组,List,Set,Map数据结构类型数据的，目前仅仅支持所有数组,ArrayList,LinkedList,Vector,HashSet,TreeSet,HashMap,TreeMap,其它均不支持,如果您想要支持其它类型的转换，笔者提供了一种方式:通过studentTable.addIntercept()添加映射拦截来完成您要添加的任何类型属性支持，关于这部分知识点，请查看【3.8 扩展支持新的属性类型】。注意的是您可以不需要添加此注解，框架内部自动判断当前属性是否为数据结构类型，如果是数据结构类型，则自行按照数据结构类型进行处理。还需要注意的是存的操作基本由框架自动帮您完成，但是取的操作您需要在ZxyTable中实现resumeDataStructure方法进行去恢复这个数据结构的数据。

- @Operate 
 
&emsp;&emsp;此注解为不是基本类型数据(Sttring除外)而使用的，比如上述代码中的father属性，因为框架本身不知道您要以怎样的方式去存储它，所以此注解的作用就是告诉框架以怎样的方式来存储它，笔者封装了2种方式，一种是JSON方式,一种是VALUE存储方式，json方式我无需解释，就是将它以json进行映射，value的方式我必须要着重讲一讲，它是通过反射将属性中的某个属性进行存储，比如Father中有个id的属性，那么如你为它指定注解 @Operate(operate = OperateEnum.VALUE,valueName = "id")，那么映射的时候father最终会以id的值进行存储，恢复数据的时候father中只有id是读取的，father其他的属性值由于没有映射到数据库表中，因此其他属性皆为初始化状态，目前VALUE方式仅仅支持基本类型和String类型，注意当您没有添加此注解时，默认会以JSON方式进行处理,也就是说如果您以JSON方式进行映射，此注解无需添加,代码示例如下：  

Father类： 

    public class Father {
 
        private String name;
        //保存的是这个id的数据
        private String id;

        ......
    }

@Operate注解的使用:

    //以JSON方式完成存储
    @Operate(operate = OperateEnum.JSON)
    private Father father;

    //以VALUE方式完成存储,这里必须指定valueName   
    @Operate(operate = OperateEnum.VALUE,valueName = "id")
    private Father father2;

    //什么注解也不加，框架内部就会以JSON映射方式进行处理
    private Father father3;

- @SqlType(不再建议使用) 
 
&emsp;&emsp;此注解与@Operate注解搭配使用，它的作用就是告诉框架,这个属性映射到数据库表中的字段类型以及数据长度，当数据长度为0或者负值时，长度不受限制，当您不传入length时它默认为-1，注意如果您将它用于基本类型以及String，数据结构类型的属性上没有任何作用。注意在新的版本中框架会判断字段在数据中的合适类型，您无须指定@SqlType。示例代码如下

    //指定father2映射到数据库字段的类为TEXT类型，长度为100
    @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
    @Operate(operate = OperateEnum.VALUE,valueName = "id")
    private Father father2;

&emsp;&emsp;SQLFieldTypeEnum 有哪些类型?无须笔者啰嗦，对数据库很懂的您懂的。  

    INTEGER("integer"),
    BIG_INT("bigint"),
    LONG_TEXT("longtext"),
    MEDIUM_TEXT("mediumtext"),
    TEXT("text"),
    REAL("real"),
    BLOB("blob"),
    NUMERIC("numeric"),
    DATE("date");

#### 以下是约束注解(对应于SQL中的约束条件设置)

- @MajorKey 
 
&emsp;&emsp;这个注解的作用就是用来指定主键的,它可以传递一个booleane类型的参数进去，这个参数代表主键是否自增，如果不传参数，默认值为false。注意当您在bean类中指定了多个@MajorKey就会报异常。

- @NotNull 

&emsp;&emsp;它的作用就是映射到数据库中的字段添加一个不能为NULL的特性。意思就是这个数据不能填塞null值,否则会报错。

- @Unique

&emsp;&emsp;加上之后具有唯一性的作用，也就是说，您往数据库的该字段中映射的值必须保持唯一，不然就会奔溃报错。

- @Check

&emsp;&emsp;此注解需要传入一个条件判断的sql语句，例如:"age > 8",类似这样的，这代表此字段只能被age大于8的数据进行填塞，否则就会奔溃报错。此外，如果您不想写具体的字段名称，您可以用"{}"来代替，笔者将自动为您进行修改成当前的属性对应的数据库中的字段名，就拿"age > 8"为例子，如果您不想写"age"，那么您只需要这么写："{} > 8"即可。

- @Default

&emsp;&emsp;从字面上理解它是为字段设置默认值的意思，它需要传递两个参数，一个是枚举(DefaultValueEnum)代表您设置的默认值类型，另一个则是您设置的具体默认值。用法如下

    public class TestDefault {

        @Default(defaultValueEnum = DefaultValueEnum.BYTE,byteValue = 1)
        private byte a1;
        @Default(defaultValueEnum = DefaultValueEnum.SHORT,shortValue = 2)
        private short a2;
        @Default(defaultValueEnum = DefaultValueEnum.INT,intValue = 3)
        private int a3;
        @Default(defaultValueEnum = DefaultValueEnum.LONG,intValue = 4)
        private long a4;
    
        ......
    }

&emsp;&emsp;不仅仅支持以上类型的默认值设置，以下是所有支持的类型:

- BYTE
- SHORT
- INT
- LONG
- FLOAT
- DOUBLE
- CHAR
- STRING

就拿STRING做一个说明，如果您的属性映射到数据库中的字段类型是TEXT,那么您为这个属性指定默认值的时候就需要这么写: 
    
    @Default(defaultValueEnum = DefaultValueEnum.STRING,strValue = "字符串默认值")

- @EndAutoString

&emsp;&emsp;这个是笔者考虑到约束注解太多了，有些同学不太想那么多注解，因为不太清晰优雅，那么笔者专门提供这个注解来完成所有字段约束的，它可以用来完成以上所有约束注解的功能。代码示例：

我想让一个字段id非空且唯一：

    @EndAutoString("NOT NULL UNIQUE")
    private int id;

我想让一个字段id为主键且约束Check条件为"id > 8"：

    @EndAutoString("PRIMARY KEY CHECK(id > 8)")
    private int id;

我想让一个字段age约束Check条件为"age > 0 & age < 200"：

    @EndAutoString("CHECK(age > 0 & age < 200)")
    private int age;

另外一定要注意的是@EndAutoString的优先级高于以上所有约束注解，也就是说您同时为属性设置了@EndAutoString和其它任意约束注解，那么其它的约束注解会失效，一切约束将会以@EndAutoString为主。


## 2.2 声明ZxyTable类介绍
 
代码：

    public class StudentTable extends ZxyTable<Student, MyAutoDesignOperate> {


        /**
         *
         * 调用此构造器表名为类名：Student
         * db：SQLiteDatabase对象，可以通过ZxyLibrary传入       
         */ 
        public StudentTable(SQLiteDatabase db) {
            super(db,);
        }

        /**
         *
         * 调用此构造器表名传入的：autoTableName
         * db：SQLiteDatabase对象，可以通过ZxyLibrary传入
         * autoTableName:自定义表名
         */ 
        public StudentTable(SQLiteDatabase db, String autoTableName) {
            super(db,autoTableName);
        }


        /**
         * boolean类型值转化为数据库中的存储
         * 此方法默认情况下无须重写
         *
         * 如果你的bean类中没有boolean类型的存储，无须重写这个方法
         *
         * 如果有：如果你的boolean类型不想映射为String类型的"0"和"1"(默认),那么您只需要将这个方法
         * 重写即可，只能返回基本数据类型 & Sting类型的数据，其他类型均会报异常，仅支持这些
         *
         * 这么实现有什么意义呢？为的是让数据库中的数据变得更加易懂，通常情况下没有这种需求，可万一有这个需求呢
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
         * 您可以根据classFieldName 或者 fieldClass
         * 再根据json来恢复您bean类中数据结构类型属性数据
         *         
         * @param classFieldName 属性的名字；例如fathers
         * @param fieldClass 属性的类型：；例如ArrayList.class 
         * @param json
         * @return
         */
        @Override
        protected Object resumeDataStructure(String classFieldName, Class fieldClass, String json) {
          if(classFieldName.equals("fathers")){
                Type founderSetType = new TypeToken<Father[]>() {}.getType();
                Father[] fathers = new Gson().fromJson(json, founderSetType);         
                return fathers;
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

- StudentTable(SQLiteDatabase db)

&emsp;&emsp;调用此构造器它的表名为它绑定的bean类的类名。db为操作数据库的SQLiteDatabase，可由ZxyLibrary获取，也可以通过原生的SQLiteOpenHelper获取。

-  StudentTable(SQLiteDatabase db,String autoTableName)

&emsp;&emsp;调用此构造器它的表名为传入的autoTableName。db为操作数据库的SQLiteDatabase，可由ZxyLibrary获取，也可以通过原生的SQLiteOpenHelper获取。 

- Object setBooleanValue(String classFieldName, boolean value)

&emsp;&emsp;此方法作用就是将bean对象的boolean类型值转换为数据库中能存储的值,无需重写，默认情况下以String类型"0"(false),"1"(true)的方式保存,如果您不希望您的boolean类型字段映射为String类型，您可以修改返回值为基本类型或者String类型其他值，其他类型暂时不支持，例如：我现在要isMan的数据库字段类型映射为TEXT类型的“男”和“女”，那么代码如下： 

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

这里有个注意的地方，就是当你设置为基本类型的映射值时，如果您的属性是装箱类型，也就是Boolean,如果您的属性值为null,按照逻辑来讲从数据库中映射出来也应该为null值，但是如果您以int类型0或者1来保存，那么就会出现一个诡异的现象就是Boolean原本应该读取的值为null,但是却被读成了false，原因是读取的时候，以int类型去读取一个为NULL的空数据，它就会返回0给您，因此最终会被映射为false,这里笔者提出来是希望您注意到这点以免给您造成不必要的bug困扰，为此呢，笔者建议您使用使用String类型返回，不建议使用其它类型进行映射，当然如果您的属性不是装箱类型的Boolean,那么您返回任何类型的值都可以。

    //如果数据为NULL,这里value不会读取成NULL,而是读取成0
    //最终判断的时候以0映射，最终您的Boolean会被映射成false(请一定注意这点)
    Object value = cursor.getInt(index);

还有一点需要笔者提出的如果您的属性有装箱类型Integer,Short,Dobule,Float,Long等等，请警惕这个将NULL读取成0的机制,没有必要的话,笔者建议不要使用装箱类型，如果装箱类型的NULL对您的业务有逻辑需求，笔者建议多加个变量进行拆分，比如以下代码:

    Integer integer;
     
    //它可以拆分成
    int integer;
    //为false时候读取integer的值，为true时为NULL状态 
    boolean isNull = false;


- JsonLibraryType getJsonLibraryType()

&emsp;&emsp;此方法的作用就是指定此框架使用的JSON库是哪种，因为此框架内部用到了json映射，因此需要指定使用何种方式的JSON库，目前仅支持2中JSON库：Gson(谷歌)和FastJson(阿里)，注意如果您的项目中没有导入Gson(谷歌)或者FastJson(阿里),那么当框架进行Json映射时就会抛出JsonNoCanFormatException的运行时异常，您无需担心，只要导入对应要使用的库即可，示例代码如下:  

    protected JsonLibraryType getJsonLibraryType() {
        //JsonLibraryType.FastJson; ->FastJson方式
        return JsonLibraryType.Gson; //Gson方式
    }

- Object resumeDataStructure(String classFieldName, Class fieldClass, String json)

&emsp;&emsp;此方法的作用从名字上就可以看出，它是将数据库中的json数据转化为数据结构的，由于笔者不知道你要恢复的数据结构，比如您的属性为List<Father>,虽然笔者知道您的数据结构类型为List,但我无法知道你要将它恢复成ArrayList还是LikedList等，所以此处的逻辑交给您来完成比较好，逻辑也很简单，只需要根据classFieldName和json进行解析，解析成你想要的数据结构类型，然后进行返回即可。比如bean类属性中有个Father[] fathers的数组数据类型结构的数据，那么恢复的示例代码如下:  

        @Override
        protected Object resumeDataStructure(String classFieldName, Class fieldClass, String json) {
          if(classFieldName.equals("fathers")){
                Type founderSetType = new TypeToken<Father[]>() {}.getType();
                Father[] fathers = new Gson().fromJson(json, founderSetType);         
                return fathers;
            }
            return null;
        }


# 3.详细操作

如何新建ZxyTable对象？ 

    ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
    SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();
    StudentTable studentTable = new StudentTable(sqLiteDatabase);

## 3.1 创建表

- 创建表(4种方式:2种不带回调，2种带回调)

        //创建表带回调
       studentTable.onCreateTableIfNotExits(new OnCreateTableCallback() {
            @Override
            public void onCreateTableFailure(String errMessage, String tableName, String createSQL) {
                
            }

            @Override
            public void onCreateTableSuccess(String tableName, String createSQL) {

            }
        });

        //创建表不带回调-->建议使用这种
        studentTable.onCreateTableIfNotExits();

        //不建议使用这种
        studentTable.onCreateTable();

        //不建议使用这种
        studentTable.onCreateTable(...(回调对象));


## 3.2 表本身相关操作

- 方便调试监听

        studentTable.setDebugListener(new DebugListener() {
            @Override
            public void exeSql(String sql) {
                //sql是框架每执行一个sql语句都会回调这里
                //您可以以debug的方式输出这些sql语句方便您进行调试
            }
        });

- 重命名表  

        //重命名表不带回调  
        boolean b = studentTable.reNameTable("修改的表名");
        if(b){
          //修改成功
        }else{
          //修改失败 
        } 
        
- 删除表

        //删除表
        boolean b = studentTable.deleteTable();、
        if(b){
          //删除成功
        }else{
          //删除失败 
        } 

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

- 查询表是否存在  

        if (studentTable.isExist()) {
           //表存在
        }else{
           //表不存在
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
        //将your_age > 20岁年龄的数据的部分值全部修改为 your_age = 18,my_name = "永远18岁"
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

- 查询单数据(根据主键进行查询)
        
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
        List<Student> studentList1 = studentTable.getAllData();
        //查询表中所有数据，根据your_age进行排序(Desc方式)
        List<Student> studentList2 = studentTable.getAllData(orderSql);

## 3.7 拦截映射过程

&emsp;&emsp;笔者提供了一个拦截机制，就是让您能修改整个框架的映射过程。举个栗子，我现在想将Father类型数据进行拦截处理,把它的id作为保存和恢复，那么代码如下:

    //第一个泛型代表要拦截的类型
    //第二个泛型代表此类型映射到数据库中字段类型
    studentTable.addIntercept(new Intercept<Father,String>() {

            /**
             * 设置表中字段类型 & 数据长度
             * @param field
             * @return
             */
            @Override
            public SQLFieldType setSQLiteType(Field field) {
                return new SQLFieldType(SQLFieldTypeEnum.TEXT,null);
            }

            /**
             * 拦截判断
             * @param field
             * @return
             */
            @Override
            public boolean isType(Field field) {
                return field.getType() == Father.class;
            }

            /**
             * 将数据库中的sqlValue映射为指定类型
             * @param field
             * @param sqlValue
             * @return
             */
            @Override
            public Father toObj(Field field, String sqlValue) {
                Father father = new Father();
                father.setId(sqlValue);
                return father;
            }

            /**
             * 将对象映射为数据库中的存储值
             * @param field
             * @param sqlValue
             * @return
             */
            @Override
            public String toValue(Field field, Father dataValue) {
                Father father = dataValue;
                if(father == null) {
                    return null;
                }else {
                    return father.getId();
                }
            }
        });

我们还可以利用这个拦截修改我们的基本类型的映射，我想把属性名为isMan且为boolean类型以"真的"(true)和"假的"(false)进行映射，代码如下:  

        studentTable.addIntercept(new Intercept<Boolean,String>() {
           
            @Override
            public SQLFieldType setSQLiteType(Field field) {
                return new SQLFieldType(SQLFieldTypeEnum.TEXT,2);
            }

            @Override
            public boolean isType(Field field) {
                if(field.getName().equals("isMan")) {
                    return field.getType() == Boolean.class || field.getType().getName().equals("boolean");
                }else {
                    return false;
                }
            }

            @Override
            public Boolean toObj(Field field, String sqlValue) {
                if(sqlValue.equals("真的")){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public String toValue(Field field, Boolean dataValue) {
                if(dataValue){
                    return "真的";
                }else {
                    return "假的";
                }
            }
        });

注意添加拦截是按照顺序来进行拦截的，因此后添加的先判断，先拦截，如果您想移除某个拦截，示例代码如下:  

    studentTable.removeIntercept(intercept);

## 3.8 扩展支持新的属性类型

&emsp;&emsp;因为笔者仅支持基本数据类型 & 部分数据结构类型 & 不是数据结构的引用类型，如果您想扩展新的数据结构类型可以像3.7那样添加一个拦截的接口就行了，比如：我现在要对Stack支持，那么代码如下:

       studentTable.addIntercept(new Intercept<Stack,String>() {
           
            @Override
            public SQLFieldType setSQLiteType(Field field) {
                return ...;
            }

            @Override
            public boolean isType(Field field) {
                return field.getType() == Stack.class;
            }

            @Override
            public Stack toObj(Field field, String sqlValue) {
                //将sqlValue(Json)恢复成Stack
                ...
                return stack;
            }

            @Override
            public String toValue(Field field, Stack dataValue) {
                //把dataValue转化为json
                return json;
            }
        });

