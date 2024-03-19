package com.example.mynovel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mynovel.core.common.resp.PageRespDto;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dao.entity.BookInfo;
import com.example.mynovel.dao.mapper.BookInfoMapper;
import com.example.mynovel.dto.req.BookSearchReqDto;
import com.example.mynovel.dto.resp.BookInfoRespDto;
import com.example.mynovel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据库搜索 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2022/5/23
 */
@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "enable", havingValue = "false")
@Service
@RequiredArgsConstructor
@Slf4j
public class DbSearchServiceImpl implements SearchService {

    private final BookInfoMapper bookInfoMapper;

    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {
        Page<BookInfoRespDto> page = new Page<>();
        page.setCurrent(condition.getPageNum());
        page.setSize(condition.getPageSize());
        List<BookInfo> bookInfos = bookInfoMapper.searchBooks(page, condition);
        return RestResp.ok(PageRespDto.of(condition.getPageNum(), condition.getPageSize(), page.getTotal()
                , bookInfos.stream().map(v -> BookInfoRespDto.builder()
                        .id(v.getId())
                        .bookName(v.getBookName())
                        .categoryId(v.getCategoryId())
                        .categoryName(v.getCategoryName())
                        .authorId(v.getAuthorId())
                        .authorName(v.getAuthorName())
                        .wordCount(v.getWordCount())
                        .lastChapterName(v.getLastChapterName())
                        .build()).toList()));
    }

}
