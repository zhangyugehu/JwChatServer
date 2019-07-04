package com.zhangyugehu.web.jwchat.push;

import com.zhangyugehu.web.jwchat.push.provider.AuthRequestFilter;
import com.zhangyugehu.web.jwchat.push.provider.GsonProvider;
import com.zhangyugehu.web.jwchat.push.service.AccountService;
import com.zhangyugehu.web.jwchat.push.service.UserService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application() {
        packages(AccountService.class.getPackage().getName());
        packages(UserService.class.getPackage().getName());

        register(AuthRequestFilter.class);
        register(GsonProvider.class);
        register(Logger.class);
    }
}
