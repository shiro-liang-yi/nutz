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
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.bean.User;

/**
 * 配置Ioc相关注解及属性,即IocBean,Inject和Dao属性,哦哦,还有At
 * 
 * @author liangshuai
 * @date 2018年6月8日 下午4:49:57
 */
@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/user")
@Ok("json")
@Fail("http:500")
public class UserModule {

	@Inject
	protected Dao dao; // 就这么注入了,有@IocBean它才会生效

	/**
	 * 测试使用
	 * 
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
	@Ok(">>:/") // 这个表示302重定向
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
	public Object add(@Param("..")User user) {
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
}
