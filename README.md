# nutz
记录nutz的学习过程

######书中的目录结构并非maven项目工程的目录结构

对应关系如下：

| 书中的目录名称 | maven中的项目目录名称 |
| -------------- | --------------------- |
| src            | src/main/java         |
| conf           | src/main/resources    |
| WebContent     | src/main/webapp       |

##### >> 和 ->

```
>>> 和 -> 分别是redirect和forward的缩写
>>>:xxx 即302重定向到xxx
->:xxx 是内部重定向，但是这条在shiro环境下会报错
```

##### @At的重要性

其实我个人理解 @At 有点类似于springmvc中的requestMapping，即根据其对应的名称来映射处理方法





