package jp.co.sss.shop.controller.client.user;


import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	
	@Autowired
	UserRepository userRepository;
	
	//リンクからの移動
	@RequestMapping(path="/client/user/regist/input/init")
	public String registUserInput(Model model,HttpSession session) {
		UserForm userForm = new UserForm();
		session.setAttribute("userSession", userForm);
		return "redirect:/client/user/regist/input";
	}
	
	//バリデーション関連とチェック画面への遷移
	@RequestMapping(path="/client/user/regist/check",method=RequestMethod.POST)
	public String registUserCheck(Model model,@Valid UserForm userForm,BindingResult bindingResult,HttpSession session) {
		session.setAttribute("userSession", userForm);
		if(bindingResult.hasErrors()) {
			model.addAttribute("userForm",userForm);
			session.setAttribute("userError", bindingResult);
			return "redirect:/client/user/regist/input";
		}else {
			model.addAttribute("userForm",userForm);
			return "client/user/regist_check";
		}
	}
	
	//インプット画面への遷移
	@RequestMapping(path="/client/user/regist/input",method={RequestMethod.POST,RequestMethod.GET})
	public String registUserReInput(Model model,UserForm userForm,HttpSession session,BindingResult bindingResult) {
		bindingResult = (BindingResult)session.getAttribute("userError");
		model.addAttribute("org.springframework.validation.BindingResult.userForm",bindingResult);
		model.addAttribute("userForm",(UserForm)session.getAttribute("userSession"));
		return "client/user/regist_input";
	}
	
	//確認画面
	@RequestMapping(path="/client/user/regist/complete",method= {RequestMethod.GET,RequestMethod.POST})
	public String registUserComplete(Model model,HttpSession session) {
		System.out.println("テスト2");
		UserForm userForm = (UserForm)session.getAttribute("userSession");
		User user = new User();
		user.setEmail(userForm.getEmail());
		user.setPassword(userForm.getPassword());
		user.setName(userForm.getName());
		user.setPostalCode(userForm.getPostalCode());
		user.setAddress(userForm.getAddress());
		user.setPhoneNumber(userForm.getPhoneNumber());
		user.setAuthority(2);
		user.setDeleteFlag(0);
		long sqlDateNow = System.currentTimeMillis();
		Date now = new Date(sqlDateNow);
		user.setInsertDate(now);
		userRepository.save(user);
		session.removeAttribute("userSession");
		return "client/user/regist_complete";
	}
}
