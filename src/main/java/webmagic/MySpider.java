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
    String userAgents[]={"Mozilla/5.0 (Windows NT 7.8; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3497.100 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.6; Win64; x64; rv:66.0) Gecko/20100101 Firefox/63.0",
            "Mozilla/5.0 (Windows NT 9.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3325.181 Safari/537.36",
    "Mozilla/5.0 (Windows NT 5.9; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36"};

    private DataBase dataBase=null;

    public MySpider(PageProcessor pageProcessor) {

        super(pageProcessor);
        dataBase=new DataBase();


        //随机    设置代理

        HttpClientDownloader proxy=new HttpClientDownloader();

    proxy.setProxyProvider(SimpleProxyProvider
            //.from(new Proxy("125.105.189.177",9999)
                    //,new Proxy("163.204.241.178",9999)));
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
        /* 读入TXT文件 */
        // String pathname = "trainData.txt"; //
        // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
        // File filename = new File(pathname); // 要读取以上路径的input。txt文件
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
        /* 读入TXT文件 */
        // String pathname = "trainData.txt"; //
        // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
        // File filename = new File(pathname); // 要读取以上路径的input。txt文件
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
