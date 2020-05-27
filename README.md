
## 0.如何导入?

[![](https://jitpack.io/v/Ellen2018/DhcSQLite.svg)](https://jitpack.io/#Ellen2018/DhcSQLite)

&emsp;&emsp;首先你需要在项目的build.gradle中配置以下代码：  

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }//加上这句即可
		}
	}

&emsp;&emsp;然后你在要使用该库的module中添加以下依赖:  

    implementation 'com.github.Ellen2018:DhcSQLite:x.y.z'

&emsp;&emsp;x,y,z是笔者库的版本值，例如：1.0.0

## 1.框架简介

&emsp;&emsp;DhcSQLite是一款基于注解加反射的第三方SQLite库，基于SQLite进行的封装。您可以轻轻松松管理您的数据库，框架内部使用到了反射，注解，动态代理等技术，最最主要的是您要是觉得SQL语句写起来太麻烦，笔者提供给您另外一个SQL语句构建库[ZxySQLiteCreate](https://github.com/Ellen2018/ZxySQLiteCreate)，整个框架内部也有使用到它。


## 2.快速入门

&emsp;&emsp;例子：学生成绩管理系统,学生属性包括：姓名(name),学号(sid),年龄(age),科目以及成绩(Map<Subject,Integer>),父亲(Father(有姓名以及联系方式属性))等等。业务包括：查看总分排名，根据学号查询学生所有信息，查看姓名里带"李"的学生等等。

### 2.1 第一步：新建bean类

Student类代码:

    public class Student {

        /**
         * 学号
         */
        @MajorKey(isAutoIncrement = true)//主键 & isAutoIncrement = true表示自增
        private int sid;

        /**
         * 姓名
         */
        private String name;

        /**
         * 年龄
         */
        private int age;

        /**
         * 科目以及成绩
         */
        @DataStructure //声明它是属于数据结构的属性
        private Map<String,Integer> subjectMap;

        /**
         * 总分
         */
        private int allGrade;

        /**
         * 是否为男生
         */
        private boolean isMan;

        @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT)//映射为TEXT类型，不限制长度
        @Operate(operate = OperateEnum.JSON)//映射成Json
        private Father father;

        ......

    }

### 2.2 步骤二：定义元操作接口(声明一个接口去继承AutoDesignOperate)

    public interface StudentOperate extends AutoDesignOperate {
    
        /**
         * 通过sid删除指定数据
         * @param sid
         */
        @Delete("sid = @sid")
        void deleteBySid(@Value("sid") int sid);

        /**
         * 更新名字通过sid(学号)
         *
         * @param sid
         * @param newName
         */
        @Update(valueSql = "name = '@newName'", whereSql = "sid = @sid")
        void updateStudentNameById(@Value("sid") int sid, @Value("newName") String newName);

        /**
         * 查询名字中带有str的数据，结果按照id进行排序
         *
         * @param str
         * @return
         */
         @Search(whereSql = "name like '%@str%'", orderSql = "sid ASC")
         List<Student> searchByLikeName(@Value("str") String str);
    
    }

