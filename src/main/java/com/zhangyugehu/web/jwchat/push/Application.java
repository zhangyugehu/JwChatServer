package com.zhangyugehu.web.jwchat.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.zhangyugehu.web.jwchat.push.service.AccountService;
import com.zhangyugehu.web.jwchat.push.service.BService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application() {
        packages(AccountService.class.getPackage().getName());
        packages(BService.class.getPackage().getName());

        register(JacksonJsonProvider.class);

        register(Logger.class);
    }
}
