package com.example.mynovel.service.impl;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.resp.HomeBookRespDto;
import com.example.mynovel.manager.cache.HomeBookCacheManager;
import com.example.mynovel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Peter
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeBookCacheManager homeBookCacheManager;

    /**
    * TODO
    * 2024/3/14
    * 代理模式
    */

    /**
     * 调用 homeBookCacheManager的listHomeBooks方法，查询所有homebooks的信息。缓存&返回
     * @return
     */
    @Override
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        return RestResp.ok(homeBookCacheManager.listHomeBooks());
    }
}
