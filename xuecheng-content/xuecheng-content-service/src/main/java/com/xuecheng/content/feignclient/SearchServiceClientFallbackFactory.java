package com.xuecheng.content.feignclient;

import com.xuecheng.content.model.dto.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Lin
 * @date 2024/2/20 17:01
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                // 降级方法
                log.debug("调用搜索服务添加索引时发生熔断，异常信息：{}", throwable.getMessage(), throwable);
                return false;
            }
        };
    }
}
