package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;

/**
 * 值对象
 * 用户详情信息
 *
 * @author lyne
 */
@TableName("sys_account_info")
public class SysAccountInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一ID
     */
    @TableId("accountId")
    private String accountId;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 签名
     */
    private String sign;

    public SysAccountInfo() {
    }

    public SysAccountInfo(String accountId) {
        this.accountId = accountId;
    }

    public SysAccountInfo(String accountId, Integer sex, Integer age, String phone, String email, String avatar,
                          String sign, String createBy, String updateBy, String remark) {
        super(createBy, updateBy, remark);
        this.accountId = accountId;
        this.sex = sex;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.sign = sign;
    }

    public SysAccountInfo(String accountId, Integer sex, Integer age, String phone,
                          String email, String avatar, String sign) {
        this.accountId = accountId;
        this.sex = sex;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.sign = sign;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "SysAccountInfo{" +
                "accountId=" + accountId +
                ", sex=" + sex +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sign='" + sign + '\'' +
                super.toString() +
                '}';
    }
}
