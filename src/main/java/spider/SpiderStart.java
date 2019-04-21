package spider;

import database.DataBase;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import webmagic.MyDownloader;
import webmagic.MyProcessor;
import webmagic.MySpider;


import javax.management.JMException;
public class SpiderStart {
    private static long beg;
    private static long end;
    private static int oldCount=0;
    private static int newCount=0;

    private  MySpider mySpider =null;

    private DataBase dataBase=new DataBase();

    private Logger logger= LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        //设置日记
        PropertyConfigurator.configure("config/log4j.properties");
        beg=System.currentTimeMillis();
        SpiderStart spiderStart=new SpiderStart();
        spiderStart.start();
    }

    private void start() {
        spiderInit();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                exitProgram();
            }
        });
        mySpider.run();
    }

    //结束程序
    public void exitProgram() {
        newCount=mySpider.count();
        dataBase.close();
        System.out.println("Stopping...");
        System.out.println("old count:"+oldCount);
        System.out.println("new count:"+newCount);
        System.out.println("add:"+(newCount-oldCount));
        mySpider.close();
        end = System.currentTimeMillis();
        System.out.println("Time consuming: " + (end - beg) / 1000 + "(s)");
    }

    private void spiderInit() {
        int threads =8;//线程数量
        MyProcessor myProcessor = new MyProcessor();
        mySpider=new MySpider(myProcessor);
        oldCount=mySpider.count();
        System.out.println("Starting...");
        //从这里开始爬  用户可以随便选
        mySpider.addUrl("https://www.zhihu.com/people/jason_wang_2018/activities")
                .setDownloader(new MyDownloader(mySpider))
                .setScheduler(new FileCacheQueueScheduler("logs")) //www.zhihu.com.urls.txt
                .thread(threads);
        //设置监控
        try {
            SpiderMonitor.instance().register(mySpider);
        } catch (JMException e) {
            System.out.println("爬虫监控设置失败!");
            logger.error(e.toString()+e.getMessage());
        }
    }
}
