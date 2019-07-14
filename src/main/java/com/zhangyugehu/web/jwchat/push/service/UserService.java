package com.zhangyugehu.web.jwchat.push.service;

import com.google.common.base.Strings;
import com.zhangyugehu.web.jwchat.push.bean.api.base.ResponseModel;
import com.zhangyugehu.web.jwchat.push.bean.api.user.UpdateInfoModel;
import com.zhangyugehu.web.jwchat.push.bean.card.UserCard;
import com.zhangyugehu.web.jwchat.push.bean.db.User;
import com.zhangyugehu.web.jwchat.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

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

    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        List<User> users = UserFactory.contacts(self);

        List<UserCard> userCards = users.stream()
                .map(user -> new UserCard(user, true))
                .collect(Collectors.toList());

        return ResponseModel.buildOk(userCards);
    }

    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(
            @PathParam("followId") String followId) {
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(followId)||Strings.isNullOrEmpty(followId)) {
            return ResponseModel.buildParameterError();
        }
        // 找到我关注的人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            return ResponseModel.buildServiceError();
        }
        // TODO 给我关注的人发通知
        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            return ResponseModel.buildOk(new UserCard(self, true));
        }
        User user = UserFactory.findById(id);
        if (user == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(
            @DefaultValue("")
            @PathParam("name") String name) {
        User self = getSelf();
        List<User> result = UserFactory.search(name);
        List<User> contacts = UserFactory.contacts(self);
        // TODO 使用关联查询优化
        List<UserCard> userCards = result.stream()
                .map(user -> {
                    boolean isFollow = user.getId().equals(self.getId())
                            || contacts.stream().allMatch(contact -> contact.getId().equals(user.getId()));
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }


}
