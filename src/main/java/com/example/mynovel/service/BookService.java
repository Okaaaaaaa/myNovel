package com.example.mynovel.service;

import com.example.mynovel.core.common.resp.RestResp;
import com.example.mynovel.dto.req.UserCommentReqDto;
import com.example.mynovel.dto.resp.BookCommentRespDto;

public interface BookService {
    RestResp<Void> saveComment(UserCommentReqDto userCommentReqDto);

    /**
     * 用户修改评论
     * @param userId 用户id
     * @param id     要修改的评论id
     * @param content
     * @return
     */
    RestResp<Void> updateComment(Long userId, Long id, String content);

    /**
     * 删除评论
     * @param userId 评论用户ID
     * @param commentId 评论ID
     * @return void
     * */
    RestResp<Void> deleteComment(Long userId, Long commentId);

    /**
     * 小说最新评论查询
     *
     * @param bookId 小说ID
     * @return 小说最新评论数据
     */
    RestResp<BookCommentRespDto> listNewestComments(Long bookId);
}
