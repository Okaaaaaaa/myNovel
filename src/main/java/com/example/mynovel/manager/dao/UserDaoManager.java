package com.example.mynovel.manager.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mynovel.core.constant.DatabaseConsts;
import com.example.mynovel.dao.entity.UserInfo;
import com.example.mynovel.dao.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDaoManager {
    private final UserInfoMapper userInfoMapper;

    /**
     * 根据用户ID集合批量查询用户信息列表
     *
     * @param userIds 需要查询的用户ID集合
     * @return 满足条件的用户信息列表
     */
    public List<UserInfo> listUsers(List<Long> userIds) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(DatabaseConsts.CommonColumnEnum.ID.getName(), userIds);
        return userInfoMapper.selectList(queryWrapper);
    }
}
