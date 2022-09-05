# day11 jni开发

**今日目标**：在安卓程序中实现**Java**和**C**代码的相互调用。

JNI，java native interface ，Java本地开发接口，实现JAVA和C语言之间的相互调用。



## 1.NDK

NDK是JNI开发的工具包。

NDK，Native Develop Kits，本地开发工具（在Android Studio中下载即可）。

![image-20220224201542989](assets/image-20220224201542989.png)



## 2.创建项目

- 普通项目：Empty Activity（Java） 

- jni项目：Native C++（Java + C）

  ```
  创建的项目多了一些内容和配置（基于C++实现了一个算法，并在Java中进行了调用）。
  - 有了默认配置后，我们就不需要自己的手动配置了。
  - 会生成一些我们用不到的默认文件，等我们学会自己再回来删除他默认的这些文件。
  ```

![image-20220804115801693](assets/image-20220804115801693.png)

![image-20220804115837738](assets/image-20220804115837738.png)

![image-20220804115913833](assets/image-20220804115913833.png)

![image-20220804120210189](assets/image-20220804120210189.png)





## 3.快速上手



### 3.1 新建Java类

新建一个java的类，模拟实现项目中的核心算法。

- v0，用Java实现的算法
- v1，用C实现算法

![image-20220804121020772](assets/image-20220804121020772.png)



### 3.2 创建C文件

![image-20220804121153078](assets/image-20220804121153078.png)

![image-20220804121229962](assets/image-20220804121229962.png)

![image-20220804121543210](assets/image-20220804121543210.png)

```c
#include <jni.h>

JNIEXPORT jint
JNICALL Java_com_nb_s4luffy_EncryptUtils_v1(JNIEnv* env,jclass clazz, jint v1,jint v2) {
    // 编写C语言的代码
    return v1 + v2;
}
```



不想手动编写的话，可以用命令自动生成：

```
>>>cd app/src/main/java
>>>javah com.nb.s4luffy.EncryptUtils
com.nb.s3song.EncryptUtils
```

![image-20220519183922056](assets/image-20220519183922056.png)



### 3.3 C编译配置

![image-20220804142811708](assets/image-20220804142811708.png)

再次回到 enc.c 文件中，再点击sync now就不再提示错误了。



### 3.4 加载C文件

![image-20220804142438256](assets/image-20220804142438256.png)



### 3.5 Java调用C

![image-20220804142849525](assets/image-20220804142849525.png)



### 3.7 删除默认native-lib

在创建项目时，自动生成的native-lib可以删除了。

![image-20220519190259378](assets/image-20220519190259378.png)



![image-20220519190316399](assets/image-20220519190316399.png)



![image-20220519190346466](assets/image-20220519190346466.png)



### 3.8 逆向和反编译

![image-20220804143018583](assets/image-20220804143018583.png)



![image-20220804143133059](assets/image-20220804143133059.png)



![image-20220804143233874](assets/image-20220804143233874.png)



![image-20220804143544619](assets/image-20220804143544619.png)





![image-20220804143601037](assets/image-20220804143601037.png)







## 4.关于类型

![image-20220804152940854](assets/image-20220804152940854.png)

```java
package com.nb.s4luffy;

class EncryptUtils {

    static {
        System.loadLibrary("enc");
    }

    public static native int v1(int v1, int v2);
    
    public static native String v1(String origin);
}
```

```C
JNIEXPORT jint
JNICALL Java_com_nb_s4luffy_EncryptUtils_v1(JNIEnv *env, jclass clazz, jint v1, jint v2) {
    // 编写C语言的代码
    return v1 + v2;
}
```

```c
JNIEXPORT jstring
JNICALL Java_com_nb_s4luffy_EncryptUtils_v2(JNIEnv *env, jclass obj, jstring origin) {
    // 获取参数中传入的字符串
    const char *string = (*env)->GetStringUTFChars(env, origin, 0);

    char data[4] = {string[0], string[2], string[4]}; // wpi

    // C语言中的字符串转换成jstring
    jstring response = (*env)->NewStringUTF(env, data);
    return response; // jstring -> Java的字符串
}
```

![image-20220804153306910](assets/image-20220804153306910.png)



## 5.一波案例（Java调C）



