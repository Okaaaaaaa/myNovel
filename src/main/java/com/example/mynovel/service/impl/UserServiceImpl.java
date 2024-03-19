package com.example.mynovel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mynovel.core.common.constant.ErrorCodeEnum;
import com.example.mynovel.core.common.exception.BusinessException;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.DatabaseConsts;
import com.example.mynovel.core.constant.SystemConfigConsts;
import com.example.mynovel.core.util.JwtUtils;
import com.example.mynovel.dao.entity.UserInfo;
import com.example.mynovel.dao.mapper.UserInfoMapper;
import com.example.mynovel.dto.req.UserLoginReqDto;
import com.example.mynovel.dto.req.UserRegisterReqDto;
import com.example.mynovel.dto.resp.UserLoginRespDto;
import com.example.mynovel.dto.resp.UserRegisterRespDto;
import com.example.mynovel.manager.redis.VerifyCodeManager;
import com.example.mynovel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final VerifyCodeManager verifyCodeManager;

    private final UserInfoMapper userInfoMapper;

    private final JwtUtils jwtUtils;

    /**
     * 校验用户的验证码、手机号，若通过，则在数据库中添加新用户
     * @param userRegisterReqDto
     * @return
     */
    @Override
    public RestResp<UserRegisterRespDto> register(UserRegisterReqDto userRegisterReqDto) {
        // 图形验证码是否输入正确
        if(!verifyCodeManager.imgVerifyCodeOk(
                userRegisterReqDto.getSessionId(),
                userRegisterReqDto.getVelCode())){
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        }

        //手机号是否已被注册
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq(DatabaseConsts.UserInfoTable.COLUMN_USERNAME,userRegisterReqDto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        //已注册过
        if(userInfoMapper.selectCount(userInfoQueryWrapper)>0){
            throw new BusinessException(ErrorCodeEnum.USER_NAME_EXIST);
        }

        /**
        * TODO
        * 2024/3/15
        * 存入数据库，什么是加盐算法
        */

        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(DigestUtils.md5DigestAsHex(userRegisterReqDto.getPassword().getBytes(StandardCharsets.UTF_8)));
        userInfo.setUsername(userRegisterReqDto.getUsername());
        userInfo.setNickName(userRegisterReqDto.getUsername());
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());
        userInfo.setSalt("0");
        userInfoMapper.insert(userInfo);

        // 删除验证码
        verifyCodeManager.removeImgVerifyCode(userRegisterReqDto.getSessionId());

        // mybatis-plus自动生成
        log.info("生成用户ID："+userInfo.getId());

        // 生成jwt并返回
        return RestResp.ok(
                UserRegisterRespDto.builder()
                        .token(jwtUtils.generateToken(userInfo.getId(), SystemConfigConsts.NOVEL_FRONT_KEY))
                        .uid(userInfo.getId())
                        .build()
        );
    }

    /*

     */
    @Override
    public RestResp<UserLoginRespDto> login(UserLoginReqDto userLoginReqDto) {
        //查询用户是否存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.UserInfoTable.COLUMN_USERNAME,userLoginReqDto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        if(Objects.isNull(userInfo)){
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }

        //判断密码是否正确
        if(!Objects.equals(userInfo.getPassword(),
                DigestUtils.md5DigestAsHex(userLoginReqDto.getPassword().getBytes(StandardCharsets.UTF_8)))){
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }

        //登录成功，生成jwt并返回
        return RestResp.ok(UserLoginRespDto.builder()
                .token(jwtUtils.generateToken(userInfo.getId(),SystemConfigConsts.NOVEL_FRONT_KEY))
                .uid(userInfo.getId())
                .nickName(userInfo.getNickName())
                .build());
    }


}
