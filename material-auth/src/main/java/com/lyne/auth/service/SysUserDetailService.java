package com.lyne.auth.service;

import cn.hutool.core.convert.Convert;
import com.lyne.api.system.service.FeignSystemService;
import com.lyne.auth.domain.User;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.SysAccount;
import com.lyne.common.core.types.SysRole;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import javax.annotation.Resource;

/**
 * 用户查询服务
 *
 * @author lyne
 */
@Service
public class SysUserDetailService implements UserDetailsService {
    @Resource
    private FeignSystemService feignQueryService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        R<?> result = feignQueryService.queryOne("account", "name", username, SecurityConstants.INNER);
        if (result.getCode() == HttpStatus.SUCCESS) {
            SysAccount sysAccount = Convert.convert(SysAccount.class, result.getData());
            User user = new User()
                    .setUsername(sysAccount.getUsername())
                    .setPassword(passwordEncoder.encode(sysAccount.getPassword()))
                    .setUserId(Convert.toLong(sysAccount.getAccountId()));
            String[] roleNames = sysAccount.getRoles().stream()
                    .filter(sysRole -> sysRole.getDelFlag() == 0)
                    .filter(sysRole -> sysRole.getStated() == 0)
                    .sorted(Comparator.comparingInt(SysRole::getRoleSort))
                    .map((SysRole::getRoleName))
                    .toArray(String[]::new);
            user.setAuthorities(AuthorityUtils.createAuthorityList(roleNames));
            HashSet<String> perms = new HashSet<>();
            for (SysRole sysRole : sysAccount.getRoles()) {
                if (sysRole.getRoleName().equals("admin")) {
                    perms.add("*:*:*");
                    break;
                }
            }
            user.setPermission(perms);
            return user;
        } else {
            throw new UsernameNotFoundException("用户名{" + username + "}不存在");
        }
    }
}
