package com.example.mynovel.controller.front;

import com.example.mynovel.core.common.resp.PageRespDto;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.ApiRouterConsts;
import com.example.mynovel.dto.req.BookSearchReqDto;
import com.example.mynovel.dto.resp.BookCommentRespDto;
import com.example.mynovel.dto.resp.BookInfoRespDto;
import com.example.mynovel.service.BookService;
import com.example.mynovel.service.SearchService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "BookController", description = "前台门户-小说模块")
@RequestMapping(ApiRouterConsts.API_FRONT_BOOK_URL_PREFIX)
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final SearchService searchService;

    @GetMapping("/comment/newest_list")
    public RestResp<BookCommentRespDto> listNewestComments(
            @Parameter(description = "小说ID") Long bookId){
        return bookService.listNewestComments(bookId);
    }

}
