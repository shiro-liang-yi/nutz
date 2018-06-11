package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

/**
 * Nutz不要求用户的Pojo类必须继承某个类
 * 
 * @author liangshuai
 * @date 2018年6月8日 下午4:09:15
 */
@Table("t_user")
public class User extends BasePojo {

	/**
	 * @date 2018年6月11日 上午11:52:04 修改User类, 使其继承BasePojo,并删除createTime和updateTime
	 *       继承该类的子类的数据库属性均需要标注@Column
	 */
	@Id
	private int id;
	@Name
	@Column
	private String name;
	@Column("passwd")
	private String password;
	@Column
	private String salt;
	/**
	 * @date 2018年6月11日 下午1:21:53
	 * 建立关联关系:好了,这里是第一次用到NutDao的关联关系了, 打开User类,加入2行
	 */
	@One(target = UserProfile.class, field = "id", key = "userId")
	protected UserProfile profile;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}

}
