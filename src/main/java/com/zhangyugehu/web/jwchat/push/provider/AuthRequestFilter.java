package com.zhangyugehu.web.jwchat.push.provider;

import com.google.common.base.Strings;
import com.zhangyugehu.web.jwchat.push.bean.api.base.ResponseModel;
import com.zhangyugehu.web.jwchat.push.bean.db.User;
import com.zhangyugehu.web.jwchat.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String relationPath = ((ContainerRequest)requestContext).getPath(false);
        if (relationPath.startsWith("account/login")
            ||relationPath.startsWith("account/register")) {
            return;
        }
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)) {
            User user = UserFactory.findByToken(token);
            if (user != null) {
                // 给当前请求添加上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        // 主体部分
                        return user;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        // 这里可以写入用户权限，role是权限名
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return null;
                    }
                });
                return;
            }
        }
        ResponseModel<Object> model = ResponseModel.buildAccountError();
        requestContext.abortWith(Response
                .status(Response.Status.OK)
                .entity(model)
                .build());
    }
}
