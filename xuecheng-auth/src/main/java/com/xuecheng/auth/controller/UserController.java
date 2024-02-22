package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.dto.RegisterDTO;
import com.xuecheng.ucenter.model.dto.RetrievePasswordDTO;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lin
 * @date 2024/2/22 14:53
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/findpassword")
    public void findPassword(@RequestBody RetrievePasswordDTO retrievePasswordDTO) {
        userService.findPassword(retrievePasswordDTO);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDTO registerDTO){
        userService.register(registerDTO);
    }
}
