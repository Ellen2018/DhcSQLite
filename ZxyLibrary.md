
# 1.简介  

&emsp;&emsp;你使用此库的第一步就是创建一个ZxyLibrary的类，在创建库的位置new 一个此类的对象出来即可。您可以根据构造器选择库存储的目录，可以存在于data/data......目录下，也可以存储在外部存储目录下,只是调用不同的构造器罢了。

# 2.代码介绍


类的代码：

    public class AppLibrary extends ZxyLibrary {

        /**
         *
         * @param context 上下文
         * @param name 数据库的名字
         * @param version 数据库的版本
         */
        public AppLibrary(Context context, String name, int version) {
            super(context, name, version);
        }

        /**
         *
         * @param context 上下文
         * @param libraryPath 数据库所在的父目录
         * @param name 数据库的名字
         * @param version 数据库版本号
         */
        public AppLibrary(Context context, String libraryPath, String name, int version) {
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

如何使用?

    //这种方式创建的数据库存放于data/data......目录下 
    AppLibrary appLibrary = new AppLibrary(this, "您的数据库名字", 1);
    
    //这种方式创建的数据库存放于外部目录下(注意这里需要先申请到文件读写权限才行) 
    AppLibrary appLibrary = new AppLibrary(this, "父目录","您的数据库名字", 1);

    //获取SQLiteDatabase对象
    SQLiteDatabase sqLiteDatabase = appLibrary.getWriteDataBase(); 

# 3.常规操作

- 删除表

    appLibrary.deleteTable("表名");

- 重命名表

    appLibrary.deleteTable("旧的表名","新的表名");  

- 清空表  

    appLibrary.deleteTable("表名");  

- 执行任意sql语句(建议不使用这种，因为笔者封装了另外一套，方便简洁)

    appLibrary.getWriteDataBase().exeSQL("sql语句")


