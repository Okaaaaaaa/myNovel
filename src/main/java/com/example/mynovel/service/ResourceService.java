package com.example.mynovel.service;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.resp.ImgVerifyCodeRespDto;

/**
 * 资源（图片/视频/文档）相关服务类
 * @author Peter
 */
public interface ResourceService {

    /**
     * 生成验证码，并封装成响应类
     * @return
     * @throws Exception
     */
    RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws Exception;
}
