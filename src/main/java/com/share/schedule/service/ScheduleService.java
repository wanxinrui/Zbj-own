package com.share.schedule.service;

import com.share.schedule.model.Schedule;

import java.util.Date;
import java.util.List;

public interface ScheduleService {

    /**
     * 添加成员日程记录
     * @param schedule
     * @return
     */
    int addSchedule(Schedule schedule);

    /**
     * 通过id删除成员日程记录
     * @param id
     * @return
     */
    int deleteScheduleById(long id);

    /**
     * 通过表单修改日程信息
     * @param schedule
     * @return
     */
    int updateScheduleBySchedule(Schedule schedule);

    /**
     * 通过id查找日程记录
     * @param id
     * @return
     */
    Schedule findScheduleById(long id);

    /**
     * 查看所有成员日程信息
     * @return
     */
    List<Schedule> queryAllSchedule();

    /**
     * 查看轮换周期
     * @return
     */
    int getPeriod();

    /**
     * 查询下一位分享者id
     * @return
     */
    Long getNextOne();

    /**
     * 查询下一位分享者顺序序号
     * @return
     */
    int getNextOneOrder();

    /**
     * 修改轮换周期
     * @param period
     * @return
     */
    int updatePeriodTo(Integer period);

    /**
     * 将isNext后移一位，指向下一次的分享者
     * @param decide
     * @param next
     * @return
     */
    int updateNextOne(boolean decide, int next);

    /**
     * 查询该序号是否是最末序号
     * @param order
     * @return
     */
    boolean isLastOne(int order);

    /**
     * 查询最后一位成员顺序序号（最大序号）
     * @return
     */
    long getLastOne();

    /**
     * 刷新开始成员、排序及分享时间
     * @param id
     * @param count
     * @param time
     * @return
     */
    int updateShareDate(long id, long count, String time);

    /**
     * 避免数据表更新、删除时id断层问题
     * @param lastOne
     * @return
     */
    int avoidFault(long lastOne);

    /**
     * 查询最大id
     * @return
     */
    long getMaxId();

    /**
     * 通过long类型更新下一位成员
     * @param id
     * @param period
     * @return
     */
    long updateNextLong(long id, int period);

    /**
     * 查询最大顺序序号并返回int类型
     * @return
     */
    Integer getLastOneInt();

    /**
     * 查询最后一次分享的时间
     * @return
     */
    Date selectLastDate();

    /**
     * 更新指定成员分享日期
     * @param id
     * @param newDate
     * @return
     */
    int updateDate(long id, Date newDate);

    /**
     * 将id1与id2成员的位置互换
     * @param id1
     * @param id2
     * @return
     */
    int changeSort(long id1, long id2);

    /**
     * 刷新全局开始时间
     * @param time
     * @return
     */
    int changeOfSchedule(String time);

    /**
     * 预定会议房间
     * @param id
     * @param room
     * @return
     */
    int orderMeetingRoom(long id, String room);
}
