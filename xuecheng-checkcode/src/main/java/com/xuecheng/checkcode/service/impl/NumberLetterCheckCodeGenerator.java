package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 数字字母生成器
 * @author Lin
 * @date 2024/2/21 17:35
 */
@Component("NumberLetterCheckCodeGenerator")
public class NumberLetterCheckCodeGenerator implements CheckCodeService.CheckCodeGenerator {


    @Override
    public String generate(int length) {
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuilder stringBuilder =new StringBuilder();
        for(int i=0;i<length;i++){
            int number=random.nextInt(36);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }


}
