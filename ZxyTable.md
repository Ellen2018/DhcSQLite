
# 1.简介  

&emsp;&emsp;你使用此库的第二步就是创建一个ZxyTable类，在继承它的时候需要传入2个泛型，第一个泛型指的是映射的bean类型，第二个参数就是您自定义的元操作角色对应的接口[AutoDesignOperate]()。
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
 
&emsp;&emsp;此属性不映射

- @DhcSqlFieldName 
 
&emsp;&emsp;需要传入一个String类型的值，此注解的作用就是设置该属性映射到数据库中的字段名。当你不写的时候，映射到数据库中的字段名为属性本身的名字，例如int age它在数据库表中的字段名为age,如果你为它指定@DhcSqlFieldName(sqlFieldName = "age\_student"),那么数据库表中的字段会被映射为age\_student。 

- @DataStructure 
 
&emsp;&emsp;这个注解专门用于映射数组,List,Set,Map,Stack数据结构类型数据的，目前仅仅支持这些,如果您想要扩展其它数据结构，您需要了解原理进行修改，数据结构属性的数据框架自动帮您以JSON的方式进行存储。注意的是你可以不需要添加此注解，框架内部自动判断当前属性是否为数据结构类型，如果是数据结构类型，则自行按照数据结构类型进行处理。还需要注意的是存的操作基本又框架自动帮您完成，但是取的操作您需要在ZxyTable中实现resumeDataStructure方法进行去恢复这个数据结构的数据。

- @MajorKey 
 
&emsp;&emsp;这个注解的作用就是用来指定主键的,它可以传递一个booleane类型的参数进去，这个参数代表主键是否自增，如果传参数，默认值为false。注意当您在bean类中指定了多个@MajorKey就会报异常。

- @Operate 
 
&emsp;&emsp;此注解为不是基本类型数据(Sttring除外)而使用的，比如上述代码中的father属性，因为框架本身不知道您要以怎样的方式去存储它，所以此注解的作用就是告诉框架以怎样的方式来存储它，笔者封装了2种方式，一种是JSON方式,一种是VALUE存储方式，json方式我无需解析，就是将它以json进行映射，value的方式我必须要着重讲一讲，它是通过反射将属性中的某个属性进行存储，比如Father中有个id的属性，那么如你为它指定注解 @Operate(operate = OperateEnum.VALUE,valueName = "id")，那么映射的时候father最终会以id的值进行存储，恢复数据的时候father中只有id是读取的，father其他的属性值由于没有映射到数据库表中，因此其他属性皆为初始化状态，代码示例如下：  

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
            if(value){
            return "男";
            }else {
            return "女";
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
         *  非常注意:如果你的项目中没有导入Json解析库:Gson或者是FastJson,那么在映射的时候就会抛出JsonNoCanFormatException
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

# 3.详细操作

## 3.1 创建表

- 创建表(4种方式:2种不带回调，2种带回调)

## 3.2 表本身相关操作

- 重命名表  
- 删除表
- 清空表

## 3.3 添加数据

- 添加单条数据
- 添加多条数据
- 添加或者更新(根据主键进行判断)
- 添加或者更新(根据传入的WHERE SQL语句进行判断)

## 3.4 删除数据

- 删除数据(根据主建判断)
- 删除数据(根据WHERE SQL语句进行判断)
- 清空表

## 3.5 修改数据

- 修改数据(根据主建判断)
- 修改数据(根据WHERE SQL进行判断)

## 3.6 查询语句

- 查询单数据(根据主建进行查询)
- 查询多数据(根据WHERE SQL语句进行查询，结果可排序->根据传入的ORDER SQL语句进行排序)
- 获取表中所有数据(结果可排序->根据传入的ORDER SQL语句进行排序)


