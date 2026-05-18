package com.believe.common.feign;

import com.believe.common.core.constants.HeaderConstants;
import com.believe.common.core.context.AuthContext;
import com.believe.common.core.context.RequestContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        RequestContext requestContext = RequestContext.get();
        if (requestContext.hasTraceId()) {
            template.header(HeaderConstants.TRACE_ID, requestContext.getTraceId());
        }

        AuthContext authContext = AuthContext.get();
        if (authContext.isLogin()) {
            template.header(HeaderConstants.USER_ID, String.valueOf(authContext.getUserId()));
            template.header(HeaderConstants.USERNAME, authContext.getUsername());
            if (authContext.getToken() != null) {
                template.header("Authorization", "Bearer " + authContext.getToken());
            }
        }
    }
}
