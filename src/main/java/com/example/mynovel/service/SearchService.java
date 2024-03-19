package com.example.mynovel.service;

import com.example.mynovel.core.common.resp.PageRespDto;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.req.BookSearchReqDto;
import com.example.mynovel.dto.resp.BookInfoRespDto;

public interface SearchService {
    RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition);
}
