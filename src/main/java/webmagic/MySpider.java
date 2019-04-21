package webmagic;

import database.DataBase;
import org.junit.Test;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MySpider extends Spider {
    

    private DataBase dataBase=null;

    public MySpider(PageProcessor pageProcessor) {

        super(pageProcessor);
        dataBase=new DataBase();


        

    HttpClientDownloader proxy=new HttpClientDownloader();

    proxy.setProxyProvider(SimpleProxyProvider
            .from(randomProxy()
                    ,randomProxy()
                    ,randomProxy()
                    ,randomProxy()
                    ,randomProxy()
            ,randomProxy()));
        this.addPipeline(new MyPipeline(dataBase)).setDownloader(proxy);
    }
    public int count(){return dataBase.count();}
/*
    设置随机代理
 */
    private Proxy randomProxy(){
        List<String> list=readPool();
        Random random = new Random();
        String proxyString = list.get(random.nextInt(list.size()));
        String ip = proxyString.substring(3, proxyString.indexOf("port"));
        Integer port = Integer.valueOf(proxyString.substring(proxyString.indexOf("t=") + 2));
        Proxy p = new Proxy(ip, port);
        return p;
    }
/*
	设置随机UserAgent
 */
    private String randomUserAgent(){
        List<String> list=readUserAgent();
        Random random = new Random();
        String userAgent = list.get(random.nextInt(list.size()));
        return userAgent;
    }


    public void setSite(){
        Random random = new Random();
        //减少被封风险
        site.setSleepTime(2500 + random.nextInt(5000))
                .setUserAgent(randomUserAgent());
    }


    private List<String> readPool() {
        List<String> list=new ArrayList<>();
        try {
            File file=new File("config/pool.txt");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine(); // 一次读入一行数据
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    private List<String> readUserAgent() {
        List<String> list=new ArrayList<>();
        try {
            File file=new File("config/useragent.txt");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine(); // 一次读入一行数据
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
