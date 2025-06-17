package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientOrderRegistController{
	@Autowired
	HttpSession session;
	@Autowired
	UserRepository userRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderItemRepository orderItemRepository;
	@Autowired
	ItemRepository itemRepository;
	
	/**
     * 注文フォームを初期化し、届け先入力画面を表示
     */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String orderFormDisplay(Model model, OrderForm orderForm) {
		 // 注文フォームの初期化
	        orderForm = new OrderForm();

	    // セッションスコープからログインユーザ情報を取得
	    UserBean user = (UserBean) session.getAttribute("user"); 
	    User users = userRepository.getReferenceById(user.getId());
	    // ユーザ情報を注文フォーム情報に設定
	    orderForm.setPostalCode(users.getPostalCode());
	    orderForm.setName(users.getName());
	    orderForm.setAddress(users.getAddress());
	    orderForm.setPhoneNumber(users.getPhoneNumber());
	    orderForm.setPayMethod(1); // 支払方法の初期値を設定

	    // 注文フォーム情報をセッションスコープに保存
	    model.addAttribute("orderForm", orderForm);
	    session.setAttribute("orderForm", orderForm);

	    // 届け先入力画面にリダイレクト
	    return "client/order/address_input";
	}
	
	/**
     * 届け先入力画面を表示
     */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String orderDeliverd(@ModelAttribute @Valid OrderForm orderForm, BindingResult result, Model model) {
		// セッションから注文フォームを取得
		orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);
		
		// セッションからエラー情報を取得
		result = (BindingResult) session.getAttribute("errors");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);
		    session.removeAttribute("errors");
	}
		return "client/order/address_input";
	}
	
	 /**
     * 支払方法選択画面への遷移処理
     */
    @RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String orderNextPayment(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, Model model) {
    	// セッションから注文フォームを取得
    	orderForm = (OrderForm) session.getAttribute("orderForm");
		if(result.hasErrors()) {
			// エラー情報をセッションに保存
			session.setAttribute("errors", result);
			return "redirect:/client/order/address/input";
		}
		    return "redirect:/client/order/payment/input";
	}
    
    /**
     * 支払方法選択画面を表示
     */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String orderShowPayment(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);
		return "client/order/payment_input";
	}
    
	/**
     * 注文内容確認画面への遷移処理
     */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderNextCheck(Integer payMethod) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(payMethod);
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/check";
	}
    
	/**
     * 注文内容確認画面を表示
     */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheckList(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		// 在庫チェックと調整
		List<String> itemsOutOfStock = new ArrayList<>();
	    List<String> itemsStockAdjusted = new ArrayList<>();
	    
		int i = 0;
        for (BasketBean basket : basketBeans) {
        Item itemItem = itemRepository.getReferenceById(basket.getId());
        if(itemItem.getStock() < basket.getOrderNum()) {
        	itemsStockAdjusted.add(itemItem.getName());
        	basketBeans.get(i).setOrderNum(itemItem.getStock());
        	i++;
        }else if(itemItem.getStock() == 0) {
        	basketBeans.remove(i);
        	itemsOutOfStock.add(itemItem.getName());
        }else {
        	i++;
        }
        }
        
     // 在庫調整結果をモデルに設定
        model.addAttribute("itemNameListLessThan", itemsOutOfStock);
        model.addAttribute("itemNameListZero", itemsStockAdjusted);
        session.setAttribute("basketBeans", basketBeans);
        // 更新された買い物かご情報をセッションに保存
        session.setAttribute("basketBeans", basketBeans);
        
     // 注文アイテムリストと合計金額を計算
        List<OrderItemBean> orderItemList = new ArrayList<>();
        int totalAmount = 0;
        for (BasketBean basket : basketBeans) {
        OrderItemBean orderItem = new OrderItemBean();
        
        Item item = itemRepository.getReferenceById(basket.getId());
        orderItem.setId(item.getId());
        orderItem.setName(item.getName());
        orderItem.setOrderNum(basket.getOrderNum());
        orderItem.setPrice(item.getPrice());
        orderItem.setSubtotal(basket.getOrderNum() * item.getPrice());
        
        //変更（指）↓
        orderItem.setImage(itemRepository.getReferenceById(item.getId()).getImage());
        //↑変更

        // 合計金額を加算
        totalAmount += orderItem.getSubtotal();
        orderItemList.add(orderItem);
            }
        保存する形を"totalamount"から"total"に変更しました
		model.addAttribute("totalAmount//total//", totalAmount);
		model.addAttribute("orderItemBeans", orderItemList);
		model.addAttribute("orderForm", orderForm);
		return "client/order/check";
	}
	
	/**
     * 支払方法選択画面への戻り処理
     */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderBackToPayment() {
		return "redirect:/client/order/address/input";
	}
	
	/**
     * 注文を確定
     */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCommit() {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

        // 在庫チェック
        List<String> stockIssues = new ArrayList<>();
        int i = 0;
        for (BasketBean basket : basketBeans) {
        Optional<Item> itemCheck = itemRepository.findById(basket.getId()); 
        Item itemItem = itemCheck.get();
        if(itemItem.getStock() < basket.getOrderNum()) {
        	stockIssues.add(itemItem.getName());
        	basketBeans.get(i).setOrderNum(itemItem.getStock());
        	i++;
        	 return "redirect:/client/order/check";
        }else if(itemItem.getStock() == 0) {
        	basketBeans.remove(i);
        	stockIssues.add(itemItem.getName());
        	 return "redirect:/client/order/check";
        }else {
        	i++;
        }
        }
        
        // 注文エンティティ作成
        UserBean userBean = (UserBean) session.getAttribute("user");
        User user = userRepository.getReferenceById(userBean.getId());
        Order order = new Order();
        BeanUtils.copyProperties(orderForm, order);
        order.setUser(user);
        orderRepository.save(order);
        
     // 注文アイテム作成
        for (BasketBean basket : basketBeans) {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(basket, orderItem);
            Optional<Item> optionalItem = itemRepository.findById(basket.getId());
            Item item = optionalItem.get();
            orderItem.setItem(item); 
            orderItem.setOrder(order); 
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(basket.getOrderNum());

            orderItemRepository.save(orderItem);

	　　// 在庫を減少させる
            //item.setStock(item.getStock() - basket.getOrderNum());
            //itemRepository.save(item);
        }
        
     // セッションデータのクリア
		session.removeAttribute("orderForm");
        session.removeAttribute("basketBeans");
	
        return  "redirect:/client/order/complete";
	}
	
	/**
     * 注文完了画面を表示
     */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		 return "client/order/complete";
	}
}
