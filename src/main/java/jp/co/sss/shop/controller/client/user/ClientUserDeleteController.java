package jp.co.sss.shop.controller.client.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserDeleteController {
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(path="/client/user/delete/check")
	public String userDeleteCheck(Model model,HttpSession session) {
		UserBean user = (UserBean)session.getAttribute("user");
		UserForm userForm = new UserForm();
//		userForm.setId(user.getId());
//		userForm.setEmail(user.getEmail());
//		userForm.setPassword(user.getPassword());
//		userForm.setName(user.getName());
//		userForm.setPostalCode(user.getPostalCode());
//		userForm.setAddress(user.getAddress());
//		userForm.setPhoneNumber(user.getPhoneNumber());
//		userForm.setAuthority(user.getAuthority());
		Optional<User> userOpt = userRepository.findById(user.getId());
		User users = userOpt.get();
		System.out.println(user.getId());
		model.addAttribute("userForm",users);
		//model.addAttribute("",);
		return "client/user/delete_check";
	}
	
	@RequestMapping(path="/client/user/delete/complete")
	public String userDeleteComplete(HttpSession session) {
		UserBean user = (UserBean)session.getAttribute("user");
		Optional<User> userOpt = userRepository.findById(user.getId());
		User users = userOpt.get();
		users.setDeleteFlag(1);
		userRepository.save(users);
		//追加↓
		session.removeAttribute("user");
		//↑
		return "client/user/delete_complete";
	}
}
