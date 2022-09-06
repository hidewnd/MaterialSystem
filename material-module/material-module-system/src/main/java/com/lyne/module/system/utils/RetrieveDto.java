package com.lyne.module.system.utils;

import com.lyne.common.core.types.SysAccountInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 密码找回对象实体
 * @author lyne
 */
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveDto {

    /**
     * 用户名
     */
    private String username;

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
     * 签名
     */
    private String sign;


    public static RetrieveDto instantiation(String username, SysAccountInfo sysAccountInfo) {
        RetrieveDto retrieveDto = new RetrieveDto();
        retrieveDto.setUsername(username);
        retrieveDto.setAge(sysAccountInfo.getAge());
        retrieveDto.setEmail(sysAccountInfo.getEmail());
        retrieveDto.setPhone(sysAccountInfo.getPhone());
        retrieveDto.setSign(sysAccountInfo.getSign());
        return retrieveDto;
    }

    public SysAccountInfo getInfo() {
        SysAccountInfo sysAccountInfo = new SysAccountInfo();
        sysAccountInfo.setAge(this.getAge());
        sysAccountInfo.setSex(this.getSex());
        sysAccountInfo.setEmail(this.getEmail());
        sysAccountInfo.setPhone(this.getPhone());
        sysAccountInfo.setSign(this.getSign());
        return sysAccountInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "RetrieveDto{" +
                "username='" + username + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
