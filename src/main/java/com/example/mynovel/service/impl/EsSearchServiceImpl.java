package com.example.mynovel.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mynovel.core.common.resp.PageRespDto;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.EsConsts;
import com.example.mynovel.dto.es.EsBookDto;
import com.example.mynovel.dto.req.BookSearchReqDto;
import com.example.mynovel.dto.resp.BookInfoRespDto;
import com.example.mynovel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ConditionalOnProperty(prefix = "spring.elasticsearch",name = "enabled",havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class EsSearchServiceImpl implements SearchService {
    private final ElasticsearchClient esClient;
    @Override
    @SneakyThrows
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {
        SearchResponse<EsBookDto> response = esClient.search(s -> {
            SearchRequest.Builder searchBuilder = s.index(EsConsts.BookIndex.INDEX_NAME);
            // 构建检索条件
            buildSearchCondition(condition, searchBuilder);
            // 排序
            if (!StringUtils.isBlank(condition.getSort())) {
                searchBuilder.sort(o -> o.field(f -> f
                        .field(StringUtils.underlineToCamel(condition.getSort().split(" ")[0]))
                        .order(SortOrder.Desc))
                );
            }
            // 分页
            searchBuilder.from((condition.getPageNum() - 1) * condition.getPageSize())
                    .size(condition.getPageSize());
            // 设置高亮显示
            searchBuilder.highlight(h -> h
                    .fields(EsConsts.BookIndex.FIELD_BOOK_NAME,
                            t -> t.preTags("<em style='color:red'>").postTags("</em>"))
                    .fields(EsConsts.BookIndex.FIELD_AUTHOR_NAME,
                            t -> t.preTags("<em style='color:red'>").postTags("</em>")));

            return searchBuilder;
        },
        EsBookDto.class
        );

        // 获取所有的查询结果
        TotalHits total = response.hits().total();

        assert total != null;


        // 要返回的结果中的List数组
        List<BookInfoRespDto> list = new ArrayList<>();

        // response中的数组
        List<Hit<EsBookDto>> hits = response.hits().hits();

        for(var hit: hits){
            EsBookDto book = hit.source();
            assert book != null;
            if(!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME))){
                book.setBookName(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME).get(0));
            }

            if(!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME))){
                book.setAuthorName(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME).get(0));
            }

            list.add(BookInfoRespDto.builder()
                    .id(book.getId())
                    .bookName(book.getBookName())
                    .categoryId(book.getCategoryId())
                    .categoryName(book.getCategoryName())
                    .authorId(book.getAuthorId())
                    .authorName(book.getAuthorName())
                    .wordCount(book.getWordCount())
                    .lastChapterName(book.getLastChapterName())
                    .build());
        }
        return RestResp.ok(
                PageRespDto.of(condition.getPageNum(),condition.getPageSize(),total.value(),list)
        );
    }

    /**
     * 构造查询条件
     * @param condition
     * @param searchBuilder
     */
    private void buildSearchCondition(BookSearchReqDto condition,
        SearchRequest.Builder searchBuilder){

        BoolQuery boolQuery = BoolQuery.of(b->{
            // 限制返回的小说为字数>0的
            b.filter(RangeQuery.of(m->m
                    .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                    .gt(JsonData.of(0))
                )._toQuery()
            );

            // 用用户输入的关键字查询 - multi_match
            if(!StringUtils.isBlank(condition.getKeyword())){
                b.must(MultiMatchQuery.of(t->t
                        .fields(EsConsts.BookIndex.FIELD_BOOK_NAME + "^2",
                                EsConsts.BookIndex.FIELD_AUTHOR_NAME + "^1.8",
                                EsConsts.BookIndex.FIELD_BOOK_DESC + "^0.1")
                        .query(condition.getKeyword())
                    )._toQuery()
                );
            }

            // 根据类别categoryId查询
            if(Objects.nonNull(condition.getCategoryId())){
                b.filter(TermQuery.of(m->m
                        .field(EsConsts.BookIndex.FIELD_CATEGORY_ID)
                        .value(condition.getCategoryId())
                    )._toQuery()
                );
            }

            // 根据作品方向workDirection查询
            if(Objects.nonNull(condition.getWorkDirection())){
                b.filter(TermQuery.of(m->m
                        .field(EsConsts.BookIndex.FIELD_WORK_DIRECTION)
                        .value(condition.getWorkDirection())
                    )._toQuery()
                );
            }

            //根据字数范围查询 - range
            if(Objects.nonNull(condition.getWordCountMin())){
                b.filter(RangeQuery.of(m->m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        .gte(JsonData.of(condition.getWordCountMin()))
                    )._toQuery()
                );
            }

            if(Objects.nonNull(condition.getWordCountMax())){
                b.filter(RangeQuery.of(m->m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        .lte(JsonData.of(condition.getWordCountMax()))
                    )._toQuery()
                );
            }

            // 用最近更新日期getUpdateTimeMin查询 - range
            if(Objects.nonNull(condition.getUpdateTimeMin())){
                b.filter(RangeQuery.of(m->m
                        .field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME)
                        .gte(JsonData.of(condition.getUpdateTimeMin().getTime()))
                    )._toQuery()
                );
            }

            return b;
        });

        searchBuilder.query(q->q.bool(boolQuery));

    }
}
