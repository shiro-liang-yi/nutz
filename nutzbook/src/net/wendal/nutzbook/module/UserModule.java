package net.wendal.nutzbook.module;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CheckSession;

import net.wendal.nutzbook.bean.User;

/**
 * 配置Ioc相关注解及属性,即IocBean,Inject和Dao属性,哦哦,还有At
 * 
 * @author liangshuai
 * @date 2018年6月8日 下午4:49:57
 */
@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦,意思是声明为Ioc容器中的一个Bean
@At("/user") //整个模块的路径前缀
@Ok("json:{locked:'password|salt',ignoreNull:true}") //将@Ok注解修改成这样，密码和salt也不可以发送到浏览器去;忽略空属性的jjson输出
@Fail("http:500") //抛出异常的话，就走500页面
@Filters(@By(type=CheckSession.class,args= {"me","/"})) //含义是如果当前Session没有带me这个attr，就跳转到 / 页面，即首页
public class UserModule {

	@Inject //注入同名的一个ioc对象
	protected Dao dao; // 就这么注入了,有@IocBean它才会生效

	/**
	 * 测试使用
	 * 统计用户数的方法
	 * @author liangshuai
	 * @date 2018年6月8日 下午4:52:16
	 * @return int
	 * @return
	 */
	@At // 这个注解一定必须有，相当于requestMapping等（我初步理解是这样）
	public int count() { 
		return dao.count(User.class);
	}

	/**
	 * 添加登录方法 在UserModule类加入一个方法
	 * 
	 * @author liangshuai
	 * @date 2018年6月8日 下午5:11:55
	 * @return Object
	 * @param name
	 * @param password
	 * @param session
	 * @return
	 */
	@At
	@Filters //同时为login方法设置为空的过滤器，不然就没法登录了;覆盖UserModule类的@Filter设置，因为登录可不能要求是个已经登录的Session
	public Object login(@Param("username") String name, @Param("password") String password, HttpSession session) {
		User user = dao.fetch(User.class, Cnd.where("name", "=", name).and("password", "=", password));
		if (user == null) {
			return false;
		} else {
			session.setAttribute("me", user.getId());
			return true;
		}
	}

	@At
	@Ok(">>:/") // 这个表示302重定向 ; 跟其它方法不同，这个方法完成后就跳转首页了
	public void logout(HttpSession session) {
		session.invalidate();
	}

	/**
	 * 当前没有引入校验相关的jar或辅助类,那我们就手工建一个帮助方法吧 打开UserModule,添加一个方法checkUser
	 * 
	 * @author liangshuai
	 * @date 2018年6月8日 下午7:07:38
	 * @return String
	 * @param user
	 * @param create
	 * @return
	 */
	protected String checkUser(User user, boolean create) {
		if (user == null) {
			return "空对象";
		}
		if (create) {
			if (Strings.isBlank(user.getName()) || Strings.isBlank(user.getPassword()))
				return "用户名/密码不能为空";
		} else {
			if (Strings.isBlank(user.getPassword()))
				return "密码不能为空";
		}
		String passwd = user.getPassword().trim();
		if (6 > passwd.length() || passwd.length() > 12) {
			return "密码长度错误";
		}
		user.setPassword(passwd);
		if (create) {
			int count = dao.count(User.class, Cnd.where("name", "=", user.getName()));
			if (count != 0) {
				return "用户名已经存在";
			}
		} else {
			if (user.getId() < 1) {
				return "用户Id非法";
			}
		}
		if (user.getName() != null)
			user.setName(user.getName().trim());
		return null;
	}
	
	/**
	 * add方法-增加用户
	 * 假设客户端通过表单把新建用户的属性都发送过来了, 那么在UserModule中就建一个add方法如下
	 * @author liangshuai
	 * @date 2018年6月8日 下午7:21:02
	 * @return Object
	 * @param user
	 * @return
	 */
	@At
	public Object add(@Param("..")User user) { //两个点号是按照对象属性一一设置
		NutMap re = new NutMap();
		String msg = checkUser(user,true);
		if(msg != null) {
			return re.setv("ok",false).setv("msg",msg);
		}
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		user = dao.insert(user);
		return re.setv("ok", true).setv("data", user);
	}
	
	/**
	 * update方法--更新用户属性
	 * @author liangshuai
	 * @date 2018年6月8日 下午7:27:41
	 * @return Object
	 * @param user
	 * @return
	 */
	@At
	public Object update(@Param("..")User user) {
		NutMap re = new NutMap();
		String msg = checkUser(user, false);
		if(msg != null) {
			return re.setv("ok", false).setv("msg", msg);
		}
		user.setName(null); //不允许更新用户名
		user.setCreateTime(null); //也不允许更新创建时间
		user.setUpdateTime(new Date()); //设置正确的更新时间
		dao.updateIgnoreNull(user);//真正更新的其实只有password和salt
		return re.setv("ok", true);
	}
	
	/**
	 * delete方法--删除指定用户,最直接的方法,通过id删除用户
	 * 留意一下,其中的@Attr是取Session/Request中的me属性
	 * @author liangshuai
	 * @date 2018年6月8日 下午7:35:37
	 * @return Object
	 * @param id
	 * @param me
	 * @return
	 */
	@At
	public Object delete(@Param("id")int id, @Attr("me")int me) {
		if(me == id) {
			return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
		}
		dao.delete(User.class,id); //再严谨一些的话，需要判断是否为 > 0
		return new NutMap().setv("ok", true);
	}
	
	/**
	 * query方法--根据条件查询用户
	 * 根据名字查就可以了,也没其他可以查询的信息了吧,当然加上分页咯
	 * @author liangshuai
	 * @date 2018年6月8日 下午7:42:37
	 * @return Object
	 * @param name
	 * @param pager
	 * @return
	 */
	@At
	public Object query(@Param("name")String name,@Param("..")Pager pager) {
		Cnd cnd = Strings.isBlank(name)? null : Cnd.where("name","like","%" + name + "%");
		QueryResult qr = new QueryResult();
		qr.setList(dao.query(User.class,cnd,pager));
		pager.setRecordCount(dao.count(User.class,cnd));
		qr.setPager(pager);
		return qr; //默认分页是第1页，每页显示20条
	}
	
	/**
	 * 因为我们打算把 jsp 放在 WEB-INF 下，然后 WEB-INF 下的文件是不能直接访问的，所以加一个跳转的方法
	 * @author liangshuai
	 * @date 2018年6月9日 下午4:34:40
	 * @return void
	 */
	@At("/")
	@Ok("jsp:jsp.user.list") //真实路径是 /WEB-INF/jsp/user/list.jsp
	public void index() {
		
	}
}
