package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.entites.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoUtilities
{
    private final PasswordEncoder passwordEncoder;
    public User userRequstDtoToUser(UserRequestDto userRequestDto)
    {
        User user = new User();
        BeanUtils.copyProperties(userRequestDto,user);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return user;
    }
    public UserResponseDto userToUserResponseDto(User user)
    {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user,userResponseDto);
        return userResponseDto;
    }
}
