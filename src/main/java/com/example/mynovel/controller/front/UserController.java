package com.example.mynovel.controller.front;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.ApiRouterConsts;
import com.example.mynovel.core.constant.SystemConfigConsts;
import com.example.mynovel.dto.req.UserCommentReqDto;
import com.example.mynovel.dto.req.UserLoginReqDto;
import com.example.mynovel.dto.req.UserRegisterReqDto;
import com.example.mynovel.dto.resp.UserLoginRespDto;
import com.example.mynovel.dto.resp.UserRegisterRespDto;
import com.example.mynovel.service.BookService;
import com.example.mynovel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserController", description = "前台门户-会员模块")
@SecurityRequirement(name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME)
@RequestMapping(ApiRouterConsts.API_FRONT_USER_URL_PREFIX)
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final BookService bookService;

    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public RestResp<UserRegisterRespDto> register(@Valid UserRegisterReqDto userRegisterReqDto){
        return userService.register(userRegisterReqDto);
    }

    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public RestResp<UserLoginRespDto> login(@Valid UserLoginReqDto userLoginReqDto){
        return userService.login(userLoginReqDto);
    }

    @Operation(summary = "用户发布评论接口")
    @PostMapping("/comment")
    public RestResp<Void> comment(@Valid @RequestBody UserCommentReqDto userCommentReqDto){
        /**
        * TODO
        * 2024/3/15
        * 用户登录时，用userhold记录用户id，之后便不用重复携带
        */
//        userCommentReqDto.setUserId(UserHolder.getUserId());
        return bookService.saveComment(userCommentReqDto);
    }

    @Operation(summary = "用户修改评论接口")
    @PostMapping("/comment/{id}")
    public RestResp<Void> updateComment(@PathVariable Long id, String content){
        /**
        * TODO
        * 2024/3/15
        * userholder
        */
//        return bookService.updateComment(UserHolder.getUserId(), id, content);
        return null;
    }

    @Operation(summary = "用户删除评论接口")
    @DeleteMapping("/comment/{id}")
    public RestResp<Void> deleteComment(@PathVariable Long id) {
//        return bookService.deleteComment(UserHolder.getUserId(), id);
        return null;
    }
}
