package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
@RequestMapping("/client/order")
public class ClientOrderShowController {

		/**
		 * 注文情報
		 */
		@Autowired
		OrderRepository orderRepository;

		/**
		 * セッション
		 */
		@Autowired
		HttpSession session;

		/**
		 * 合計金額計算サービス
		 */
		@Autowired
		PriceCalc priceCalc;

		/**
		 * Entity、Form、Bean間のデータ生成、コピーサービス
		 */
		@Autowired
		BeanTools beanTools;

		/**
		 * 一覧取得、一覧画面表示　処理
		 *
		 * @param model Viewとの値受渡し
		 * @param pageable ページング情報
		 * @return "admin/order/list" 注文情報 一覧画面へ
		 */
		@RequestMapping(path = "/list", method = { RequestMethod.GET, RequestMethod.POST })
		public String showOrderList(Model model, Pageable pageable) {

			//ユーザの購入情報をとってくる
			//表示画面でページングが必要なため、ページ情報付きの検索を行う
			UserBean userBean = (UserBean)session.getAttribute("user");
			
			if(userBean == null) {
				return "redirect:/login";
			}
			
			Page<Order> orderList = orderRepository.findByUserIdOrderByInsertDateDescIdDesc(userBean.getId(),pageable);

			// 注文情報リストを生成
			List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
			for (Order order : orderList) {
				// BeanToolsクラスのcopyEntityToOrderBeanメソッドを使用して表示する注文情報を生成
				OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
				//orderレコードから紐づくOrderItemのListを取り出す
				List<OrderItem> orderItemList = order.getOrderItemsList();
				//PriceCalcクラスのorderItemPriceTotalメソッドを使用して合計金額を算出
				int total = priceCalc.orderItemPriceTotal(orderItemList);

				//合計金額のセット
				orderBean.setTotal(total);

				orderBeanList.add(orderBean);
			}

			// 注文情報リストをViewへ渡す
			model.addAttribute("pages", orderList);
			model.addAttribute("orders", orderBeanList);

			return "client/order/list";

		}

		/**
		 * 詳細表示処理
		 *
		 * @param id 詳細表示対象ID
		 * @param model Viewとの値受渡し
		 * @return "admin/order/detail" 詳細画面　表示
		 */
		
		
		@RequestMapping(path = "/detail/{id}",method = RequestMethod.GET)
		public String showOrder(@PathVariable int id, Model model) {


			
			UserBean userBean = (UserBean)session.getAttribute("user");
			//ログイン
			if(userBean==null) {
				return "redirect:/login";
			}
			
			// 選択された注文情報に該当する情報を取得
			Order order = orderRepository.getReferenceById(id);			
			 
			//不正アクセス防止
			if(!order.getUser().getId().equals(userBean.getId())) {
				return"redirect:/login";
			}
			
			// 表示する注文情報を生成
			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

			// 注文商品情報を取得
			List<OrderItemBean> orderItemBeanList = beanTools.generateOrderItemBeanList(order.getOrderItemsList());

			// 合計金額を算出
			int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);

			// 注文情報をViewへ渡す
			model.addAttribute("order", orderBean);
			model.addAttribute("orderItemBeans", orderItemBeanList);
			model.addAttribute("total", total);

			return "client/order/detail";
		}
}
