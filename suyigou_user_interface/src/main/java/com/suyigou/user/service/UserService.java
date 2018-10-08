package com.suyigou.user.service;
import com.suyigou.pojo.TbUser;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	

	
	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);


	/**
	 * 生成短信验证码
	 * @return
	 */
	public void createSmsCode(String phone);

    boolean checkCode(String phone, String smscode);
}
