package com.example.document_management_tool.dto;

import com.example.document_management_tool.entity.UserInfo;
import com.example.document_management_tool.enums.UserRoles;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private Long id;

    @NotEmpty(message = "Name should not be empty")
    private String name;

    @Column(unique = true)
    private String username;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    private String password;

    private String contact;

    private String roles;


    public static UserInfoDTO form(UserInfo userInfo){
        if(userInfo == null)
            return null;

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userInfo.getId());
        userInfoDTO.setName(userInfo.getName());
        userInfoDTO.setUsername(userInfo.getUsername());
        userInfoDTO.setEmail(userInfo.getEmail());
        userInfoDTO.setContact(userInfo.getContact());
        String userRoles =
                UserRoles.getLabelByUserRoles(userInfo.getRoles());
        userInfoDTO.setRoles(userRoles);
        return userInfoDTO;
    }
}
