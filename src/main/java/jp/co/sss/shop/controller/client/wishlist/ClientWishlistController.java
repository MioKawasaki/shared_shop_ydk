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

@Controller
public class ClientWishlistController {
	
	@Autowired
	WishlistItemRepository wishlistItemRepository;
	
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	BeanTools beanTools;
	
	@RequestMapping(path = "/client/wishlist/list", method = RequestMethod.GET)
	public String wishlist(Model model) {
		List<WishlistItem> wishlistItems = wishlistItemRepository.findUserIdByWishlistItemOrderByInsertDate(((UserBean)session.getAttribute("user")).getId());
		List<WishlistItemBean> wishlistItemBeans = beanTools.generateWishlistItemBeanList(wishlistItems);
		model.addAttribute("wishlistBeans", wishlistItemBeans);
		return "client/wishlist/wishlist";
	}
	
	@RequestMapping(path = "/client/wishlist/add", method = RequestMethod.POST)
	public String wishlistAdd(@RequestParam Integer id) {
		Item item = itemRepository.getReferenceById(id);
		User user = userRepository.getReferenceById(((UserBean)session.getAttribute("user")).getId());
		WishlistItem wishlistItem = new WishlistItem();
		wishlistItem.setItem(item);
		wishlistItem.setUser(user);
		wishlistItemRepository.save(wishlistItem);
		return "redirect:/client/wishlist/list";
	}
	
	@RequestMapping(path = "/client/wishlist/delete", method = RequestMethod.POST)
	public String wishlistDelete(@RequestParam Integer id) {
		wishlistItemRepository.deleteById(id);
		return "redirect:/client/wishlist/list";
	}
}
