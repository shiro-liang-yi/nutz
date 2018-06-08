package net.wendal.nutzbook;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * ComboIocProvider的args参数, 星号开头的是类名或内置缩写,剩余的是各加载器的参数
 * js 是JsonIocLoader,负责加载js/json结尾的ioc配置文件
 * anno 是AnnotationIocLoader,负责处理注解式Ioc, 例如@IocBean
 * tx 是TransIocLoader,负责加载内置的事务拦截器定义, 1.b.52开始自带
 * @author liangshuai
 * @date 2018年6月8日 下午4:03:20
 */
@SetupBy(value=MainSetup.class)  //打开MainModule类, 配置@SetupBy, 引用刚刚创建的MainSetup
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
        // 这个package下所有带@IocBean注解的类,都会登记上
                            "*anno", "net.wendal.nutzbook",
                            "*tx", // 事务拦截 aop
                            "*async"}) // 异步执行aop
@Modules(scanPackage=true)
public class MainModule {

}
