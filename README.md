
## 1.介绍

&emsp;&emsp;DhcSQLite是一款基于注解加反射的第三方SQLite库，基于SQLite进行的封装。用到了Java反射加注解技术，后期有时间改成注解加APT的方式。

## 2.如何使用？

&emsp;&emsp;步骤一：新建一个类去继承ZxyLibrary

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