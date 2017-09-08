package cn.e3.user.service;

import cn.e3.pojo.TbUser;
import cn.e3.utils.E3mallResult;

public interface UserService {
	
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
	 */
	public E3mallResult dataCheck(String param,Integer type);
	
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
	public E3mallResult register(TbUser user);
	
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
	 */
	public E3mallResult login(String username,String password);
	
	/**
	 * 需求: 根据token查询redis集群中用户身份唯一标识session
	 * 参数:String token
	 * 返回值:E3mallResult.ok(user)
	 */
	public E3mallResult findUserWithToken(String token);
	
}
