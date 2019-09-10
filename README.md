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

&emsp;&emsp;DhcSQLite是一款基于注解加反射的第三方SQLite库，基于SQLite进行的封装。用到了Java反射加注解技术，后期有时间改成注解加APT的方式。

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
         *  设置数据库中对应的属性名
         */
        @Override
        protected String getSQLFieldName(String classFieldName, Class typeClass) {
            return classFieldName;
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
