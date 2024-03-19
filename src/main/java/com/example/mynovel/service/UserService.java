package com.example.mynovel.service;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.req.UserLoginReqDto;
import com.example.mynovel.dto.req.UserRegisterReqDto;
import com.example.mynovel.dto.resp.UserLoginRespDto;
import com.example.mynovel.dto.resp.UserRegisterRespDto;

public interface UserService {

    /**
     * 用户注册
     * @param userRegisterReqDto
     * @return
     */
    RestResp<UserRegisterRespDto> register(UserRegisterReqDto userRegisterReqDto);

    /**
     *
     * @param userLoginReqDto
     * @return
     */
    RestResp<UserLoginRespDto> login(UserLoginReqDto userLoginReqDto);
}
