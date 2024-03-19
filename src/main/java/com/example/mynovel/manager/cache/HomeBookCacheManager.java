package com.example.mynovel.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mynovel.core.constant.CacheConsts;
import com.example.mynovel.core.constant.DatabaseConsts;
import com.example.mynovel.dao.entity.BookInfo;
import com.example.mynovel.dao.entity.HomeBook;
import com.example.mynovel.dao.mapper.BookInfoMapper;
import com.example.mynovel.dao.mapper.HomeBookMapper;
import com.example.mynovel.dto.resp.HomeBookRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Peter
 */
@Component
@RequiredArgsConstructor
public class HomeBookCacheManager {
    private final HomeBookMapper homeBookMapper;
    private final BookInfoMapper bookInfoMapper;


    //查询homebook，并用redis缓存
    @Cacheable(value = CacheConsts.HOME_BOOK_CACHE_NAME,
            cacheManager=CacheConsts.CAFFEINE_CACHE_MANAGER)
    public List<HomeBookRespDto> listHomeBooks(){

        //查询home book中所有书籍id
        QueryWrapper<HomeBook> queryWrapper = new QueryWrapper<>();
        List<HomeBook> homeBooks = homeBookMapper.selectList(queryWrapper);

        // 根据查到的bookId再去查book的具体内容
        if(!homeBooks.isEmpty()){
            //获得要查询的id数组
            List<Long> bookIds = homeBooks.stream().map(HomeBook::getId).toList();

            //根据小说ID列表查询相关的小说信息列表，组装query wrapper，进行查询
            QueryWrapper<BookInfo> bookInfoQueryWrapper = new QueryWrapper<>();
            bookInfoQueryWrapper.in(DatabaseConsts.CommonColumnEnum.ID.getName(),bookIds);
            List<BookInfo> bookInfos = bookInfoMapper.selectList(bookInfoQueryWrapper);

            if(!bookInfos.isEmpty()){
                // <bookId,bookInfo>
                Map<Long,BookInfo> bookInfoMap =bookInfos.stream()
                        .collect(Collectors.toMap(BookInfo::getId, Function.identity()));
                //组装HomeBookRespDto对象
                return homeBooks.stream().map(v->{
                    BookInfo bookInfo = bookInfoMap.get(v.getBookId());
                    HomeBookRespDto homeBookRespDto = new HomeBookRespDto();
                    homeBookRespDto.setBookId(v.getBookId());
                    homeBookRespDto.setType(v.getType());
                    homeBookRespDto.setBookName(bookInfo.getBookName());
                    homeBookRespDto.setBookDesc(bookInfo.getBookDesc());
                    homeBookRespDto.setPicUrl(bookInfo.getPicUrl());
                    homeBookRespDto.setAuthorName(bookInfo.getAuthorName());
                    return homeBookRespDto;
                }).toList();
            }

        }
        return Collections.emptyList();
    }
}
