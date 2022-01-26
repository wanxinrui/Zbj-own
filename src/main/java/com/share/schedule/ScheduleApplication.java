package com.share.schedule;

import com.share.schedule.schedule.impl.ScheduleImpl;
import com.share.schedule.schedule.ScheduleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@MapperScan("com.share.schedule.dao")
//@EnableConfigCenter(module = "java-zbj-opencirrusauth-dubbo", enableListener = true)
//@ComponentScan(basePackages = {"com.share.schedule.*"},excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApplicationStarted.class)})

@SpringBootApplication(scanBasePackages = {"com.share.schedule.*"}, exclude = SecurityAutoConfiguration.class)
@EnableWebMvc
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
        //配置并开启定时任务：
        ScheduleManager scheduleManager = new ScheduleManager();
        scheduleManager.addJob("postMan", "group1", "trigger1",
                "triggerGroup1", ScheduleImpl.class, "0 0 0 * * ?");
                                                                    //每天零点执行一次，"*/5 * * * * ?"：每5秒执行一次；
    }

}
