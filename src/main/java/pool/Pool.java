package pool;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;


public class Pool implements PageProcessor {

    private Site site=Site.me().setSleepTime(1000).setRetryTimes(3);
    private static List<String> ipList=new ArrayList<>();
    private static List<String> portList=new ArrayList<>();
    private static Map<List<String>,List<String>> map=new HashMap<>();
    @Override
    public void process(Page page) {
        Html html=page.getHtml();
        page.addTargetRequests(page.getHtml().links().regex("https://www.kuaidaili.com/free/inha/[1-9]/").all());
        ipList=html.xpath("//td[@data-title='IP']/text()").all();
        portList=html.xpath("//td[@data-title='PORT']/text()").all();
        map.put(ipList,portList);

    }

    @Override
    public Site getSite() {
        return site;
    }


    private  static void initPool(){

        try {
            File file=new File("config/pool.txt");

                if(!file.exists()){
                    file.createNewFile();
                }

               for(List<String> ipList:map.keySet()) {
                    portList=map.get(ipList);
                   for (int j = 0; j < ipList.size() - 1; j++) {
                       String ip = ipList.get(j);
                       String port = portList.get(j);

                       ip = "\r\n"+"ip=" + ip;
                       port = " port=" + port ;
                       byte[] ipByte = ip.getBytes();
                       byte[] portByte = port.getBytes();
                       System.out.println(ip+port);
                       FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                       fileOutputStream.write(ipByte);
                       fileOutputStream.write(portByte);
                       fileOutputStream.close();
                   }
               }
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    public static void exitProgram() {
        initPool();
    }




    public static void main(String[] args) {
        Spider spider=Spider.create(new Pool()).addUrl("https://www.kuaidaili.com/free/inha/2/");
        spider.setScheduler(new FileCacheQueueScheduler("logs"));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                exitProgram();
            }
        });
        spider.thread(2).run();

    }
}
