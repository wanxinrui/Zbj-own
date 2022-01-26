package com.share.schedule.dao;

import com.share.schedule.model.User;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {

    /**
     * 通过id删除成员信息
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 选择性插入成员信息
     * @param record
     * @return
     */
    int insertSelective(User record);

    /**
     * 通过id查询成员信息
     * @param id
     * @return
     */
    User selectByPrimaryKey(Long id);

    /**
     * 通过id选择性更改成员信息
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * 查询全体成员信息
     * @return
     */
    List<User> queryAllUser();

    /**
     * 避免增删断层
     * @param lastOne
     * @return
     */
    int avoidFault(long lastOne);

//    long countByExample(UserExample example);

//    int deleteByExample(UserExample example);

//    int insert(User record);

//    List<User> selectByExample(UserExample example);

//    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

//    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

//    int updateByPrimaryKey(User record);

}
