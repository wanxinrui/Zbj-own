package com.share.schedule.service.impl;

import com.share.schedule.dao.ScheduleDao;
import com.share.schedule.model.Schedule;
import com.share.schedule.service.ScheduleService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private ScheduleDao scheduleMapper;

    /**
     * 添加成员日程记录
     * @param schedule
     * @return
     */
    @Override
    public int addSchedule(Schedule schedule) {
        return scheduleMapper.insertSelective(schedule);
    }

    /**
     * 通过id删除成员日程记录
     * @param id
     * @return
     */
    @Override
    public int deleteScheduleById(long id) {
        return scheduleMapper.deleteByPrimaryKey(id);
    }

    /**
     * 通过表单修改日程信息
     * @param schedule
     * @return
     */
    @Override
    public int updateScheduleBySchedule(Schedule schedule) {
        return scheduleMapper.updateByPrimaryKeySelective(schedule);
    }

    /**
     * 通过id查找日程记录
     * @param id
     * @return
     */
    @Override
    public Schedule findScheduleById(long id) {
        return scheduleMapper.selectByPrimaryKey(id);
    }

    /**
     * 查看所有成员日程信息
     * @return
     */
    @Override
    public List<Schedule> queryAllSchedule() {
        return scheduleMapper.queryAllSchedule();
    }

    /**
     * 查看轮换周期
     * @return
     */
    @Override
    public int getPeriod() {
        return scheduleMapper.getPeriod().getIsnext();
    }

    /**
     * 查询下一位分享者id
     * @return
     */
    @Override
    public Long getNextOne() {
        return scheduleMapper.getNextOne().getId();
    }

    /**
     * 查询下一位分享者顺序序号
     * @return
     */
    @Override
    public int getNextOneOrder() {
        return scheduleMapper.getNextOne().getOrder();
    }

    /**
     * 修改轮换周期
     * @param period
     * @return
     */
    @Override
    public int updatePeriodTo(Integer period) {
        return scheduleMapper.updatePeriodTo(period,scheduleMapper.getNextOne().getId());
    }

    /**
     * 将isNext后移一位，指向下一次的分享者
     * @param decide
     * @param next
     * @return
     */
    @Override
    public int updateNextOne(boolean decide, int next) {
        if(decide) {
            scheduleMapper.updateNext(1, getPeriod());
        }else {
            scheduleMapper.updateNext(next + 1, getPeriod());
        }
        scheduleMapper.updateNext(next, 0);
        return 0;
    }

    /**
     * 查询该序号是否是最末序号
     * @param order
     * @return
     */
    @Override
    public boolean isLastOne(int order) {
        boolean rat = false;
        if (scheduleMapper.getLastOne() == order){
            rat = true;
        }
        return rat;
    }

    /**
     * 查询最后一位成员顺序序号（最大序号）
     * @return
     */
    @Override
    public long getLastOne() {
        return scheduleMapper.getLastOne();
    }

    /**
     * 刷新全局开始时间
     * @param time
     * @return
     */
    @Override
    public int changeOfSchedule(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calenDate = Calendar.getInstance();
        Date newDate;
        int period = scheduleMapper.getPeriod().getIsnext();
        long isNextId = scheduleMapper.getNextOne().getId();
        long maxId = scheduleMapper.getMaxId();

        try {
            newDate = sdf.parse(time);
            calenDate.setTime(newDate); //将 newDate 的值改为特定日期
            calenDate.add(Calendar.DATE, -period); //特定时间的Period天前
            for(int i=0; i<maxId; i++){
                calenDate.add(Calendar.DATE, period); //特定时间的Period天后
                String dateString = calenDate.get(Calendar.YEAR)+"-"+(calenDate.get(Calendar.MONTH)+1)+"-"+calenDate.get(Calendar.DAY_OF_MONTH)+" 00:00:00";
                newDate = sdf.parse(dateString);
                //                System.out.println(newDate);
                if((isNextId+i) > maxId) {
                    scheduleMapper.updateDate(isNextId + i - maxId, newDate);
                }else {
                    scheduleMapper.updateDate(isNextId + i, newDate);
                }
            }
        } catch (ParseException e) {
            log.error(logger);
        }
        return 0;
    }

    /**
     * 预定会议房间
     * @param id
     * @param room
     * @return
     */
    @Override
    public int orderMeetingRoom(long id, String room) {
        return scheduleMapper.orderMeetingRoom(id, room);
    }

    /**
     * 刷新开始成员、排序及分享时间
     * @param id
     * @param count
     * @param newTime
     * @return
     */
    @Override
    public int updateShareDate(long id, long count, String newTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastDate = scheduleMapper.selectLastDate();
        Calendar calenDate = Calendar.getInstance();
        Date newDate;

        if (0 != id){   // TODO 刚开始启动程序

            // TODO 根据order排序
            for (long i=0; i<count; i++){
                if((id+i) > scheduleMapper.getMaxId()) {
                    //清除会议号
                    scheduleMapper.orderMeetingRoom(id + i - count,"下次再会~");
                    //更新顺序序号
                    scheduleMapper.updateOrderSort(id + i - count, i+1);
                }else {
                    //清除会议号
                    scheduleMapper.orderMeetingRoom(id + i,"下次再会~");
                    //更新顺序序号
                    scheduleMapper.updateOrderSort(id + i, i+1);
                }
            }

            // TODO 遍历刷新所有成员的分享日期
            changeOfSchedule(newTime);

            }else {         // TODO 已刷新过分享日期
                /**
                   1、获取order最大的成员的分享结束日期；
                   2、由于使用了"指针"isNext，此方案作废：自身order=1，前一位成员order=count，其余成员order依次-1；
                   3、自身时间=max(date)+1*Period;
                */
                calenDate.setTime(lastDate); //将 newDate 的值改为特定日期
                calenDate.add(Calendar.DATE, scheduleMapper.getPeriod().getIsnext()); //特定时间的Period天后

                String dateString = calenDate.get(Calendar.YEAR)+"-"+(calenDate.get(Calendar.MONTH)+1)+"-"+calenDate.get(Calendar.DAY_OF_MONTH)+" 00:00:00";
                try {
                    newDate = sdf.parse(dateString);
                    scheduleMapper.updateDate(scheduleMapper.getNextOne().getId(), newDate);
                } catch (ParseException e) {
                    log.error(logger);
                }

            }

        return 0;
    }

    /**
     * 避免数据表更新、删除时id断层问题
     * @param lastOne
     * @return
     */
    @Override
    public int avoidFault(long lastOne) {
        return scheduleMapper.avoidFault(lastOne);
    }

    /**
     * 查询最大id
     * @return
     */
    @Override
    public long getMaxId() {
        return scheduleMapper.getMaxId();
    }

    /**
     * 通过long类型更新下一位成员
     * @param id
     * @param period
     * @return
     */
    @Override
    public long updateNextLong(long id, int period) {
        return scheduleMapper.updateNextLong(id, period);
    }

    /**
     * 查询最大顺序序号并返回int类型
     * @return
     */
    @Override
    public Integer getLastOneInt() {
        return scheduleMapper.getLastOneInt();
    }

    /**
     * 查询最后一次分享的时间
     * @return
     */
    @Override
    public Date selectLastDate() {
        return scheduleMapper.selectLastDate();
    }

    /**
     * 更新指定成员分享日期
     * @param id
     * @param newDate
     * @return
     */
    @Override
    public int updateDate(long id, Date newDate) {
        return scheduleMapper.updateDate(id, newDate);
    }

    /**
     * 将id1与id2成员的位置互换
     * @param id1
     * @param id2
     * @return
     */
    @Override
    public int changeSort(long id1, long id2) {
        Schedule schedule1 = scheduleMapper.selectByPrimaryKey(id1);
        Schedule schedule2 = scheduleMapper.selectByPrimaryKey(id2);
        int order1 = schedule1.getOrder();
        int order2 = schedule2.getOrder();
        Date date1 = schedule1.getDate();
        Date date2 = schedule2.getDate();
        int isNext1 = schedule1.getIsnext();
        int isNext2 = schedule2.getIsnext();
//        System.out.println(date1+","+date2);
        scheduleMapper.updateOrderSort(id1, order2);
        scheduleMapper.updateOrderSort(id2, order1);
        scheduleMapper.updateDate(id1, date2);
        scheduleMapper.updateDate(id2, date1);
        scheduleMapper.updateIsNext(id1, isNext2);
        scheduleMapper.updateIsNext(id2, isNext1);

        return 0;
    }

}
