package com.example.mynovel.controller.front;

import com.example.mynovel.core.common.resp.PageRespDto;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.ApiRouterConsts;
import com.example.mynovel.dto.req.BookSearchReqDto;
import com.example.mynovel.dto.resp.BookInfoRespDto;
import com.example.mynovel.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_SEARCH_URL_PREFIX)
@Tag(name = "SearchController", description = "前台门户-搜索模块")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/books")
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition){
        return searchService.searchBooks(condition);
    }
}
