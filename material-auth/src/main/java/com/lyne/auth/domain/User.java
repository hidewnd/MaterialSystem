package com.lyne.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author lyne
 */
public class User implements UserDetails, Serializable {
    private Long userId;
    private String username;
    private String password;

    private int delFlag; // 是否删除
    private int status; // 账号状态
    private List<GrantedAuthority> authorities;
    private Set<String> permission;
    private String token;

    public User() {
    }

    public User(String username, String password, int delFlag, int status,
                List<GrantedAuthority> authorities, Set<String> permission, String token) {
        this.username = username;
        this.password = password;
        this.delFlag = delFlag;
        this.status = status;
        this.authorities = authorities;
        this.permission = permission;
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setDelFlag(int delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    public User setStatus(int status) {
        this.status = status;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    public Long getUserId() {
        return userId;
    }

    public User setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean isAccountNonExpired() {
        return delFlag == 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 0;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public int getStatus() {
        return status;
    }

    public User setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public Set<String> getPermission() {
        return permission;
    }

    public User setPermission(Set<String> permission) {
        this.permission = permission;
        return this;
    }

    public String getToken() {
        return token;
    }

    public User setToken(String token) {
        this.token = token;
        return this;
    }
}
