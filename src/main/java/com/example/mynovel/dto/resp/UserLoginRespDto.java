package com.example.mynovel.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRespDto {

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户token")
    private String token;
}
