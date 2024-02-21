package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDTO;
import com.xuecheng.checkcode.model.CheckCodeResultDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * 验证码接口
 *
 * @author Lin
 * @date 2024/2/21 17:32
 */
@Slf4j
public abstract class AbstractCheckCodeService implements CheckCodeService {

    protected CheckCodeGenerator checkCodeGenerator;
    protected KeyGenerator keyGenerator;
    protected CheckCodeStore checkCodeStore;

    /**
     * 设置验证码生成器
     *
     * @param checkCodeGenerator 验证码生成器
     */
    public abstract void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator);

    /**
     * 设置key生成器
     *
     * @param keyGenerator key生成器
     */
    public abstract void setKeyGenerator(KeyGenerator keyGenerator);

    /**
     * 设置校验码存储
     *
     * @param checkCodeStore 校验码存储
     */
    public abstract void setCheckCodeStore(CheckCodeStore checkCodeStore);


    /**
     * 生成验证码公用方法
     *
     * @param checkCodeParamsDTO 生成验证码参数
     * @param codeLength         验证码长度
     * @param keyPrefix          key的前缀
     * @param expire             过期时间
     */
    public GenerateResult generate(CheckCodeParamsDTO checkCodeParamsDTO, Integer codeLength, String keyPrefix, Integer expire) {
        //生成四位验证码
        String code = checkCodeGenerator.generate(codeLength);
        log.debug("生成验证码:{}", code);
        //生成一个key
        String key = keyGenerator.generate(keyPrefix);

        //存储验证码
        checkCodeStore.set(key, code, expire);
        //返回验证码生成结果
        GenerateResult generateResult = new GenerateResult();
        generateResult.setKey(key);
        generateResult.setCode(code);
        return generateResult;
    }

    @Data
    protected class GenerateResult {
        String key;
        String code;
    }


    /**
     * 生成验证码
     *
     * @param checkCodeParamsDto 生成验证码参数
     * @return 验证码
     */
    @Override
    public abstract CheckCodeResultDTO generate(CheckCodeParamsDTO checkCodeParamsDto);


    @Override
    public boolean verify(String key, String code) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            return false;
        }
        String storeCode = checkCodeStore.get(key);
        if (storeCode == null) {
            return false;
        }
        boolean result = storeCode.equalsIgnoreCase(code);
        if (result) {
            //删除验证码
            checkCodeStore.remove(key);
        }
        return result;
    }


}
