package com.share.schedule.service.impl;

import com.share.schedule.dao.UserDao;
import com.share.schedule.model.User;
import com.share.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userMapper;

    /**
     * 通过表单添加成员
     * @param user
     * @return
     */
    @Override
    public int addUser(User user) {
        return userMapper.insertSelective(user);
    }

    /**
     * 通过成员id删除成员
     * @param userId
     * @return
     */
    @Override
    public int deleteUserByUserId(Long userId) {
        //如果id是int，需要转换为long
        return userMapper.deleteByPrimaryKey(userId);
    }

    /**
     * 通过表单更新成员信息
     * @param user
     * @return
     */
    @Override
    public int updateUserByUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 查询所有成员信息
     * @return
     */
    @Override
    public List<User> queryAllUser() {
        return userMapper.queryAllUser();
    }

    /**
     * 通过成员id查找成员
     * @param id
     * @return
     */
    @Override
    public User findUserByUserId(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 避免增添成员时的id断层问题
     * @param lastOne
     * @return
     */
    @Override
    public int avoidFault(long lastOne) {
        return userMapper.avoidFault(lastOne);
    }

}
