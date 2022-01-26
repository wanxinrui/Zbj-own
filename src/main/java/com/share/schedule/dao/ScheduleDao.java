package com.share.schedule.dao;

import com.share.schedule.model.ScheduleExample;
import com.share.schedule.model.Schedule;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScheduleDao {

    /**
     * 通过id删除日程信息
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 选择性插入数据
     * @param record
     * @return
     */
    int insertSelective(Schedule record);

    /**
     * 通过id查找schedule
     */
    Schedule selectByPrimaryKey(Long id);

    /**
     * 通过id选择性地更新schedule
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Schedule record);

    /**
     * 查询所有日程信息
     * @return
     */
    List<Schedule> queryAllSchedule();

    /**
     * 获取周期信息
     * @return
     */
    Schedule getPeriod();

    /**
     * 获取本次分享成员信息
     * @return
     */
    Schedule getNextOne();

    /**
     * 更新分享周期
     * @param period
     * @param id
     * @return
     */
    int updatePeriodTo(Integer period, Long id);

    /**
     * 获取现在为止最后分享的成员的顺序号（最大序号）
     * @return
     */
    long getLastOne();

    /**
     * this.isNext置零，next.isNext赋值：
     * @param nextOneOrder
     * @param period
     * @return
     */
    int updateNext(int nextOneOrder, int period);

    /**
     * 更改指定id的成员的isNext值，并返回long类型
     * @param id
     * @param period
     * @return
     */
    long updateNextLong(long id, int period);

    /**
     * 查询现在为止最后一次分享的时间
     * @return
     */
    Date selectLastDate();

    /**
     * 将指定id的分享日期改为newDate
     * @param id
     * @param newDate
     * @return
     */
    int updateDate(long id, Date newDate);

    /**
     * 避免增删断层
     * @param lastOne
     * @return
     */
    int avoidFault(long lastOne);

    /**
     * 获取最大id号
     * @return
     */
    long getMaxId();

    /**
     * 将指定id的顺序号改为newOrder
     * @param lastId
     * @param newOrder
     * @return
     */
    int updateOrderSort(long lastId, long newOrder);

    /**
     * 将指定id的isNext值改为period的值
     * @param id
     * @param period
     * @return
     */
    int updateIsNext(long id, Integer period);

    /**
     * 获取最大序号并返回int类型
     * @return
     */
    Integer getLastOneInt();

    /**
     * 修改指定id的会议号
     * @param id
     * @param room
     * @return
     */
    int orderMeetingRoom(Long id, String room);

//    long countByExample(ScheduleExample example);

//    int deleteByExample(ScheduleExample example);

//    int insert(Schedule record);

//    List<Schedule> selectByExample(ScheduleExample example);

//    int updateByExampleSelective(@Param("record") Schedule record, @Param("example") ScheduleExample example);

//    int updateByExample(@Param("record") Schedule record, @Param("example") ScheduleExample example);

//    int updateByPrimaryKey(Schedule record);

}
