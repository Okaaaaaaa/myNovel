package com.example.mynovel.controller.front;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.ApiRouterConsts;
import com.example.mynovel.dto.resp.ImgVerifyCodeRespDto;
import com.example.mynovel.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter
 */
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_RESOURCE_URL_PREFIX)
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "获取图片验证码接口")
    @GetMapping("/img_verify_code")
    public RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws Exception{
        return resourceService.getImgVerifyCode();
    }
}
