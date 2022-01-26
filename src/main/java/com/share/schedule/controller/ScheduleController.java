package com.share.schedule.controller;

import com.share.schedule.model.Schedule;
import com.share.schedule.service.ScheduleService;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
@Controller
public class ScheduleController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 添加日程
     * @param schedule
     * @return
     */
    @PostMapping("/addSchedule")
    @ResponseBody
    public int addSchedule(Schedule schedule){
        //避免断层：
        scheduleService.avoidFault(scheduleService.getLastOne());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            schedule.setCreatetime(sdf.parse(df.format(System.currentTimeMillis())));
        } catch (ParseException e) {
            log.error(logger);
        }
        return scheduleService.addSchedule(schedule);
    }

    /**
     * 通过id删除成员安排
     * @param id
     * @return
     */
    @DeleteMapping("/deleteScheduleById/{id}")
    @ResponseBody
    public int deleteScheduleById(@PathVariable("id") long id){
        scheduleService.deleteScheduleById(id);
        //避免断层
        scheduleService.avoidFault(scheduleService.getLastOne());
        return 0;
    }

    /**
     * 通过form表单修改信息
     * @param schedule
     * @return
     */
    @PutMapping("/updateScheduleBySchedule")
    @ResponseBody
    public int updateScheduleBySchedule(Schedule schedule){
        return scheduleService.updateScheduleBySchedule(schedule);
    }

    /**
     * 查询个人日程信息
     * @param id
     * @return
     */
    @GetMapping("/findScheduleById/{id}")
    @ResponseBody
    public Schedule findScheduleById(@PathVariable("id") long id){
        return scheduleService.findScheduleById(id);
    }

    /**
     * 查询所有日程
     * @return
     */
    @GetMapping("/queryAllSchedule")
    @ResponseBody
    public List<Schedule> queryAllSchedule(){
        return scheduleService.queryAllSchedule();
    }

    /**
     * 查询会议周期
     * @return
     */
    @GetMapping("/getPeriod")
    @ResponseBody
    public int getPeriod(){
        return scheduleService.getPeriod();
    }

    /**
     * 修改会议周期
     * @param period
     * @return
     */
    @PutMapping("/updatePeriodTo/{period}")
    @ResponseBody
    public int updatePeriodTo(@PathVariable("period") Integer period){
        return scheduleService.updatePeriodTo(period);
    }

    /**
     * 查询下一位分享者
     * @return
     */
    @GetMapping("/getNextOne")
    @ResponseBody
    public Long getNextOne(){
        return scheduleService.getNextOne();
    }

    /**
     * 调整分享顺序
     * @param id1
     * @param id2
     * @return
     */
    @PutMapping("/changeSort")
    @ResponseBody
    public int changeSort(@Param("id1") long id1, @Param("id2") long id2){
        return scheduleService.changeSort(id1, id2);
    }

    /**
     * 提前/延期
     * @param time
     * @return
     */
    @PutMapping("changeOfSchedule/{time}")
    @ResponseBody
    public int changeOfSchedule(@PathVariable("time") String time){
        return scheduleService.changeOfSchedule(time);
    }

    /**
     * 预约会议室
     * @param room
     * @return
     */
    @PutMapping("orderMeetingRoom/{room}")
    @ResponseBody
    public int orderMeetingRoom(@PathVariable("room") String room){
        return scheduleService.orderMeetingRoom(scheduleService.getNextOne(), room);
    }
}
