package com.zhangyugehu.web.jwchat.push.service;

import com.google.common.base.Strings;
import com.zhangyugehu.web.jwchat.push.bean.api.account.AccountRspModel;
import com.zhangyugehu.web.jwchat.push.bean.api.account.LoginModel;
import com.zhangyugehu.web.jwchat.push.bean.api.account.RegisterModel;
import com.zhangyugehu.web.jwchat.push.bean.api.base.ResponseModel;
import com.zhangyugehu.web.jwchat.push.bean.db.User;
import com.zhangyugehu.web.jwchat.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// baseurl/api/account
@Path("/account")
public class AccountService {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model) {
        if (!LoginModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                // 绑定pushId
                return bind(user, model.getPushId());
            }
            return ResponseModel.buildOk(new AccountRspModel(user));
        }
        return ResponseModel.buildLoginError();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        if (!RegisterModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.findByPhone(model.getAccount());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }
        user = UserFactory.findByName(model.getName());
        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.register(
                model.getAccount(),
                model.getPassword(),
                model.getName()
        );
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                // 绑定pushId
                return bind(user, model.getPushId());
            }
            return ResponseModel.buildOk(new AccountRspModel(user));
        }
        return ResponseModel.buildRegisterError();
    }

    @POST
    @Path("/bind/{pushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> bind(
            // 从请求头中获取token字段
            @HeaderParam("token") String token,
            @PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(token)
        ||  Strings.isNullOrEmpty(pushId)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.findByToken(token);
        if (user != null) {
            // 进行pushId绑定操作
            return bind(user, pushId);
        }
        return ResponseModel.buildAccountError();
    }

    /**
     * 绑定PushId
     * @param self
     * @param pushId
     * @return
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) return ResponseModel.buildServiceError();
        return ResponseModel.buildOk(new AccountRspModel(user, true));
    }
}
