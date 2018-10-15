package com.suyigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.order.service.OrderService;
import com.suyigou.pojo.TbOrder;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;

	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll() {
		return orderService.findAll();
	}


	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return orderService.findPage(page, rows);
	}

	/**
	 * 增加
	 *
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public ResultInfo add(@RequestBody TbOrder order) {
		try {
			//获取当前用户账号
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			order.setUserId(username);
			order.setSourceType("2");//订单来源 PC
			orderService.add(order);
			return new ResultInfo(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "增加失败");
		}
	}

	/**
	 * 修改
	 *
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public ResultInfo update(@RequestBody TbOrder order) {
		try {
			orderService.update(order);
			return new ResultInfo(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id) {
		return orderService.findOne(id);
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public ResultInfo delete(Long[] ids) {
		try {
			orderService.delete(ids);
			return new ResultInfo(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 *
	 * @param order
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows) {
		return orderService.findPage(order, page, rows);
	}
}
