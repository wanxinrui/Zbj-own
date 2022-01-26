package com.share.schedule.schedule.impl;


import com.share.schedule.service.UserService;
import com.share.schedule.service.ScheduleService;
import lombok.extern.log4j.Log4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
@DisallowConcurrentExecution
public class ScheduleImpl extends QuartzJobBean {
    /**
     * 定时任务job重写，实现定时发送邮件功能
     */

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ScheduleImpl.class);

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    UserService userService;

    Date today,orderDay;

//    @Value("${mail.fromMail.sender}")
//    private  String sender;
//
//    @Value("${mail.fromMail.receiver}")
//    private String receiver;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     *  对比本地时间,若本地时间处于下一次会议时间的前一天，则发送邮件通知下一位分享者：
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calenDate = Calendar.getInstance();
        orderDay = scheduleService.findScheduleById(scheduleService.getNextOne()).getDate();
        try {
            today = sdf.parse(df.format(System.currentTimeMillis()));
            calenDate.setTime(today);
            calenDate.add(Calendar.DATE, 1);
            String dateString = calenDate.get(Calendar.YEAR)+"-"+(calenDate.get(Calendar.MONTH)+1)+"-"+calenDate.get(Calendar.DAY_OF_MONTH);
//            System.out.println(dateString);
            calenDate.setTime(orderDay);
            String nextDate = calenDate.get(Calendar.YEAR)+"-"+(calenDate.get(Calendar.MONTH)+1)+"-"+calenDate.get(Calendar.DAY_OF_MONTH);
            System.out.println(nextDate);
            if(dateString.equals(nextDate)){
//                System.out.println("Awake");
                //发送邮件：
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("1169540159@qq.com");
                message.setTo(userService.findUserByUserId(scheduleService.getNextOne()).getEmail());
//                System.out.println(userService.findUserByUserId(scheduleService.getNextOne()).getEmail());
                message.setSubject("分享活动通知");
                message.setSentDate(sdf.parse(df.format(System.currentTimeMillis())));    // 设置邮件发送日期
                message.setText("同志您好：\n" +
                        "    请您准备好明天的分享活动，提前预定好会议号，谢谢！\n" +
                        "\n" +
                        "祝生活愉快，\n" +
                        "    ZBJ事业部");
                try {
                    javaMailSender.send(message);// 发送
                    log.info("邮件已经发送。");
                } catch (Exception e) {
                    log.error("发送邮件时发生异常！", e);
                }
            }
        } catch (ParseException e) {
            log.error(logger);
        }
    }
}
