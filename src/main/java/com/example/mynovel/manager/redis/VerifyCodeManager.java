package com.example.mynovel.manager.redis;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.mynovel.core.constant.CacheConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * @author Peter
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class VerifyCodeManager {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成base64格式的图片校验码
     * @param sessionId
     * @return
     * @throws IOException
     */
    public String genImgVerifyCode(String sessionId) throws IOException{
        // 生成图片验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 90, 4, 100);
        // 验证码值
        String verifyCode = lineCaptcha.getCode();
        log.info("验证码："+verifyCode);
        // 存入redis
        stringRedisTemplate.opsForValue().set(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY+sessionId,verifyCode, Duration.ofMinutes(5));
        // 验证码以base64的格式返回到客户端
        return lineCaptcha.getImageBase64Data();
    }

    /**
     * 校验用户输入的验证码是否正确
     * @param sessionId
     * @param verifyCode
     * @return
     */
    public boolean imgVerifyCodeOk(String sessionId, String verifyCode){
        return Objects.equals(stringRedisTemplate.opsForValue().get(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY+sessionId),
                verifyCode);
    }

    /**
     * 从redis中移除校验码
     * @param sessionId
     */
    public void removeImgVerifyCode(String sessionId){
        stringRedisTemplate.delete(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY+sessionId);
    }
}
