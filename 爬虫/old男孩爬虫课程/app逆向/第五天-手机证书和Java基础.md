# 手机证书的问题

当模拟器或者手机是安卓7以上的版本时，如果还是之前哪种配置就抓不到https的包，安卓7版本及以上的按照之前的操作安装的是用户级别的证书，所以抓不到，需要进一步的配置。

配置分为两中情况：

- 已解锁system分区
- 未解锁system分区

## 解锁system分区

解决system分区，可以理解成可以对system这个目录下面的文件进行操作了。有些手机root方式不同，在root时没有完全解锁system分区。

通过twrp去root时，一般都会解锁system分区。



## 已解锁system分区的证书安装

**电脑上的操作**

1. charles导出证书
2. 安装openssl对证书进行处理
3. 处理过后的证书进行改名，并传输到手机上

证书导出：

![image-20220824205934206](.\asset5\image-20220824205934206.png)



windows安装openssl

下载地址：`http://slproweb.com/products/Win32OpenSSL.html`

![image-20220824210145761](.\asset5\image-20220824210145761.png)

无脑下一步安装，记住安装的位置

![image-20220824211057885](.\asset5\image-20220824211057885.png)

打开cmd，用openssl处理保存下来的charles证书

```
openssl工具 x509 -subject_hash_old -in 证书文件路径

加引号的意思是因为Program和Files之间有空格，加引号表示为一条命令
"D:\Program Files\OpenSSL-Win64\bin\openssl.exe"  x509 -subject_hash_old -in charles_certificate.pem
```

![image-20220824211712056](.\asset5\image-20220824211712056.png)

改名

原来名称：`charles_certificate.pem`

重命名：`处理证书得到的字符串.0`

注意后缀名一定要该，有些是隐藏后缀名的，修改的话不会成功，需要显示文件的扩展名，再进行修改文件名。

![image-20220824212054493](.\asset5\image-20220824212054493.png)

将`862062d7.0`文件传输到手机的任意目录。

**手机上的操作**

1. 将处理好的证书传输到手机的目录下
2. 安装`RootExplorer_32578.apk`应用程序到手机，百度网盘中有
3. 打开`Root Explorer`管理器，返回根目录并挂载读写权限，将证书拷贝到系统目录
4. 关闭写权限

拷贝证书和apk文件

![image-20220824213110097](.\asset5\image-20220824213110097.png)

管理器设置权限，找到证书位置，拷贝证书到系统目录

![image-20220428193426241](.\asset5\image-20220428193426241.png)

执行完成后，回到根目录，将权限修改会 r/o 权限。

![image-20220428193533311](.\asset5\image-20220428193533311.png)

对于解锁了system的，做到这一步骤就可以抓取设备上的https包了。

## 未解锁system分区的证书安装

后续再加入。。

## 使用charles抓取手机上的包

手机上点击连接的无线网添加代理，代理的地址是第四天中的藏航准备网中配置的charles的端口和help中展示的ip

![image-20220824215354105](.\asset5\image-20220824215354105.png)

配置好后，手机和电脑在同一网段：

- 手机和电脑连接同一个无线
- 手机连接无线，电脑连接网线，这两个是同一个网段

电脑打开charles手机上运行爱安丘app登录，可以在电脑的charles中看到抓到了爱安丘的包

![image-20220824215648888](.\asset5\image-20220824215648888.png)

# java基础环境

目标是能看懂java代码，能分析其逻辑就可以了。在逆向的过程中只需要看到其流程即可，没必要死扣每个代码是什么意思。

先搞懂当前的，有时间再看一些额外的java内容。

java是编译型语言，在执行代码时，需要先对代码进行javac编译，在用java解释器执行编译好的代码。

![image-20220824221253332](asset5\image-20220824221253332.png)



## 安装java环境

安装JDK，JDK包含了JRE和开发必备的工具

- 编译工具javac.exe
- 运行工具java.exe

下载和安装的过程在第四天的笔记中。

## 安装IDE-Inteliji

下载地址[版本 2020.1.1]：`https://www.jetbrains.com/idea/download/other.html`

![image-20220824222446493](asset5\image-20220824222446493.png)

激活:

```
视频：https://www.zhihu.com/zvideo/1254435808801050624
激活包：在网盘共享目录，其他共享资料包文件夹【jetbrains-agent-latest.zip】
```



编写第一个java程序

![image-20220824223818955](asset5\image-20220824223818955.png)

# java基础语法

文件名：

```
一个文件中最多只能有一个public类 且 文件名必须和public类名一致。
如果文件中有多个类，文件名与public类名一致，其他的类不能有public修饰。
如果文件中有多个类 且 无public类，文件名可以是任意类名，即个某一个类的名字保持一致。
```

静态成员：

无需实例化就可以使用

```java
class MyTest{
	// void 修饰，表示无返回值
    public void f1(){
        System.out.println("f1");
    }
    // 静态成员,static修饰
    public static void f2(){
        System.out.println("f2");
    }
    // int 修饰，表示返回值必须是int类型
    public int get_data(){
        return 1;
    };
}

public class Hello {

    public static void main(String[] args) {
        MyTest.f2();
        
        //1.实例化
        MyTest obj = new MyTest();
        // 2.对象调用
        obj.f1();
        // -> 1
		System.out.println(obj.get_data());
    }
}
```

参数，要指定参数的类型

```java
class MyTest {

    public int f1(int a1, int a2) {
        int result = a1 + a2;
        return result;
    }

}

public class Hello {

    public static void main(String[] args) {
        MyTest obj = new MyTest();
        int v1 = obj.f1(1, 2);
    }
}
```

## 注释

```java
// 单行注释

/*
多行注释
*/
```

## 变量和常量

```java
// 定义变量
String name = "嘻嘻嘻";
// 修改变量
name = "jjj";

int age = 18;

// 常量,定义好后不允许修改
final int size = 18;
```

## 条件判断

if...else..

```
public class Hello {

    public static void main(String[] args) {
        int age = 19;

        if (age < 18) {
            System.out.println("少年");
        } else if (age < 40) {
            System.out.println("大叔");
        } else {
            System.out.println("老汉");
        }

    }
}
```

switch..case..

```
public class Hello {

    public static void main(String[] args) {
        int score = 19;

        switch (score) {
            case 10:
                System.out.println("xxx");
                System.out.println("xxx");
                System.out.println("xxx");
                break;
            case 20:
                System.out.println("xxx");
                System.out.println("xxx");
                System.out.println("xxx");
                break;
            default:
                System.out.println("xxx");
                break;
        }

    }
}
```

## 循环

while循环

```
public class Hello {

    public static void main(String[] args) {
        
        int count = 0;
        while (count < 3) {
            System.out.println("执行中...");
            count += 1;
        }
    }
}
```

do ... while(至少执行一次，再去走条件判断，是否接着循环执行)

```
public class Hello {
    public static void main(String[] args) {
        
        int count = 0;
        do {
            System.out.println("执行中...");
            count += 1;
        } while (count < 3);
        
    }
}
```

for 循环

```
public class Hello {

    public static void main(String[] args) {
        
        for (int i = 0; i < 10; i++) {
            System.out.println("哈哈哈");
        }
        
    }
}
```

```
public class Hello {

    public static void main(String[] args) {        
        String[] nameList = {"修仙", "肖峰", "麻子", "十分"};
        // nameList.length   4
        // nameList[0]

        for (int idx = 0; idx < nameList.length; idx++) {
            String ele = nameList[idx];
            System.out.println(ele);
        }
    }
}
```

