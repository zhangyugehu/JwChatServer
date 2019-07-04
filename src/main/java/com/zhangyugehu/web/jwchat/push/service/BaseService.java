package com.zhangyugehu.web.jwchat.push.service;

import com.zhangyugehu.web.jwchat.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class BaseService {

    @Context
    protected SecurityContext securityContext;
    protected User getSelf() {
        if (securityContext == null) return null;
        return (User) securityContext.getUserPrincipal();
    }
}
