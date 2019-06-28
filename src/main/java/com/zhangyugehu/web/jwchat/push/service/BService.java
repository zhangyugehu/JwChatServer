package com.zhangyugehu.web.jwchat.push.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/b")
public class BService {
    @GET
    public String get() {
        return "b service";
    }
}