### 5.1 数字处理

```java
public static native int v1(int v1, int v2);
```

```c
#include <jni.h>

JNIEXPORT jint

JNICALL Java_com_nb_s4luffy_EncryptUtils_v1(JNIEnv *env, jclass clazz, jint v1, jint v2) {
    return v1 + v2;
}
```



### 5.2 字符串修改-指针

```java
public static native String v2(String old);
```

```c
#include <jni.h>
#include <string.h>
#include <syslog.h>

JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v2(JNIEnv *env, jclass clazz, jstring old) {
	
    // char info[]  = {'w','w','p','e',..'w'...};
    char *info = (*env)->GetStringUTFChars(env, old, 0);
    syslog(LOG_ERR, "%s", info);
    
    info += 1;
    *info = 'w';

    info += 3;
    *info = 'w';

    info -= 4;

    syslog(LOG_ERR, "%s", info);

    return (*env)->NewStringUTF(env, info);
}
```



### 5.3 字符串修改-数组

```java
public static native String v3(String old);
```

```c
JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v3(JNIEnv *env, jclass clazz, jstring old) {

    char *info = (*env)->GetStringUTFChars(env, old, 0);
    info[0] = 'x';
    info[5] = 'x';
    
    return (*env)->NewStringUTF(env, info);
}
```





















### 5.4 字符串拼接

```java
public static native String v4(String name, String role);
```

```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>


int GetStringLen(char *dataString) {
    int count = 0;
    for (int i = 0; dataString[i] != '\0'; i++) {
        count += 1;
    }
    return count;
}

JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v4(JNIEnv *env, jclass clazz, jstring name, jstring role) {
	
    // 字符数组=指针
    char *nameString = (*env)->GetStringUTFChars(env, name, 0);
    char *roleString = (*env)->GetStringUTFChars(env, role, 0);
	
    // dingningdsb
    char *result = malloc(GetStringLen(nameString) + GetStringLen(roleString) + 1);
    
    strcpy(result, nameString);
    strcat(result, roleString);

    syslog(LOG_ERR, "%s", result);

    return (*env)->NewStringUTF(env, result);
}
```



### 5.5 字符处理

```java
String n5 = EncryptUtils.v5("name=wupeiqi&age=19");
```

```java
public static native String v5(String data);
```

```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>

int GetStringLen(char *dataString) {
    int count = 0;
    for (int i = 0; dataString[i] != '\0'; i++) {
        count += 1;
    }
    return count;
}


JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v5(JNIEnv *env, jclass clazz, jstring data) {
	
    // "name=wupeiqi&age=19"
    char *urlParams = (*env)->GetStringUTFChars(env, data, 0);
    int size = GetStringLen(urlParams);
	
    // v34 = {1,9,,,,,}
    char v34[size * 2];
    char *v28 = v34;

    for (int i = 0; urlParams[i] != '\0'; i++) {
        //syslog(LOG_ERR, "%02x", urlParams[i]);
        sprintf(v28, "%02x", urlParams[i]);
        v28 += 2;
    }

    return (*env)->NewStringUTF(env, v34);
}
```



### 5.6 字节处理

```java
String n6 = EncryptUtils.v6("name=wupeiqi&age=19".getBytes());
```

```java
public static native String v6(byte[] data);
```

```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v6(JNIEnv *env, jclass clazz, jbyteArray data) {

//    jbyte *byteArray = (*env)->GetByteArrayElements(env, data, 0);
    char *byteArray = (*env)->GetByteArrayElements(env, data, 0);
    int size = (*env)->GetArrayLength(env, data);


    char v34[size * 2];
    char *v28 = v34;

    for (int i = 0; byteArray[i] != '\0'; i++) {
        syslog(LOG_ERR, "%02x", byteArray[i]);
        sprintf(v28, "%02x", byteArray[i]);
        v28 += 2;
    }

    return (*env)->NewStringUTF(env, v34);
}
```



### 5.7 字节处理-案例

```java
public static native String v7(byte[] data);
```

