package net.wendal.nutzbook.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 为了简化将来需要更多的Pojo(Bean),做个抽象的BasePojo,把共有的属性和方法统一一下
 * @author liangshuai
 * @date 2018年6月11日 上午11:52:04
 */
public abstract class BasePojo {

	/**
	 * 可以看到,这个类带了@Column注解的属性,所以继承这个类的子类的数据库属性均需要标注@Column了
	 * 另外这个类也覆盖了toString方法,默认输出为对象的Json字符串格式
	 */
    @Column("ct")
    protected Date createTime;
    @Column("ut")
    protected Date updateTime;

    public String toString() {
        // 这不是必须的, 只是为了debug的时候方便看
        return Json.toJson(this, JsonFormat.compact());
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
