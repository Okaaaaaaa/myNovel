package com.example.mynovel.core.auth;

import com.example.mynovel.core.common.constant.ErrorCodeEnum;
import com.example.mynovel.core.common.exception.BusinessException;
import com.example.mynovel.core.constant.SystemConfigConsts;
import com.example.mynovel.core.util.JwtUtils;
import com.example.mynovel.dto.UserInfoDto;
import com.example.mynovel.manager.cache.UserInfoCacheManager;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 策略模式实现用户认证、授权功能
 *
 * @author peter
 */
public interface AuthStrategy {

    /**
     * 用户认证授权
     *
     * @param token      登录 token
     * @param requestUri 请求的 URI
     * @throws BusinessException 认证失败则抛出业务异常
     */
    void auth(String token, String requestUri) throws BusinessException;

    /**
     * 前台多系统单点登录统一账号认证（门户系统、作家系统以及后面会扩展的漫画系统和视频系统等）
     *
     * @param jwtUtils             jwt 工具
     * @param userInfoCacheManager 用户缓存管理对象
     * @param token                token 登录 token
     * @return 用户ID
     */
    default Long authSSO(JwtUtils jwtUtils, UserInfoCacheManager userInfoCacheManager,
                         String token) {
        // 1.解析携带的token，获取用户id
        // 1.1 token为空 - 抛出用户登录过期异常
        if(!StringUtils.hasText(token)){
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }
        // 1.2 获取token中携带的用户id
        Long userId =  jwtUtils.parseToken(token,SystemConfigConsts.NOVEL_FRONT_KEY);
        // 1.3 token解析失败，没有获取用户id
        if(Objects.isNull(userId)){
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }

        // 2.获取userInfo
        UserInfoDto userInfo = userInfoCacheManager.getUser(userId);
        // 2.1 没有查到用户信息
        if(Objects.isNull(userInfo)){
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        // 2.2 设置 userId 到当前线程
        UserHolder.setUserId(userId);

        // 3.返回userId
        return userId;

    }

}