### 2.3 步骤三：声明ZxyLibrary类(声明一个类去继承ZxyLibrary)

    public class AppLibrary extends ZxyLibrary {
    
        public AppLibrary(Context context, String name, int version) {
            super(context, name, version);
        }

        public AppLibrary(Context context, String libraryPath, String name, int version) {
            super(context, libraryPath, name, version);
        }

        @Override
        public void onZxySQLiteCreate(SQLiteDatabase db) {
        
        }

        @Override
        public void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

### 2.4 步骤四：声明ZxyTable类(声明一个类去继承ZxyTable)

    public class StudentTable extends ZxyTable<Student,StudentOperate> {

        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, Class<? extends AutoDesignOperate> autoClass) {
            super(db, dataClass, autoClass);
        }

        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, String autoTableName, Class<? extends AutoDesignOperate> autoClass) {
            super(db, dataClass, autoTableName, autoClass);
        }

        @Override
        protected SQLFieldType getSqlFieldType(String classFieldName, Class typeClass) {
            if(classFieldName.equals("isMan")){
                return new SQLFieldType(SQLFieldTypeEnum.TEXT,1);
            }else {
                return super.getSqlFieldType(classFieldName, typeClass);
            }
        }

        @Override
        protected Object setBooleanValue(String classFieldName, boolean value) {
        vif(classFieldName.equals("isMan")){
                if(value){
                    return "男";
                }else {
                    return "女";
                }
            }else {
                return super.setBooleanValue(classFieldName, value);
            }
        }
    
        /**
         * 恢复数据结构数据
         * @param classFieldName
         * @param json
         * @return
         */
        @Override
        protected Object resumeDataStructure(String classFieldName, String json) {
            if(classFieldName.equals("subjectMap")){
                Type type = new TypeToken<HashMap<String,Integer>>() {}.getType();
                HashMap<String,Integer> subjectMap = new Gson().fromJson(json, type);
                return subjectMap;
            }
            return super.resumeDataStructure(classFieldName, json);
        }
    }

### 2.5 步骤五:开始创建数据库,表 & 进行一系列数据库操作

[此处请直接点击查看如何操作数据库的代码](https://github.com/Ellen2018/DhcSqlteTest/blob/master/app/src/main/java/com/ellen/dhcsqltetest/MainActivity.java)


[点击查看演示的项目](https://github.com/Ellen2018/DhcSqlteTest)

## 3.理论介绍 & 深入学习

&emsp;&emsp;DhcSqlite里面有4个比较重要的角色:

- ZxySQLiteCreate(SQL语句制作者)  
&emsp;&emsp;这个笔者已经提到了，它是用来帮助您完成各种Sql语句构建的，如果您想要完整看此框架的用法，请移步到[**基于Android SQLite语句构建库的轮子**](https://blog.csdn.net/ClAndEllen/article/details/103909339),一定要注意的是此框架DhcSqlite已经内部集成了，您无须单独集成，整个项目任何位置均可使用。  

- ZxyLibrary(数据库管理者)  
&emsp;&emsp;这个是用来对数据库进行一系列操作的，您可以通过此类对库的名字，存储地址进行定义，它还包括对表的删除，重命名，清空等等操作，反是对表操作的都在里面已经封装好了，一个对象对应一个库。 

&emsp;&emsp;**详细用法请查看**[**ZxyLibrary**](https://github.com/Ellen2018/DhcSQLite/blob/master/ZxyLibrary.md)  

- ZxyTable(数据库中表的管理者)   
&emsp;&emsp;这个是用来对数据库中表进行一系列操作的，例如：增删改查，清空等等操作，它是一个泛型类，就是说泛型的参数指定的是关联的映射类， 此外还提供了好几个注解给您的bean类提供方便的数据库映射逻辑，例如:@MajorKey是用来bean类中指定主键的注解,@Ignore是用来bean类中不想该属性映射到数据库中的注解，还有很多其他的注解。

&emsp;&emsp;**详细用法请查看**[**ZxyTable**](https://github.com/Ellen2018/DhcSQLite/blob/master/ZxyTable.md)

- AutoDesignOperate(自定义元操作者)  
&emsp;&emsp;这个是一个接口，它是用来具体定义您项目中具体业务逻辑的接口，以动态代理的方式为您完成各种骚操作，您只需要在实现AutoDesignOperate里面声明方法，方法中使用@Search,@Update,@Value等注解帮您自动操作数据库，好处就是你不需要自动手动写代码，您只需要注意sql语句的填写即可。  

&emsp;&emsp;**详细用法请查看**[**AutoDesignOperate**](https://github.com/Ellen2018/DhcSQLite/blob/master/AutoDesignOperate.md)
