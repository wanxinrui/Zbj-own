package com.share.schedule.controller;

import com.share.schedule.service.UserService;
import com.share.schedule.model.LinkData;
import com.share.schedule.model.Schedule;
import com.share.schedule.model.User;
import com.share.schedule.service.ScheduleService;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.annotations.Param;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
@Controller
public class UserController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private JavaMailSender javaMailSender;

//    @Value("${mail.fromMail.sender}")
//    private  String sender;
//
//    @Value("${mail.fromMail.receiver}")
//    private String receiver;

    /**
     * 开始
     */
    @GetMapping
    @ResponseBody
    public List<User> start() {
        //打印下一次分享时间
//        System.out.println(scheduleService.findScheduleById(scheduleService.getNextOne()).getDate().toString());
        //获取所有成员信息
        return userService.queryAllUser();
    }

    /**
     * <设置>分享周期、开始日期以及从谁开始分享
     * @param id
     * @param sDate
     * @param period
     * @return
     */
    @PutMapping("/setStartOneAndDate")
    @ResponseBody
    public int setStartOne(@Param("id") long id, @Param("sDate") String sDate, @Param("period") int period) {
        // TODO 刷新isNext
        //isNext清零
        long count = scheduleService.getMaxId();
        for(int i=1; i<=count; i++) {
            scheduleService.updateNextLong(i, 0);
        }
        //更新isNext
        scheduleService.updateNextLong(id, period);

        scheduleService.updateShareDate(id, scheduleService.getMaxId(), sDate);

        scheduleService.orderMeetingRoom(scheduleService.getNextOne(),"稍等，马上安排！");

        return 0;
    }

    /**添加成员信息
     * 1、根据user参数更新user表；//name、gender、email
     * 2、自动生成schedule表：
     *    ①isNext=0；
     *    ②order=lastOrder+1；
     *    ③date=maxDate+period；
     *    ④自动配置createTime；
     * @param user
     * @return
     */
    @PostMapping("/addUser")
    @ResponseBody
    public int addUser(User user) {
        //避免断层：
        userService.avoidFault(scheduleService.getLastOne());
        scheduleService.avoidFault(scheduleService.getLastOne());
        //构造user、schedule表的createTime：
        Schedule schedule = new Schedule();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastDate = scheduleService.selectLastDate();
        Calendar calenDate = Calendar.getInstance();
        Date newDate;
        try {
            user.setCreatetime(sdf.parse(df.format(System.currentTimeMillis())));//更新user.createTime
            schedule.setCreatetime(sdf.parse(df.format(System.currentTimeMillis())));//更新schedule.createTime
        } catch (ParseException e) {
//            log.error(logger);
        }
        //生成user表：
        userService.addUser(user);
        //构造schedule表：
        schedule.setIsnext(0);
        schedule.setOrder(scheduleService.getLastOneInt()+1);
        int period = scheduleService.getPeriod();

        calenDate.setTime(lastDate); //将 newDate 的值改为特定日期
        calenDate.add(Calendar.DATE, period); //特定时间的Period天后
        String dateString = calenDate.get(Calendar.YEAR)+"-"+(calenDate.get(Calendar.MONTH)+1)+"-"+calenDate.get(Calendar.DAY_OF_MONTH)+" 00:00:00";
        try {
            newDate = sdf.parse(dateString);
            schedule.setDate(newDate);
//            System.out.println(scheduleService.getMaxId()+1);
        } catch (ParseException e) {
//            log.error(logger);
        }
        //生成schedule表：
        scheduleService.addSchedule(schedule);
        return 0;
    }

    /**
     * 通过id删除成员个人信息
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUserById/{id}")
    @ResponseBody
    public int deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserByUserId(id);
        scheduleService.deleteScheduleById(id);
        //避免断层：
        scheduleService.avoidFault(scheduleService.getLastOne());
        userService.avoidFault(scheduleService.getLastOne());
        return 0;
    }

    /**
     * 查询下一位分享成员
     * @return
     */
    @GetMapping("/findNextOne")
    @ResponseBody
    public LinkData findNextOne(){
        long id = scheduleService.getNextOne();
        LinkData ld = new LinkData();
        User user = userService.findUserByUserId(id);
        Schedule schedule = scheduleService.findScheduleById(id);
        ld.setId(id);
        ld.setName(user.getName());
        if (user.getGender() == 1){
            ld.setGender("男");
        }else {
            ld.setGender("女");
        }
        ld.setOrder(schedule.getOrder());
        ld.setRoom(schedule.getRoom());
        ld.setEmail(user.getEmail());
        ld.setDate(schedule.getDate());
        if (schedule.getIsnext() != 0) {
            ld.setIsnext("Yes");
        } else {
            ld.setIsnext("No");
        }
        return ld;
    }

    /**
     * 修改成员信息
     * @param user
     * @return
     */
    @PutMapping("/updateUserByUser")
    @ResponseBody
    public int updateUserByUser(User user) {
        return userService.updateUserByUser(user);
    }

    /**
     * 查询成员个人信息
     * @param id
     * @return
     */
    @GetMapping("/findUserByUserId/{id}")
    @ResponseBody
    public User findUserByUserId(@PathVariable("id") Long id) {
        return userService.findUserByUserId(id);
    }

    /** 本次会议结束
     *   1、清除本次会议号；
     *   2、更新isNext；
     *       ·依靠order序号查询下一位分享者，因此一定要保证order排序的正确性；
     *   3、date信息根据isNext中包含的周期信息进行日程更新；
     *   4、向下一位成员发送提示邮件；
     * @return
     */
    @PutMapping("/meetingAdjourned")
    @ResponseBody
    public int meetingAdjourned() {

        int order = scheduleService.getNextOneOrder();

        //清除会议号
        scheduleService.orderMeetingRoom(scheduleService.getNextOne(),"下次再会~");

        //更新日程：
        scheduleService.updateShareDate(0, scheduleService.getMaxId(), "date");

        //更新isNext：
        if (scheduleService.isLastOne(order) == false) {
            scheduleService.updateNextOne(false, order);
        } else {
            scheduleService.updateNextOne(true, order);
        }

        //刷新本次分享者会议信息
        scheduleService.orderMeetingRoom(scheduleService.getNextOne(),"稍等，马上安排！");

        //发送通知邮件：
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1169540159@qq.com");
        message.setTo(userService.findUserByUserId(scheduleService.getNextOne()).getEmail());
        message.setSubject("分享活动通知");
        message.setText("同志您好:\n" +
                "    两周后将轮到您进行知识分享，请做好准备。在分享前务必建好会议号，并填写进分享平台。\n" +
                "    如有因为特殊情况需要交换顺序、提前或延后分享的同志，请提前一周与管理员取得联系，方便调配，谢谢！\n" +
                "\n" +
                "祝生活愉快，\n" +
                "    ZBJ事业部");
        try {
            javaMailSender.send(message);// 发送
//            log.info("邮件已经发送。");
        } catch (Exception e) {
//            log.error("邮件发送异常！", e);
        }

        return 0;
    }

    /**
     * 联合查询个人详细信息
     * @param id
     * @return
     */
    @GetMapping("/getLinkListById/{id}")
    @ResponseBody
    public LinkData getLinkListById(@PathVariable("id") long id) {
        LinkData ld = new LinkData();
        User user = userService.findUserByUserId(id);
        Schedule schedule = scheduleService.findScheduleById(id);
        ld.setId(id);
        ld.setName(user.getName());
        if (user.getGender() == 1){
            ld.setGender("男");
        }else {
            ld.setGender("女");
        }
        ld.setOrder(schedule.getOrder());
        ld.setRoom(schedule.getRoom());
        ld.setEmail(user.getEmail());
        ld.setDate(schedule.getDate());
        if (schedule.getIsnext() != 0) {
            ld.setIsnext("Yes");
        } else {
            ld.setIsnext("No");
        }
        return ld;
    }

}
