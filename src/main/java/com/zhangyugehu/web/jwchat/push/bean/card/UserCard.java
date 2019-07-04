package com.zhangyugehu.web.jwchat.push.bean.card;

import com.google.gson.annotations.Expose;
import com.zhangyugehu.web.jwchat.push.bean.db.User;

import java.time.LocalDateTime;

public class UserCard {

    @Expose
    private String id;

    @Expose
    private LocalDateTime modifyAt = LocalDateTime.now();

    @Expose
    private String name;

    @Expose
    private String phone;

    @Expose
    private String portrait;

    @Expose
    private String desc;

    @Expose
    private int sex = 0;

    // 关注数量
    @Expose
    private int follows;

    // 粉丝数量
    @Expose
    private int following;

    public UserCard(final User user) {
        this(user, false);
    }
    public UserCard(final User user, boolean isFollow) {
        this.id = user.getId();
        this.name = user.getName();
        this.portrait = user.getPortrait();
        this.phone = user.getPhone();
        this.desc = user.getDescription();
        this.sex = user.getSex();
        this.modifyAt = user.getUpdateAt();
        this.isFollow = isFollow;
        // TODO 得到关注人和粉丝数量
        // user.getFollowers().size(); 懒加载会报错，因为没有session
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    // 我与当前用户的关注状态
    @Expose
    private boolean isFollow;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}
