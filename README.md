# webmagic-spider
基于webmagic框架的Java爬虫项目, 爬取知乎用户信息,使用Mybatis持久化,ELK可视化数据

## 运行环境
*  webmagic
*  maven
*  mybatis
*  mysql
*  ELK(可以忽略)

## 反爬虫措施
*  使用代理池，运行主程序前，先运行爬虫代理，将爬取到的代理保存到代理池
*  使用多个UserAgent模拟不同浏览器
*  线程休眠随机时间，被封后随机时间重启，模拟人工访问

## 配置
初次运行请配置 [jdbc.properties](https://github.com/phantommmm/webmagic-spider/blob/master/config/jdbc.properties)
和 [useragent.txt](https://github.com/phantommmm/webmagic-spider/blob/master/config/useragent.txt)

## 运行
先运行代理爬虫，再运行主程序

代理爬虫入口: [Pool.java](https://github.com/phantommmm/webmagic-spider/blob/master/src/main/java/pool/Pool.java)

主程序入口: [SpiderStart.java](https://github.com/phantommmm/webmagic-spider/blob/master/src/main/java/spider/SpiderStart.java)

### 效果展示 
![爬取过程](https://github.com/phantommmm/webmagic-spider/blob/master/img/working.png)

![知乎职业分布](https://github.com/phantommmm/webmagic-spider/blob/master/img/business.png)

![知乎地域分布](https://github.com/phantommmm/webmagic-spider/blob/master/img/location.png)
