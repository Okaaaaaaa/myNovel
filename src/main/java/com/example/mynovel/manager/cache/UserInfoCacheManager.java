package com.example.mynovel.manager.cache;

import com.example.mynovel.core.constant.CacheConsts;
import com.example.mynovel.dao.entity.UserInfo;
import com.example.mynovel.dao.mapper.UserInfoMapper;
import com.example.mynovel.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Peter
 */
@RequiredArgsConstructor
@Component
public class UserInfoCacheManager {

    private final UserInfoMapper userInfoMapper;

    /**
     * 从数据库中查询UserInfo，将userID、User status存到redis中
     * @param userId
     * @return UserInfoDto
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
                value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long userId){
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .status(userInfo.getStatus()).build();
    }
}
