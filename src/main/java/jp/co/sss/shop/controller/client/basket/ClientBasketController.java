package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;

@Controller
public class ClientBasketController {
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	ItemRepository itemRepository;
	
	//買い物かご画面への遷移
	@RequestMapping(path="/client/basket/list")
	public String basketList(HttpSession session,Model model) {
		List<BasketBean> basketCopy = (List<BasketBean>)session.getAttribute("basketBeans");
		List<BasketBean> beanArray = new ArrayList<>();
		List<BasketBean> beanArrayPush = new ArrayList<>();
		if(basketCopy == null || basketCopy.isEmpty()) {
			//session.setAttribute("basketBeans",beanArray);
		}else {
			for(BasketBean bean : basketCopy) {
				if(bean.getStock() <= 0) {
					model.addAttribute("itemNameListZero",bean.getName());
				}else if(bean.getOrderNum() > bean.getStock()) {
					beanArrayPush.add(bean);
					model.addAttribute("itemNameListLessThan",bean.getName());
				}else {
					beanArrayPush.add(bean);
				}
			}
			session.setAttribute("basketBeans",beanArrayPush);
		}

		return "client/basket/list";
	}
	
	//商品を追加
	@RequestMapping(path="/client/basket/add",method=RequestMethod.POST)
	public String basketAdd(HttpSession session,Item item) {
		boolean createFlag = false;
		List<BasketBean> basketCopy = (ArrayList<BasketBean>)session.getAttribute("basketBeans");
		List<BasketBean> beanArray = new ArrayList<>();
		BasketBean basketInput = new BasketBean();
		BasketBean basketNow = new BasketBean();
		Item itemCopy = itemRepository.getReferenceById(item.getId());
		basketInput.setId(itemCopy.getId());
		basketInput.setName(itemCopy.getName());
		basketInput.setOrderNum(1);
		basketInput.setStock(itemCopy.getStock());
		
		if(basketCopy == null || basketCopy.isEmpty()) {
			beanArray.add(basketInput);
			session.setAttribute("basketBeans", beanArray);
		}else {
			for(BasketBean b : basketCopy) {
				if(b.getName().equals(basketInput.getName())) {
					createFlag = true;
					b.setOrderNum(b.getOrderNum() + 1);
				}
				beanArray.add(b);
			}
			if(!createFlag) {
				basketInput = new BasketBean(itemCopy.getId(),itemCopy.getName(),itemCopy.getStock());
				basketInput.setOrderNum(1);
				beanArray.add(basketInput);
			}
			session.setAttribute("basketBeans",beanArray);
		}

		return "client/basket/list";
	}
	
	//商品をすべて削除
	@RequestMapping(path="/client/basket/allDelete")
	public String basketAllDelete(HttpSession session) {
		session.removeAttribute("basketBeans");
		session.setAttribute("basketBeans", null);
		//リダイレクトに変更
		return "client/basket/list";
	}
	
	//選択した商品を削除
	@RequestMapping(path="/client/basket/delete",method=RequestMethod.POST)
	public String basketDelete(HttpSession session,long id) {
		List<BasketBean> beanArray = (ArrayList<BasketBean>)session.getAttribute("basketBeans");
		List<BasketBean> beanArrayCopy = new ArrayList<>();
		int count = 0;
		for(BasketBean bean : beanArray) {
			if((long)bean.getId() == id) {
				if(bean.getOrderNum() > 1) {
					beanArrayCopy.add(new BasketBean(bean.getId(),bean.getName(),bean.getStock(),bean.getOrderNum() - 1));
				}
				continue;
			}
			beanArrayCopy.add(new BasketBean(bean.getId(),bean.getName(),bean.getStock(),bean.getOrderNum()));
		}
		if(beanArrayCopy.size() == 0) {
			beanArrayCopy = null;
		}
		session.removeAttribute("basketBeans");
		session.setAttribute("basketBeans", beanArrayCopy);
		//リダイレクトに変更
		return "client/basket/list";
	}
	
	
}

