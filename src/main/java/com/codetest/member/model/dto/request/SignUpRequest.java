package com.codetest.member.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
    @NotBlank(message = "비밀번호를 다시 입력해주세요.")
    private String verifyPassword;
}
