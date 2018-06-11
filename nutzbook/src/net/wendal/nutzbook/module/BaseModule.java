package net.wendal.nutzbook.module;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.random.R;

import net.wendal.nutzbook.service.EmailService;

/**
 * Module类的一些属性总是雷同的,所以,新建一个BaseModule类
 * 【注意，基类一般都是抽象类】
 * @author liangshuai
 * @date 2018年6月11日 上午11:58:33
 */
public abstract class BaseModule {

	/** 注入同名的一个ioc对象 */
    @Inject protected Dao dao;
    
    /**
     * 因为这是一个基类，一定会被其它的类所继承，因此，此处的权限修饰符是基于安全的最小可用的修饰符protected
     * —————————————————————————————————————————————————————————————————————————————————————————————————————————
     * |private				|	     protected			|	   default		|	public							|
     * |只有本类能够访问		|放大到子类也可访问(其他类不能)	|		同包可以		|	随意访问，不具备子类关系的其他类也可访问	|
     *——————————————————————————————————————————————————————————————————————————————————————————————————————————
     */
    @Inject protected EmailService emailService;
    
    protected byte[] emailKEY = R.sg(24).next().getBytes();
}
