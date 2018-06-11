package net.wendal.nutzbook;

import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
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

@ChainBy(args="mvc/nutzbook-mvc-chain.js")
@Localization(value="msg/",defaultLocalizationKey="zh-CN")
@Fail("jsp:jsp.500")
@Ok("json:full")
@SetupBy(value=MainSetup.class)  //打开MainModule类, 配置@SetupBy, 引用刚刚创建的MainSetup
@IocBy(type=ComboIocProvider.class, args={
        "*js", "ioc/",
                          "*anno", "net.wendal.nutzbook",
                          "*tx",
                          "*quartz"})
@Modules(scanPackage=true)
public class MainModule {

}
