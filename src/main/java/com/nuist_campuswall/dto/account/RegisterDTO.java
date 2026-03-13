package com.nuist_campuswall.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String userName;
    private String passWord;
    private String nickName;
    private String educationEmail;
}
