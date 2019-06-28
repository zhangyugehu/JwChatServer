package com.zhangyugehu.web.jwchat.push.service;

import com.zhangyugehu.web.jwchat.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// baseurl/api/account
@Path("/account")
public class AccountService {

    @GET
    @Path("/login")
    public String get() {
        return "login get";
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post() {
        User user = new User();
        user.setGender("man");
        user.setName("zhangsan");
        return user;
    }
}
