package database;


import domain.User;
import mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBase {

    private SqlSessionFactory sqlSessionFactory=null;

    private SqlSession sqlSession=null;

    private Logger logger=LoggerFactory.getLogger(getClass());

    public DataBase(){
            init();
            isExist();
    }

/*
    连接初始化
 */
    private void init(){
        String resource="mybatis.xml";
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            sqlSession=sqlSessionFactory.openSession();
        } catch (IOException e) {
            System.out.println("连接初始化失败！!");
            logger.error(e.toString()+e.getMessage());
            System.exit(1);
        }
    }
/*
    是否存在表 不存在则创建
 */
    private void isExist(){
        try {
            UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
            userMapper.isExist();
            sqlSession.commit();
        }catch (Exception e){
            System.out.println("isExist方法失败!!");
            logger.error(e.toString()+e.getMessage());
        }

    }

    public int count(){
       try {
           UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
           int num=userMapper.num();
           return num;
       }catch (Exception e){
           System.out.println("count方法失败！！");
           logger.error(e.toString()+e.getMessage());
       }
       return 0;
    }

    public  void add(User user){
        try {
            UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
            userMapper.add(user);
            sqlSession.commit();
        }catch (Exception e){
            System.out.println("add方法失败!!  用户:"+user.getId()+"已添加");
            logger.error(e.toString()+e.getMessage());
        }

    }

    public void close(){
        sqlSession.close();
    }

}
