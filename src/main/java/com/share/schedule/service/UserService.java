package com.share.schedule.service;

import com.share.schedule.model.User;

import java.util.List;

public interface UserService {

    /**
     * 通过表单添加成员
     * @param user
     * @return
     */
    int addUser(User user);

    /**
     * 通过成员id删除成员
     * @param userId
     * @return
     */
    int deleteUserByUserId(Long userId);

    /**
     * 通过表单更新成员信息
     * @param user
     * @return
     */
    int updateUserByUser(User user);

    /**
     * 查询所有成员信息
     * @return
     */
    List<User> queryAllUser();

    /**
     * 通过成员id查找成员
     * @param id
     * @return
     */
    User findUserByUserId(Long id);

    /**
     * 避免增添成员时的id断层问题
     * @param lastOne
     * @return
     */
    int avoidFault(long lastOne);

}
