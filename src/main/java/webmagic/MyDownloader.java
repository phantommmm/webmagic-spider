package webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

public class MyDownloader extends HttpClientDownloader {
    private MySpider mySpider=null;

    public MyDownloader(MySpider mySpider){
        this.mySpider=mySpider;
    }

    @Override
    public Page download(Request request, Task task) {
        mySpider.setSite();

        return super.download(request, task);
    }


}