```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>


JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v7(JNIEnv *env, jclass clazz, jbyteArray data) {

    char *byteArray = (*env)->GetByteArrayElements(env, data, 0);
    int size = (*env)->GetArrayLength(env, data);


    char v34[size * 2];
    char *v28 = v34;

    int v29 = 0;
    do {
        sprintf(v28, "%02x", byteArray[v29++]);
        v28 += 2;
    } while (v29 != size);

    return (*env)->NewStringUTF(env, v34);
}
```



## 6.一波案例（C调Java）

### 6.1 静态方法

```java
String n8 = EncryptUtils.v8();
```



```java
package com.nb.s4luffy;

class EncryptUtils {
    static {
        System.loadLibrary("enc");
    }
    
    public static native String v8();
}
```



```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>


JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v8(JNIEnv *env, jclass clazz) {

    // 找到类
    jclass cls = (*env)->FindClass(env, "com/nb/s4luffy/SignQuery");

    // 找到方法
    jmethodID method1 = (*env)->GetStaticMethodID(env, cls, "getPart1", "()Ljava/lang/String;");
    jmethodID method2 = (*env)->GetStaticMethodID(env, cls, "getPart2", "(I)Ljava/lang/String;");
    jmethodID method3 = (*env)->GetStaticMethodID(env, cls, "getPart3","(Ljava/lang/String;)Ljava/lang/String;");
    jmethodID method4 = (*env)->GetStaticMethodID(env, cls, "getPart4", "(Ljava/lang/String;I)I");


    // 执行方法
    jstring res1 = (*env)->CallStaticObjectMethod(env, cls, method1);
    jstring res2 = (*env)->CallStaticObjectMethod(env, cls, method2, 100);
    jstring res3 = (*env)->CallStaticObjectMethod(env,cls,method3,(*env)->NewStringUTF(env, "hahahahh"));
    jint res4 = (*env)->CallStaticIntMethod(env,cls,method4,(*env)->NewStringUTF(env, "hahahahh"),18);

    char *p1 = (*env)->GetStringUTFChars(env, res1, 0);
    char *p2 = (*env)->GetStringUTFChars(env, res2, 0);
    char *p3 = (*env)->GetStringUTFChars(env, res3, 0);

    char *result = malloc(50);
    strcat(result,p1);
    strcat(result,p2);
    strcat(result,p3);

    return (*env)->NewStringUTF(env, result);
}
```



```java
package com.nb.s4luffy;

public class SignQuery {

    public static String getPart1() {
        return "wupeiqi";
    }

    public static String getPart2(int len) {
        return "root".substring(2);
    }

    public static String getPart3(String prev) {
        return "xxx";
    }

    public static int getPart4(String prev, int v1) {
        return 100;
    }
}
```



### 6.2 实例方法

```java
String n8 = EncryptUtils.v8();
```

```java
package com.nb.s4luffy;

class EncryptUtils {
    static {
        System.loadLibrary("enc");
    }
    
    public static native String v9();
}
```



```c
#include <jni.h>
#include <string.h>
#include <syslog.h>
#include<stdlib.h>


JNIEXPORT jstring

JNICALL
Java_com_nb_s4luffy_EncryptUtils_v9(JNIEnv *env, jclass clazz) {

    // 找到类
    jclass cls = (*env)->FindClass(env, "com/nb/s4luffy/SignQuery2");

    // 找到构造方法
    jmethodID init = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;I)V");

    // 实例化对象 new SignQuery2(...)
    jobject cls_obj = (*env)->NewObject(env, cls, init, (*env)->NewStringUTF(env, "hahahahh"), 22);

    // 找到方法
    jmethodID method1 = (*env)->GetMethodID(env, cls, "getPart1", "()Ljava/lang/String;");
    jmethodID method2 = (*env)->GetMethodID(env, cls, "getPart2", "(I)Ljava/lang/String;");
    jmethodID method3 = (*env)->GetMethodID(env, cls, "getPart3","(Ljava/lang/String;)Ljava/lang/String;");
    jmethodID method4 = (*env)->GetMethodID(env, cls, "getPart4", "(Ljava/lang/String;I)I");


    // 执行方法
    jstring res1 = (*env)->CallObjectMethod(env, cls_obj, method1);
    jstring res2 = (*env)->CallObjectMethod(env, cls_obj, method2, 100);
    jstring res3 = (*env)->CallObjectMethod(env,cls_obj,method3,(*env)->NewStringUTF(env, "hahahahh"));
    jint res4 = (*env)->CallIntMethod(env,cls_obj,method4,(*env)->NewStringUTF(env, "hahahahh"),18);


    char *p1 = (*env)->GetStringUTFChars(env, res1, 0);

    return (*env)->NewStringUTF(env, p1);
}
```



