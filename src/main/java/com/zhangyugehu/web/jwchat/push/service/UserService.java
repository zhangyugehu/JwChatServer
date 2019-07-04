package com.zhangyugehu.web.jwchat.push.service;

import com.google.common.base.Strings;
import com.zhangyugehu.web.jwchat.push.bean.api.base.ResponseModel;
import com.zhangyugehu.web.jwchat.push.bean.api.user.UpdateInfoModel;
import com.zhangyugehu.web.jwchat.push.bean.card.UserCard;
import com.zhangyugehu.web.jwchat.push.bean.db.User;
import com.zhangyugehu.web.jwchat.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserService extends BaseService {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(
            // 从请求头中获取token字段
            @HeaderParam("token") String token,
            UpdateInfoModel model) {
        if (Strings.isNullOrEmpty(token) || !UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = getSelf();
        user = model.updateUser(user);
        user = UserFactory.update(user);
        return ResponseModel.buildOk(new UserCard(user, true));
    }
}
