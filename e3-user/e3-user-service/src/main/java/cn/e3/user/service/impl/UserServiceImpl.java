package cn.e3.user.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3.mapper.TbUserMapper;
import cn.e3.pojo.TbUser;
import cn.e3.pojo.TbUserExample;
import cn.e3.pojo.TbUserExample.Criteria;
import cn.e3.user.jedis.JedisDao;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;
@Service
public class UserServiceImpl implements UserService {
	
	//注入usermapper接口代理对象
	@Autowired
	private TbUserMapper userMapper;
	
	//注入jedis
	@Autowired
	private JedisDao jedisDao;
	
	//注入redis集群中session唯一标识
	@Value("${SESSION_KEY}")
	private String SESSION_KEY;
	
	//注入redis集群中用户身份信息过期时间常量
	@Value("${SESSION_KEY_EXPIRE_TIME}")
	private Integer SESSION_KEY_EXPIRE_TIME;
	
	
	
	/**
	 * 需求:校验数据是否可用.(校验用户名,手机号,邮箱是否被占用)
	 * 参数:String param,Integer type
	 * 返回值:
	 * {
		status: 200 //200 成功
		msg: "OK" // 返回信息消息
		data: false // 返回数据，true：数据可用，false：数据不可用
		}
		业务:
		type=1: param=username
		type=2: param=phone
		type=3: param=email
		思考:服务是否发布?
	 */
	public E3mallResult dataCheck(String param, Integer type) {
		
		//创建TbUserExample对象
		TbUserExample example = new TbUserExample();
		//创建example的criteria对象,设置查询参数
		Criteria createCriteria = example.createCriteria();
		//设置参数查询
		if (type==1) {
			//设置用户名参数
			createCriteria.andUsernameEqualTo(param);
		}else if(type==2){
			//校验电话号码
			createCriteria.andPhoneEqualTo(param);
		}else if(type==3){
			createCriteria.andEmailEqualTo(param);
		}
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		//判断数据库中是否有值
		if (list !=null && list.size() > 0) {
			//有值,说明此用户,电话,邮箱已被占有
			return E3mallResult.ok(false);
		}
		//否则,数据可用
		return E3mallResult.ok(true);
		
	}

	/**
	 * 需求:用户注册
	 * 参数:TbUser user
	 * 返回值:
	 * 成功时:
	 * {
		status: 200
		msg: "注册成功!"
		data: null
		}

	 * 失败时:
	 * {
		status: 400
		msg: "注册失败. 请校验数据后请再提交数据."
		data: null
		}
	 */
	public E3mallResult register(TbUser user) {
		try {
			// 用户数据密码需要被加密
			//先获取密码
			String newPwd = user.getPassword();
			//加密
			newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
			//把加密后的密码设置到用户对象中
			user.setPassword(newPwd);
			//设置用户数据更新时间
			Date date = new Date();
			user.setCreated(date);
			user.setUpdated(date);
			
			//保存用户注册数据到数据库
			userMapper.insert(user);
			
			//成功
			return E3mallResult.build(200, "注册成功!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//注册失败
			return E3mallResult.build(400, "注册失败. 请校验数据后请再提交数据.");
		}
		
	}

	/**
	 * 需求:用户登录
	 * 参数:String username,String password
	 * 返回值:
	 * 成功时:
	 * {
		status: 200
		msg: "OK"
		data: "fe5cb546aeb3ce1bf37abcb08a40493e" //登录成功，返回token
	   }
	 * 失败时:
	 * {
		status: 201
		msg: "登录失败!"
		data:null
	   }
	   
	   业务分析:
	   1. 接收登录的用户名和密码
	   2. 根据用户查询数据库,判断此用户是否存在
	   3. 如果此用户不存在,返回用户名或密码错误
	   4. 如果此用户存在,获取用户数据,判断用户密码
	   5. 密码加密和数据库密码匹配
	   6. 如果不能匹配成功,返回用户名或密码错误
	   7. 否则,登录成功
	   8. 生成token
	   9. 把用户身份信息写入redis集群
	   10. 给用户身份信息设置过期时间
	   11. 返回token
	   把用户身份信息写入redis服务器:
	   	数据数据是String
	   	KEY:SESSION_KEY:token
	   	VALUE:json(user)
	 */
	public E3mallResult login(String username, String password) {
		
		//创建example对象
		TbUserExample example = new TbUserExample();
		//创建example的criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数,根据用户名查询
		createCriteria.andUsernameEqualTo(username);
		//1. 接收用户登录名和密码
		//2. 根据用户查询数据库,判断此用户是否存在
		List<TbUser> list = userMapper.selectByExample(example);
		//判断用户名是否存在
		if (list.isEmpty()) {
			//如果此用户名查询为空
			return E3mallResult.build(201, "用户名或密码错误");
		}
		//如果次用户存在,获取用户数据,判断用户密码
		TbUser user = list.get(0);
		//获取用户密码
		String oldPwd = user.getPassword();
		//给登录密码加密,进行匹配
		String newPwd = DigestUtils.md5DigestAsHex(password.getBytes());
		//对比密码是否相同
		if (!newPwd.equals(oldPwd)) {
			return E3mallResult.build(201, "用户名或密码错误");
		}
		
		//否则,登录成功
		//生成UUID token
		String token = UUID.randomUUID().toString();
		//把用户身份信息写入到redis集群
		jedisDao.set(SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
		//设置redis集群中用户身份信息过期数据
		jedisDao.expire(SESSION_KEY+":"+token, SESSION_KEY_EXPIRE_TIME);
		//返回token
		return E3mallResult.ok(token);
	}

	/**
	 * 需求: 根据token查询redis集群中用户身份唯一标识session
	 * 参数:String token
	 * 返回值:E3mallResult.ok(user)
	 * 过期时间重置:
	 * 1. 重新根据token查询用户身份信息
	 * 2. 重新访问系统,过期时间重新设置
	 */
	public E3mallResult findUserWithToken(String token) {
		
		//根据token查询redis服务器
		String userJson = jedisDao.get(SESSION_KEY+":"+token);
		//判断用户身份信息在redis服务器中身份存在
		if (StringUtils.isBlank(userJson)) {
			//不存在
			return E3mallResult.build(401, "用户身份信息已过期!");
		}
		//如果用户身份不为空
		//把json格式数据转成user对象
		TbUser user = JsonUtils.jsonToPojo(userJson, TbUser.class);
		
		//设置过期时间
		jedisDao.expire(SESSION_KEY+":"+token, SESSION_KEY_EXPIRE_TIME);
		//返回
		return E3mallResult.ok(user);
	}

}
