package com.share.schedule.schedule;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduleManager {
    /**
     * 设置定时任务相关参数，并开启定时任务
     */

    //应用启动后，Scheduler已经装配到ioc容器中了，无需重新new一个
    private static Scheduler sched = SpringContextUtils.getBean(Scheduler.class);

    @Autowired
    public static void addJob(String jobName, String jobGroupName,
        String triggerName, String triggerGroupName, Class jobClass, String cron) {
        try {
            // 任务名，任务组，任务执行类
            JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();

            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));//"0 0 0 * * ?"每天零点执行一次
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }

        } catch (Exception e) {
        throw new RuntimeException(e);
        }
    }
}
