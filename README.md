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

## 1.介绍

&emsp;&emsp;DhcSQLite是一款基于注解加反射的第三方SQLite库，基于SQLite进行的封装。用到了Java反射加注解技术，后期有时间改成注解加APT的方式。此库的操作增删改查等全部基于另外一个库生成的SQL语句，库的地址是：
[ZxySQLiteCreate](https://github.com/Ellen2018/ZxySQLiteCreate)

## 2.如何使用？

&emsp;&emsp;用法很简单详细步骤如下：

&emsp;&emsp;步骤一：新建一个类去继承ZxyLibrary,首先需要一个Library对象，你必须继承ZxyLibrary，并且重写哪些该重写的方法，至于这些方法有什么用,请看注释

    public class SQliteLibrary extends ZxyLibrary {

        /**
         * 
         * @param context 上下文
         * @param name 数据库的名字
         * @param version 数据库的版本
         */
        public SQliteLibrary(Context context, String name, int version) {
            super(context, name, version);
        }

        /**
         * 
         * @param context 上下文
         * @param libraryPath 数据库所在的父目录
         * @param name 数据库的名字
         * @param version 数据库版本号
         */
        public SQliteLibrary(Context context, String libraryPath, String name, int version) {
            super(context, libraryPath, name, version);
        }
    
        /**
         * 创建数据库时回调
         * @param db
         */
        @Override
        public void onZxySQLiteCreate(SQLiteDatabase db) {

        }
    
        /**
         *  数据库版本升级时回调
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

&emsp;&emsp;选择好需要和数据库进行绑定的类，例如我要绑定的是Student类，那么我就需要创建一个StudentTable的类并让它继承ZxyReflectionTable<Student>,代码如下所示：

    public class StudentTable extends ZxyReflectionTable<Student> {

        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass) {
            super(db, dataClass);
        }

        public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, String autoTableName) {
            super(db, dataClass, autoTableName);
        }
    
        /**
         * 设置属性对应的数据库字段的类型
         * @param classFieldName
         * @param typeClass
         * @return
         */
        @Override
        protected SQLFieldType getSQLFieldType(String classFieldName, Class typeClass) {
            if(classFieldName.equals("isMan")){
                //将isMan的boolean映射为Integer类型，且长度为1位
                return new SQLFieldType(SQLFieldTypeEnum.INTEGER,1);
            }else {
                return new SQLFieldType(getSQlStringType(typeClass), null);
            }
        }

        /**
         * boolean类型值转化为数据库中的存储
         * @param classFieldName
         * @param value
         * @return
         */
        @Override
        protected Object setBooleanValue(String classFieldName, boolean value) {
            if(value){
                return 0;
            }else {
                return 1;
            }
        }

        /**
         *  检测发现目标类中不可转换类型的属性类型映射为数据库中存储的字段类型
         *  例如:Father --> TEXT
         * @param classFieldName 类属性名字
         * @param typeClass 类属性的类型
         * @return 返回存储在数据库的类型
         */
        @Override
        protected SQLFieldType conversionSQLiteType(String classFieldName, Class typeClass) {
            //将Java类中的Father类型的father字段映射为数据库中的TEXT类型
            if(classFieldName.equals("father")){
                return new SQLFieldType(SQLFieldTypeEnum.TEXT,null);
            }
            return null;
        }

        /**
         * 将目标类对象的非转换类型的属性值映射为数据库中存储的值,与conversionSQLiteType方法配合使用
         * @param student
         * @param classFieldName
         * @param typeClass
         * @param <E>
         * @return
         */
        @Override
        protected <E> E setConversionValue(Student student, String classFieldName, Class typeClass) {
            if(classFieldName.equals("father")){
                Gson gson = new Gson();
                String jsonFather = gson.toJson(student.getFather());
                return (E) jsonFather;
            }
            return null;
        }

        /**
         *  将数据库中相应的非转换类型存储的值映射为相应的类型值
         *  例如： TEXT -> Father
         * @param value
         * @param classFieldName
         * @param typeClass
         * @param <E>
         * @return
         */
        @Override
        protected <E> E resumeConversionObject(Object value, String classFieldName, Class typeClass) {
            if(classFieldName.equals("father")){
                String json = (String) value;
                Gson gson = new Gson();
                Father father = gson.fromJson(json,Father.class);
                return (E) father;
            }
            return null;
        }
    }

&emsp;&emsp;步骤三:操作表。接下来，我们就可以任性的进行创建表和操作表了，笔者提供了两种方式来操作表，第一种是笔者封装的方法，第二种是通过[ZxySQLiteCreate](https://github.com/Ellen2018/ZxySQLiteCreate)
库来构建add,delete,where,order等语句来完成操作，由于封装的实在太多，待笔者一点点进行整理吧。详细代码如下：  


        //库的名字叫sqlite_library,版本号为1
        SQliteLibrary sQliteLibrary = new SQliteLibrary(this,"sqlite_library",1);
        //构建步骤二中StudentTable对象
        StudentTable studentTable = new StudentTable(sQliteLibrary.getWriteDataBase(),Student.class);

        //创建表带回调(也有不带回调的)
        studentTable.onCreateTableIfNotExits(new ZxyReflectionTable.OnCreateSQLiteCallback() {

            //创建表之前回调
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            //创建表失败后回调
            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            //创建表成功后回调
            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {
                BaseLog.log("创建表",createSQL);
                tvAll.setText(createSQL);
            }
        });

        Student student = new Student("ellen",23,"1823213","侏儒");
        student.setFather(new Father("Ellen_chen","2133123123123"));
        //添加单条数据
        studentTable.saveData(student);
        //通过SQL语句制作库制作SQL语句
        String ordersql = Order.getInstance(false).setFirstOrderFieldName("id").setSecondOrderFieldName("name").setIsDesc(true).createSQL();
      
        for(Student student1:studentTable.getAllDatas(ordersql)){
            BaseLog.log("存储的数据",student1.toString());
        }
    
&emsp;&emsp;关于注解的使用规则，阅读下面Student Bean类很明显就能知道用法:

    public class Student {

        @Primarykey //主键
        private int id;
        @DhcSqlFieldName("my_name") //映射数据库中字段名字为my_name
        private String name;
        @DhcSqlFieldName("your_age")
        private int age;
        private String phoneNumber;
        private String address;
        @Ignore //不映射这个属性到数据库中
        private String ingoreString;
        private boolean isMan;
        private Father father;

        public Student(String name, int age, String phoneNumber, String address) {
            this.name = name;
            this.age = age;
            this.phoneNumber = phoneNumber;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Father getFather() {
            return father;
        }

        public void setFather(Father father) {
            this.father = father;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", address='" + address + '\'' +
                    ", ingoreString='" + ingoreString + '\'' +
                    ", isMan=" + isMan +
                    ", father=" + father +
                    '}';
        }    
    }           
    
# 要想查看完整用法，请查阅app Module 下的 MainActivity（感谢使用）    
        
