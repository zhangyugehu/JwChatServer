package com.zhangyugehu.web.jwchat.push.factory;

import com.google.common.base.Strings;
import com.zhangyugehu.web.jwchat.push.bean.db.User;
import com.zhangyugehu.web.jwchat.push.bean.db.UserFollow;
import com.zhangyugehu.web.jwchat.push.utils.Hib;
import com.zhangyugehu.web.jwchat.push.utils.TextUtil;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserFactory {

    public static User findByPhone(String phone) {
        phone = phone.trim();
        String finalPhone = phone;
        return Hib.query(session -> (User)session
                .createQuery("from User where phone=:phone")
                .setParameter("phone", finalPhone)
                .uniqueResult());
    }
    public static User findByName(String name) {
        name = name.trim();
        String finalName = name;
        return Hib.query(session -> (User)session
                .createQuery("from User where name=:name")
                .setParameter("name", finalName)
                .uniqueResult());
    }
    public static User findByToken(String token) {
        token = token.trim();
        String finalToken = token;
        return Hib.query(session -> (User)session
                .createQuery("from User where token=:token")
                .setParameter("token", finalToken)
                .uniqueResult());
    }
    public static User findById(final String id) {
        return Hib.query(session -> session.get(User.class, id));
    }

    /**
     * 更新用户信息到数据库
     * @param user
     * @return
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 给当前账户绑定PushId
     * @param user
     * @param pushId
     * @return
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            return null;
        }
        // 查询是否有其他账号绑定该设备
        Hib.queryOnly(session -> {
            List<User> users = session
                    .createQuery("from User where lower(pushId) =:pushId and id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();
            for (User u:users) {
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });
        if (pushId.equals(user.getPushId())) {
            // 我就是之前已经绑定的
            return user;
        }
        // 如果当前账户之前的pushId和需要绑定的不同
        // 需要给之前设备推送下线通知
        if (Strings.isNullOrEmpty(user.getPushId())) {
            // TODO 推送下线通知
        }
        // 更新新的pushId
        user.setPushId(pushId);
        return update(user);
    }

    /**
     * 账号密码登录
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        String finalAccount = account.trim();
        String finalPassword = encodePassword(password);
        User user = Hib.query(session -> (User)session
                .createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone", finalAccount)
                .setParameter("password", finalPassword)
                .uniqueResult());
        if (user != null) {
            user = login(user);
        }
        return user;
    }

    /**
     * 用户注册
     * @param account
     * @param password
     * @param name
     * @return
     */
    public static User register(String account, String password, String name) {
        account = account.trim();
        password = encodePassword(password);
        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);
        }
        return user;
//        // 数据库操作
//        Session session = Hib.session();
//        session.beginTransaction();
//        try {
//            session.save(user);
//            session.getTransaction().commit();
//            return user;
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 回滚事务
//            session.getTransaction().rollback();
//            return null;
//        }
    }
    private static User login(User user) {
        String token = UUID.randomUUID().toString();
        token = TextUtil.encodeBase64(token);
        user.setToken(token);
        return update(user);
    }
    private static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    private static String encodePassword(String pwd) {
        pwd = pwd.trim();
        // 解析MD5非对称加密
        pwd = TextUtil.getMD5(pwd);
        // 对称Base64加密
        return TextUtil.encodeBase64(pwd);
    }

    /**
     * 获取我的联系人
     * @param self
     * @return
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            // 由于懒加载所以需要重新加载一次
            session.load(self, self.getId());
            Set<UserFollow> followers = self.getFollowing();
            return followers.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 关注你操作
     * @param origin
     * @param target
     * @param alias
     * @return
     */
    public static User follow(User origin, User target, String alias) {
        UserFollow userFollow = getUserFollow(origin, target);
        if (userFollow != null) {
            // 已关注
            return userFollow.getTarget();
        }
        return Hib.query(session -> {
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            // 我关注人的时候，同事他也要关注我
            // 所以需要两条UserFollow数据
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            originFollow.setAlisa(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            session.save(originFollow);
            session.save(targetFollow);

            return target;
        });
    }

    /**
     * 查询两个人是否已经关注
     * @param origin
     * @param target
     * @return
     */
    public static UserFollow getUserFollow(User origin, User target) {
        return Hib.query(session -> (UserFollow)session
                .createQuery("from UserFollow where originId =:originId and targetId=:target")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                .setMaxResults(1)
                .uniqueResult()
        );
    }

    /**
     * 搜索联系人的实现
     * @param name
     * @return 如空name为空，则返回最近的用户
     */
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name)) name = "";
        final String searchName = "%"+name+"%";
        return Hib.query(session -> {
            // 查询条件：name忽略大小写模糊查询，头像和描述必须完善
            return session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null ")
                    .setParameter("name", searchName)
                    .setMaxResults(20)
                    .list();
        });
    }
}
