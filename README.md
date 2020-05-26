
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

## 2.理论介绍

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

