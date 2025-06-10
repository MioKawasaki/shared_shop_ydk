package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/" , method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {
		int sortType = 2;
		List<Item> item = orderItemRepository.findByOrderItemSumQuantity(0, 0);
		if (item.isEmpty()) {
			item = itemRepository.findByCategoryIdOrderByInsertDateDesc(0, 0);
			sortType = 1;
		}
		List<ItemBean> itemBean = beanTools.copyEntityListToItemBeanList(item);
		model.addAttribute("items", itemBean);
		model.addAttribute("sortType", sortType);
		return "index";
	}
	
	/**
	 * 商品一覧画面 表示処理
	 * 
	 * @param sortType   ソートの判別
	 * @param categoryId カテゴリ検索で利用
	 * @param model      Viewとの値受け渡し
	 * @return "client/item/list" 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String listSort(@PathVariable Integer sortType, Integer categoryId, Model model) {
		List<Item> item = itemRepository.findByCategoryIdOrderByInsertDateDesc(categoryId, 0);
		if (sortType == 2) {
			item = orderItemRepository.findByOrderItemSumQuantity(categoryId, 0);
		}
		List<ItemBean> itemBean = beanTools.copyEntityListToItemBeanList(item);
		model.addAttribute("items", itemBean);
		return "client/item/list";
	}
	
	/**
	 * 商品詳細画面 表示処理
	 * 
	 * @param id    商品ID判別
	 * @param model Viewとの値受け渡し
	 * @return "client/item/detail" 商品詳細画面
	 */
	@RequestMapping(path = "client/item/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDetail(@PathVariable Integer id, Model model) {
		Item item = itemRepository.getReferenceById(id);
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);
		model.addAttribute("item", itemBean);
		return "client/item/detail";
	}
}
