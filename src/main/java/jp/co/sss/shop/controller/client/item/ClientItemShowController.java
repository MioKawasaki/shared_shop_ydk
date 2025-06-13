package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.WishlistItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.WishlistItemRepository;
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
	
	/**
	 * 注文商品情報
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * ほしいもの情報
	 */
	@Autowired
	WishlistItemRepository wishlistItemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/" , method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {
		
		// 表示順を売れ筋順に初期化
		int sortType = 2;
		
		// 売れ筋順の注文商品情報を検索
		List<Item> item = orderItemRepository.findByOrderItemSumQuantity(0, 0);
		
		// 売れ筋順の注文商品情報がない場合
		if (item.isEmpty()) {
			
			// 新着順の商品情報を検索
			item = itemRepository.findByCategoryIdOrderByInsertDateDesc(0, 0);
			
			// 表示順を新着順に変更
			sortType = 1;
		}
		
		// 商品情報Beanにコピー
		List<ItemBean> itemBean = beanTools.copyEntityListToItemBeanList(item);
		
		// Viewへ値渡し
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
		
		// 商品情報を新着順で検索
		List<Item> item = itemRepository.findByCategoryIdOrderByInsertDateDesc(categoryId, 0);
		
		// 表示順が売れ筋順の場合
		if (sortType == 2) {
			
			// 注文商品情報を売れ筋順で検索
			item = orderItemRepository.findByOrderItemSumQuantity(categoryId, 0);
		}
		
		// 商品情報Beanにコピー
		List<ItemBean> itemBean = beanTools.copyEntityListToItemBeanList(item);
		
		// Viewへ値渡し
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
		
		// 商品情報を主キー検索
		Item item = itemRepository.getReferenceById(id);
		
		// 商品情報Beanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);
		
		// Viewへ値渡し
		model.addAttribute("item", itemBean);
		
		// ほしいものリストへ追加ボタンのための処理
		// ユーザ情報を取得
		UserBean user = ((UserBean)session.getAttribute("user"));
		
		// ほしいものリスト登録判定をfalseに初期化
		boolean inWishlist = false;
		
		// ログインされている場合
		if (user != null) {
			
			// ユーザが商品をほしいものリストに登録しているか検索
			WishlistItem wishlistItem = wishlistItemRepository.findItemIdByWishlistItem(id, user.getId());
			
			// 登録されている場合、ほしいものリスト登録判定にtrueを代入
			inWishlist = (wishlistItem != null);
		}
		
		// Viewへ値渡し
		model.addAttribute("inWishlist", inWishlist);
		
		return "client/item/detail";
	}
}
