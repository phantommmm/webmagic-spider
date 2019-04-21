package webmagic;

import domain.User;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.List;
import java.util.Random;

public class MyProcessor implements PageProcessor {

    private Site site   = Site.me()
            .setRetryTimes(3)
            //.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            .setCharset("UTF-8")
            .addHeader("Host", "www.zhihu.com")
            .setTimeOut(10000)
            .addHeader("Referer", "https://www.zhihu.com/");

    @Override
    public void process(Page page) {
            //访问失败 输出状态码
            if(page.getStatusCode()!=200){
                System.out.println("访问失败:"+page.getStatusCode());
            }
            //访问关注列表中的用户
            if (page.getUrl().regex("/api/v4/members").match()) {
                List<String> urls = page.getJson().jsonPath("$..data[*].url_token").all();
                for (String url : urls) {
                    page.addTargetRequest("https://www.zhihu.com/people/" + url + "/activities");
                }
            } else {
                page.addTargetRequest(getList(page.getUrl().get()));
                setJsonInfo(page);
            }
    }
/*
    返回关注列表
 */
    private String getList(String url){
            return url.replace("https://www.zhihu.com/people",
                    "https://www.zhihu.com/api/v4/members")
                    .replace("activities",
                            "followees?offset=0&limit=20");
    }
/*
    设置json
 */
    private void setJsonInfo(Page page){
        if (page.getHtml().css("div.Unhuman").match()) {
            System.out.println("账号/ip被封,暂停程序!!!");// 账号/ip被封,终止程序
            try {
                Random random=new Random();
                Thread.sleep(10000+random.nextInt(25000));
                System.out.println("Restarting...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // System.exit(1);
        }

        User user=new User();
        //获取id
        String id=page.getUrl().regex("people/(.*?)/").get();
        //获取jsonString
        String jsonString=page.getHtml().xpath("//script[@id='js-initialData']").get();

        if(jsonString==null){
            System.out.println("无法获取该用户主页:"+id);
            return;
        }

        //jsonString格式化
        jsonString=jsonString.substring(jsonString.indexOf('>')+1, jsonString.lastIndexOf('<'));
        jsonString=jsonString.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">");// 替换转义字符

        Json json=new Json(jsonString);
        //提取User
        String userJsonString=json.jsonPath("$..users['" + id + "']").get();

        if(userJsonString==null){
            System.out.println("无法提取User:"+id);
            return ;
        }

        Json userJson=new Json(userJsonString);
        int flag=setUser(user,userJson,id,page);
        //提交给pipeline处理
        if(flag==1)
        page.putField("data",user);
    }
/*
    User赋值 返回-1 表示赋值失败 返回1 表示成功
*/
    private int setUser(User user,Json userJson,String id,Page page){
        String sex[]={"未知","女","男"};
        Integer sexx;
        //出现异常 说明账户不存在了
        try {
            user.setId(id);
            user.setName(userJson.jsonPath("$..name").get());
            if ((Integer.valueOf(userJson.jsonPath("$..gender").get())) != null) {
                sexx = Integer.valueOf(userJson.jsonPath("$..gender").get());
                if (sexx == -1 || sexx == 1)
                    user.setSex(sex[2]);//1 -1 为男
                else {
                    user.setSex(sex[1]);//0 为女
                }
            } else {
                user.setSex(sex[0]);//其他
            }
            user.setIntroduction(userJson.jsonPath("$..headline").get());
            user.setSchool(userJson.jsonPath("$..educations[*].school.name").get());
            user.setCompany(userJson.jsonPath("$..employments[*].company.name").get());
            user.setJob(userJson.jsonPath("$..employments[*].job.name").get());
            if (userJson.regex("\"business\":\\{").match()) {
                user.setBusiness(userJson.jsonPath("$..business.name").get());
            }
            user.setLocation(userJson.jsonPath("$..locations[*].name").get());
            user.setAnswer(userJson.jsonPath("$..answerCount").get());
            user.setAgree(userJson.jsonPath("$..voteupCount").get());
            user.setFollower(userJson.jsonPath("$..followerCount").get());
        }catch(Exception e){
            System.out.println("该账户不存在:"+id);
            return -1;
        }
        return 1;

    }



    @Override
    public Site getSite() {
        return site;
    }
}
