package jp.co.sss.shop.controller.client.wishlist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.bean.WishlistItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.WishlistItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.repository.WishlistItemRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * ほしいものリスト 一覧表示機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientWishlistController {
	/**
	 * ほしいもの情報
	 */
	@Autowired
	WishlistItemRepository wishlistItemRepository;
	
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;
	
	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;
	
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * ほしいものリスト画面 表示処理
	 * 
	 * @param model Viewとの値渡し
	 * @return "client/wishlist/wishlist" ほしいものリスト画面
	 */
	@RequestMapping(path = "/client/wishlist/list", method = RequestMethod.GET)
	public String wishlist(Model model) {
		
		// ログイン中のユーザのほしいものリストを検索
		List<WishlistItem> wishlistItems = wishlistItemRepository.findUserIdByWishlistItemOrderByInsertDate(((UserBean)session.getAttribute("user")).getId());
		
		// Beanにコピー
		List<WishlistItemBean> wishlistItemBeans = beanTools.generateWishlistItemBeanList(wishlistItems);
		
		// Viewに値渡し
		model.addAttribute("wishlistBeans", wishlistItemBeans);
		
		return "client/wishlist/wishlist";
	}
	
	/**
	 * ほしいものリスト画面 追加処理
	 * 
	 * @param id 商品ID
	 * @return "redirect:/client/wishlist/list" 表示処理にリダイレクト
	 */
	@RequestMapping(path = "/client/wishlist/add", method = RequestMethod.POST)
	public String wishlistAdd(@RequestParam Integer id) {
		
		// 商品IDから商品情報を検索
		Item item = itemRepository.getReferenceById(id);
		
		// 会員IDから会員情報を検索
		User user = userRepository.getReferenceById(((UserBean)session.getAttribute("user")).getId());
		
		// ほしいもの情報
		WishlistItem wishlistItem = new WishlistItem();
		
		// ほしいもの情報に商品情報をセット
		wishlistItem.setItem(item);
		
		// ほしいもの情報に会員情報をセット
		wishlistItem.setUser(user);
		
		// ほしいもの情報を更新
		wishlistItemRepository.save(wishlistItem);
		
		return "redirect:/client/wishlist/list";
	}
	
	/**
	 * ほしいものリスト画面 削除処理
	 * 
	 * @param id ほしいものID
	 * @return "redirect:/client/wishlist/list" 表示処理にリダイレクト
	 */
	@RequestMapping(path = "/client/wishlist/delete", method = RequestMethod.POST)
	public String wishlistDelete(@RequestParam Integer id) {
		
		// ほしいもの情報を削除
		wishlistItemRepository.deleteById(id);
		
		return "redirect:/client/wishlist/list";
	}
}
