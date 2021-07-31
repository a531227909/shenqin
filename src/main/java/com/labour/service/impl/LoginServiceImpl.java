package com.labour.service.impl;

import com.labour.dao.UserDao;
import com.labour.entity.Result;
import com.labour.entity.UpLoadImg;
import com.labour.entity.User;
import com.labour.entity.WechatParams;
import com.labour.plugins.LabourPluginFactory;
import com.labour.plugins.Plugin;
import com.labour.plugins.labourResult.LabourReulstPlugin;
import com.labour.service.LoginService;
import com.labour.utils.HttpClientUtil;
import com.labour.utils.Md5Utils;
import com.labour.utils.TokenUtils;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class LoginServiceImpl extends ApplicationObjectSupport implements LoginService {

    private static Logger logger = LogManager.getLogger("org.springframework");

    @Resource
    private UserDao userDao;

    @Resource
    private WechatParams wechatParams;

    @Override
    public Result doLogin(String user_name, String password, String ip) {
        Result result = new Result();
        User user = userDao.selectOneUser(user_name);
        if(user ==null || "".equals(user)){
            result.setCode("1001");
            result.setMsg("账号不存在");
            return result;
        }
        if(Md5Utils.string2Md5(password).equals(user.getPassword())){
            String token = TokenUtils.token(user_name, password);
            user.setToken(token);
            user.setPassword("");
            result.setCode("1000");
            result.setMsg("登录成功");
            result.setData(user);
            logger.info("用户:"+user_name+"登陆成功 IP:"+ip);
        }else{
            result.setCode("1001");
            result.setMsg("您的账号或密码有误");
        }
        return result;
    }

    @Override
    public Result selectAllUser(String data) {
        Result result = new Result();
        List<User> users = userDao.selectAllUser();
        result.setCode("1000");
        result.setMsg(data);
        result.setData(users);
        return result;
    }

    @Override
    public Result doWechatLogin(String code, String headImage, String petName, String genderId, String genderName, String phoneNum, String ip) {
        Result result = new Result();
        String appid = wechatParams.getAppid();
        String secret = wechatParams.getSecret();
        String grant_type = wechatParams.getGrant_type();
        String wechatPostUrl = wechatParams.getPostUrl();
        Map<String, String> weChatLoginParams = new HashMap<>();
        weChatLoginParams.put("appid", appid);
        weChatLoginParams.put("secret", secret);
        weChatLoginParams.put("js_code", code);
        weChatLoginParams.put("grant_type", grant_type);
        String reqStr = HttpClientUtil.getInstance().doPost(wechatPostUrl, weChatLoginParams, "utf-8");
        JSONObject wechatParams = JSONObject.fromObject(reqStr);
        String weChatUnionID = wechatParams.getString("unionid");
        String weChatOpenId = wechatParams.getString("openid");;
        String weChatSessionKey = wechatParams.getString("session_key");;
        User user = userDao.selectOneWechatUser(weChatUnionID, weChatOpenId);
        if(user == null || user.equals("")){
            //默认为普通用户类型
            String user_type_id = "1";
            //默认用户启用
            String status = "1";
            int i = userDao.addWeChatUser(weChatUnionID, weChatOpenId, weChatSessionKey, headImage, petName, genderId, genderName, phoneNum, user_type_id, status);
            if(i == 1){
                String token = TokenUtils.token(weChatUnionID, weChatOpenId);
                user = userDao.selectOneWechatUser(weChatUnionID, weChatOpenId);
                user.setToken(token);
                result.setCode("1000");
                result.setMsg("微信用户登录成功");
                result.setData(user);
                logger.info("微信用户:"+weChatUnionID+"登陆成功 IP:"+ip);
            }else{
                result.setCode("1001");
                result.setMsg("系统故障，微信用户登录失败");
            }
        }else{
            String token = TokenUtils.token(weChatUnionID, weChatOpenId);
            user.setToken(token);
            result.setCode("1000");
            result.setMsg("微信用户登录成功");
            result.setData(user);
            logger.info("微信用户:"+weChatUnionID+"登陆成功 IP:"+ip);
        }
        return result;
    }

    @Override
    public Result testLabour() {
        Result result = new Result();
        String payChannel = "testlabour1";
        List<String> classPaths = getClassPathByPayChannel(payChannel);
        try {
            for(String classPath : classPaths){
                LabourReulstPlugin labourReulstPlugin = LabourPluginFactory.create(classPath);
                result = labourReulstPlugin.hanld();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<String> getClassPathByPayChannel(String labourChannel){
        List<String> classPaths = new ArrayList<>();
        Map<String, Object> beans = getApplicationContext()
                .getBeansWithAnnotation(Plugin.class);
        if (beans != null && beans.size() > 0) {
            Collection<Object> values = beans.values();
            for (Object e : values) {
                if (e instanceof LabourReulstPlugin) {
                    LabourReulstPlugin plugin = (LabourReulstPlugin) e;
                    Class<? extends LabourReulstPlugin> cls = plugin.getClass();
                    Plugin annotation = cls.getAnnotation(Plugin.class);
                    if (annotation != null) {
                        String labourType = annotation.labourType();
                        if (!StringUtils.isEmpty(labourType)) {
                            if(labourType.equals(labourChannel)){
                                classPaths.add(cls.getName());
                            }
                        }
                    }
                }
            }
        }
        return classPaths;
    }

}
