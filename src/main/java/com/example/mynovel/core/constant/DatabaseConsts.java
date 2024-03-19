package com.example.mynovel.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Peter
 */
public class DatabaseConsts {

    /**
     * 数据库表通用列名枚举
     */
    @Getter
    @AllArgsConstructor
    public enum CommonColumnEnum{
        ID("id"),
        SORT("sort"),
        CREATE_TIME("create_time"),
        UPDATE_TIME("update_time");

        private String name;
    }

    @Getter
    public enum SqlEnum {

        LIMIT_1("limit 1"),
        LIMIT_2("limit 2"),
        LIMIT_5("limit 5"),
        LIMIT_30("limit 30"),
        LIMIT_500("limit 500");

        private String sql;

        SqlEnum(String sql) {
            this.sql = sql;
        }

    }

    /**
     * 用户信息表
     */
    public static class UserInfoTable {
        public static final String COLUMN_USERNAME = "username";

    }

    /**
     * 小说评论表
     */
    public static class BookCommentTable {

        private BookCommentTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_BOOK_ID = "book_id";

        public static final String COLUMN_USER_ID = "user_id";

    }


}
