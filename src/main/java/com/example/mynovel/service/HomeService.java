package com.example.mynovel.service;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.resp.HomeBookRespDto;

import java.util.List;

/**
 * @author Peter
 */
public interface HomeService {

    /**
     * 查询首页小说推荐列表
     *
     * @return 首页小说推荐列表的 rest 响应结果
     * */
    RestResp<List<HomeBookRespDto>> listHomeBooks();
}
