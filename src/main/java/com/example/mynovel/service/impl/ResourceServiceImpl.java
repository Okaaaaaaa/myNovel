package com.example.mynovel.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.resp.ImgVerifyCodeRespDto;
import com.example.mynovel.manager.redis.VerifyCodeManager;
import com.example.mynovel.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Peter
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final VerifyCodeManager verifyCodeManager;
    @Override
    public RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws Exception {
        /**
        * TODO
        * 2024/3/14
        * 什么是雪花算法？生成全局唯一的uuid
        */
        String sessionId = IdWorker.get32UUID();
        return RestResp.ok(ImgVerifyCodeRespDto.builder()
                .sessionId(sessionId)
                .img(verifyCodeManager.genImgVerifyCode(sessionId))
                .build());
    }
}
