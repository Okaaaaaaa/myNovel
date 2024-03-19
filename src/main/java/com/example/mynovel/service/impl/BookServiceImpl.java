package com.example.mynovel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mynovel.core.common.constant.ErrorCodeEnum;
import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.core.constant.DatabaseConsts;
import com.example.mynovel.dao.entity.BookComment;
import com.example.mynovel.dao.entity.UserInfo;
import com.example.mynovel.dao.mapper.BookCommentMapper;
import com.example.mynovel.dto.req.UserCommentReqDto;
import com.example.mynovel.dto.resp.BookCommentRespDto;
import com.example.mynovel.manager.dao.UserDaoManager;
import com.example.mynovel.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookCommentMapper bookCommentMapper;

    private final UserDaoManager userDaoManager;

    @Override
    public RestResp<Void> saveComment(UserCommentReqDto userCommentReqDto) {
        // 用户对一部小说只能发表一次评论
        QueryWrapper<BookComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.BookCommentTable.COLUMN_BOOK_ID,userCommentReqDto.getBookId())
                .eq(DatabaseConsts.BookCommentTable.COLUMN_USER_ID,userCommentReqDto.getUserId());
        if(bookCommentMapper.selectCount(queryWrapper)>0){
            return RestResp.fail(ErrorCodeEnum.USER_COMMENTED);
        }
        //存入数据库
        BookComment bookComment = new BookComment();
        bookComment.setBookId(userCommentReqDto.getBookId());
        bookComment.setUserId(userCommentReqDto.getUserId());
        bookComment.setCommentContent(userCommentReqDto.getCommentContent());
        bookComment.setCreateTime(LocalDateTime.now());
        bookComment.setUpdateTime(LocalDateTime.now());
        bookCommentMapper.insert(bookComment);
        return RestResp.ok();
    }

    /**
     * 用户修改评论
     * @param userId  用户id
     * @param id      要修改的评论id
     * @param content
     * @return
     */
    @Override
    public RestResp<Void> updateComment(Long userId, Long id, String content) {
        QueryWrapper<BookComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.BookCommentTable.COLUMN_BOOK_ID, userId)
                .eq(DatabaseConsts.CommonColumnEnum.ID.getName(), id);
        BookComment bookComment = new BookComment();
        bookComment.setCommentContent(content);
        bookCommentMapper.update(bookComment,queryWrapper);
        return RestResp.ok();
    }

    /**
     * 删除评论
     * @param userId    评论用户ID
     * @param commentId 评论ID
     * @return void
     */
    @Override
    public RestResp<Void> deleteComment(Long userId, Long commentId) {
        QueryWrapper<BookComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.CommonColumnEnum.ID.getName(), commentId)
                .eq(DatabaseConsts.BookCommentTable.COLUMN_USER_ID,userId);
        bookCommentMapper.delete(queryWrapper);
        return RestResp.ok();
    }

    /**
     * 小说最新评论查询
     *
     * @param bookId 小说ID
     * @return 小说最新评论数据
     */
    @Override
    public RestResp<BookCommentRespDto> listNewestComments(Long bookId) {
        QueryWrapper<BookComment> countQueryWrapper = new QueryWrapper<>();
        countQueryWrapper.eq(DatabaseConsts.BookCommentTable.COLUMN_BOOK_ID,bookId);

        Long commentTotal = bookCommentMapper.selectCount(countQueryWrapper);
        BookCommentRespDto bookCommentRespDto = BookCommentRespDto.builder().commentTotal(commentTotal).build();

        if(commentTotal > 0){
            //查询这本小说对应的所有评论，按照创建时间选出最后五条
            QueryWrapper<BookComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(DatabaseConsts.BookCommentTable.COLUMN_BOOK_ID,bookId)
                    .orderByDesc(DatabaseConsts.CommonColumnEnum.CREATE_TIME.getName())
                    .last(DatabaseConsts.SqlEnum.LIMIT_5.getSql());

            // 最新发布的五条评论，每条bookcommet:评论id、bookId、userId、commentContent、replyCount、auditStatus、createTime、updateTime
            List<BookComment> bookComments = bookCommentMapper.selectList(queryWrapper);
            // 查询发布这些评论的用户id
            List<Long> userIds = bookComments.stream().map(BookComment::getUserId).toList();
            // 根据用户id查询用户信息
            List<UserInfo> userInfos = userDaoManager.listUsers(userIds);
            // 映射成 userId, username
            Map<Long,UserInfo> userInfoMap = userInfos.stream()
                    .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            // BookCommentRespDto.CommentInfo：评论id、commentContent、commentUser、commentUserId、commentUserPhoto、commentTime
            List<BookCommentRespDto.CommentInfo> commentInfos = bookComments.stream()
                    .map(v -> BookCommentRespDto.CommentInfo.builder()
                            .id(v.getId())
                            .commentUserId(v.getUserId())
                            .commentUser(userInfoMap.get(v.getUserId()).getUsername())
                            .commentUserPhoto(userInfoMap.get(v.getUserId()).getUserPhoto())
                            .commentTime(v.getCreateTime())
                            .commentContent(v.getCommentContent()).build()).toList();
            bookCommentRespDto.setComments(commentInfos);
        }else{
            bookCommentRespDto.setComments(Collections.emptyList());
        }

        return RestResp.ok(bookCommentRespDto);
    }
}