```java
package com.nb.s4luffy;

public class SignQuery {
    String name;
    String city;
    int count;

    public SignQuery(String city, int count) {
        this.name = "wupeiqi";
        this.city = city;
        this.count = count;
    }

    public String getPart1() {
        return this.name;
    }

    public String getPart2(int len) {
        return "root".substring(2);
    }

    public String getPart3(String prev) {
        return "xxx-";
    }

    public int getPart4(String prev, int v1) {
        return 100;
    }
}
```



## 7.静态和动态注册



### 7.1 静态注册

上述编写的C语言的函数和Java的对应关系，在函数名上就可以体现，例如：

```
Java_com_nb_s2long_EncryptUtils_s2
Java_com_nb_s2long_EncryptUtils_s5
Java_com_nb_s2long_EncryptUtils_s8
```

这种称为静态注册，如果是静态注册，那么在逆向时，是比较方便的，直接可以找到函数在C中的实现。例如：车智赢。

![image-20220519193907236](assets/image-20220519193907236.png)



![image-20220519193832337](assets/image-20220519193832337.png)

![image-20220519193843997](assets/image-20220519193843997.png)

![image-20220519193944611](assets/image-20220519193944611.png)



### 7.2 动态注册

```java
package com.nb.s3jni;

class DynamicUtils {

    static {
        System.loadLibrary("dynamic");
    }

    public static native int add(int v1, int v2);
}

```

![image-20220519194113707](assets/image-20220519194113707.png)





```c
#include <jni.h>


jint plus(JNIEnv *env, jobject obj, jint v1, jint v2) {

    //    jclass clazz = (*env)->FindClass(env, "com/nb/fucker/DynamicUtils");
    //    jmethodID id = (*env)->GetMethodID(env, clazz, "", "(D)V");
    //    (*env)->NewObject(env,clazz,id)
    // 调用Java中类中的方法。
    return v1 + v2;
}


static JNINativeMethod gMethods[] = {
        {"add", "(II)I", (void *) plus},
};


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    JNIEnv *env = NULL;

    // 在java虚拟机中获取env
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // 找到Java中的类
    jclass clazz = (*env)->FindClass(env, "com/nb/s3jni/DynamicUtils");

    // 将类中的方法注册到JNI中 (RegisterNatives)
    int res = (*env)->RegisterNatives(env, clazz, gMethods, 1);
    if (res < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

```

![image-20220519194142402](assets/image-20220519194142402.png)



![image-20220519194453158](assets/image-20220519194453158.png)



这种情况下，在逆向时，就不能直接找对应关系了，就需要去找jni_onload

![image-20220519194701822](assets/image-20220519194701822.png)

![image-20220519195032711](assets/image-20220519195032711.png)

![image-20220519195102598](assets/image-20220519195102598.png)

![image-20220519195118262](assets/image-20220519195118262.png)



![image-20220519195152779](assets/image-20220519195152779.png)





























## 总结

- 逆向时，很多算法基于Java实现。 jadx

- 逆向时，看到了。

  ```
  内部算法基于C实现。
  想要硬核破解
  	app解压
  	lib/libdym.so
  不需要:frida-rpc/unidbg
  ```

  ![image-20220806224614171](assets/image-20220806224614171.png)

- 反编译so -> ida

  - 静态注册（直接分析c）
    ![image-20220806224804795](assets/image-20220806224804795.png)

  - 动态注册（jni_onload找到方法，分析）

    ```
    JNI_Onload方法中
    	int res = (*env)->RegisterNatives(env, clazz, gMethods, 2);
    ```

    ![image-20220806224848007](assets/image-20220806224848007.png)

- 分析

  - c语言代码执行

  - 调用Java中的方法/对象

    ```
    "<init>"
    NewObject
    com/nb/s4dingning/SignQuery2
    "getPart1", "()Ljava/lang/String;"
    ```

    











































































