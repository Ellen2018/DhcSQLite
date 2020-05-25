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

&emsp;&emsp;步骤一：新建一个类去继承ZxyLibrary,首先需要一个Library对象，你必须继承ZxyLibrary，并且重写哪些该重写的方法，至于这些方法有什么用,请看注释:[代码查阅](https://github.com/Ellen2018/DhcSQLite/blob/master/app/src/main/java/com/ellen/dhcsqlite/SQliteLibrary.java)

&emsp;&emsp;选择好需要和数据库进行绑定的类，例如我要绑定的是Student类，那么我就需要创建一个StudentTable的类并让它继承ZxyReflectionTable<Student>,代码如下所示：[代码查阅](https://github.com/Ellen2018/DhcSQLite/blob/master/app/src/main/java/com/ellen/dhcsqlite/StudentTable.java)

&emsp;&emsp;步骤三:操作表。接下来，我们就可以任性的进行创建表和操作表了，笔者提供了两种方式来操作表，第一种是笔者封装的方法，第二种是通过[ZxySQLiteCreate](https://github.com/Ellen2018/ZxySQLiteCreate)
库来构建add,delete,where,order等语句来完成操作，由于封装的实在太多，待笔者一点点进行整理吧。[代码查阅](https://github.com/Ellen2018/DhcSQLite/blob/master/app/src/main/java/com/ellen/dhcsqlite/MainActivity.java)  
  
&emsp;&emsp;关于注解的使用规则，阅读下面Student Bean类很明显就能知道用法:[代码查阅](https://github.com/Ellen2018/DhcSQLite/blob/master/app/src/main/java/com/ellen/dhcsqlite/Student.java)