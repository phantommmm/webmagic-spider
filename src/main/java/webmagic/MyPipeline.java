package webmagic;

import database.DataBase;
import domain.User;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MyPipeline implements Pipeline {
    private DataBase dataBase=null;
    public  MyPipeline(DataBase dataBase){
        this.dataBase=dataBase;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        User user=resultItems.get("data");
        if(user!=null){
            System.out.println("正在爬取:"+resultItems.getRequest().getUrl());
            dataBase.add(user);
        }
    }
}
