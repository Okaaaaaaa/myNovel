package com.example.mynovel.core.auth;

import com.example.mynovel.core.common.exception.BusinessException;
import com.example.mynovel.core.util.JwtUtils;
import com.example.mynovel.manager.cache.UserInfoCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Peter
 */
@RequiredArgsConstructor
@Component
public class FrontAuthStrategy implements AuthStrategy{

    private final JwtUtils jwtUtils;

    private final UserInfoCacheManager userInfoCacheManager;

    /**
     * 用户认证授权
     *
     * @param token      登录 token
     * @param requestUri 请求的 URI
     * @throws BusinessException 认证失败则抛出业务异常
     */
    @Override
    public void auth(String token, String requestUri) throws BusinessException {
        // 统一账号认证
        authSSO(jwtUtils,userInfoCacheManager,token);
    }
}
