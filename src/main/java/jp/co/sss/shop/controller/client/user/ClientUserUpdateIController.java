package jp.co.sss.shop.controller.client.user;


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
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
@Controller
public class ClientUserUpdateIController {
	//会員情報リポジトリ
	@Autowired
	UserRepository userRepository;
	//セッション
	 HttpSession session;
	 
	
	//入力画面初期表示処理 
	@RequestMapping(path = "/client/user/update/input",method = RequestMethod.POST)
	public String updateInputInit(HttpSession session) {
		//セッションスコープから入力フォームを取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		if (userForm == null) {
			
			//セッション情報がない場合、変更対象の情報取得
			UserBean users = (UserBean) session.getAttribute("user");
			
			if (users == null) {
				//ログインしていない場合、エラー
				return "redirect:/syserror" ;
			}
			
			//初期表示用のフォーム情報の生成
			userForm = new UserForm();
			//IDを条件に変更対象のDBを取得の分を付け加えました
		        //User user = userRepository.getReferenceById(users.getId());
			//変更対象の情報をuserFormにコピー
			BeanUtils.copyProperties(user, userForm);
			
			//変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);
		}
		//変更入力画面　表示処理
		return "redirect:/client/user/update/input";
	}
	
	
	//入力画面　表示処理
	@RequestMapping(path = "/client/user/update/input")
	public String updateInput(Model model, HttpSession session) {
		
		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		if(userForm == null) {
			//セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報を画面表示設定
		model.addAttribute("userForm",userForm);
		//getAttributeとremoveAttributeの()の中をresult→errorsに変更しました。
		BindingResult result = (BindingResult) session.getAttribute("errors");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報を画面表示設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("errors");
		}
		//変更入力画面　表示
		return "/client/user/update_input";
	}
	
	//確認ボタン　押下時処理
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute UserForm form, BindingResult result, HttpSession session) {
		
		//直前のセッション情報を保持
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		if(userForm == null) {
			//セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}
		if(form.getAuthority() == null) {
			//権限情報がない場合、セッション情報から値をセット
			form.setAuthority(userForm.getAuthority());
		}
		// 入力フォームをセッションに保存
	    session.setAttribute("userForm", form);

	    // 入力チェック結果にエラーがある場合
	    if (result.hasErrors()) {
	        // エラー情報をセッションに保存
		    //resultをerrorsに変えた
	        session.setAttribute("errors", result);
	        // 変更入力画面表示処理にリダイレクト
	        return "redirect:/client/user/update/input";
	    }
	    	// 入力エラーなし → 変更確認画面表示処理にリダイレクト
	    	return "redirect:/client/user/update/check";
		}
	
	
	 //変更確認画面表示処理
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheckView(Model model, HttpSession session) {

	    // セッションから入力フォーム情報を取得
	    UserForm userForm = (UserForm) session.getAttribute("userForm");
	    if (userForm == null) {
	        // セッション情報がない場合はエラー画面へリダイレクトなど
	        return "redirect:/syserror";
	    }

	    // 入力フォーム情報をリクエストスコープ（Model）に設定
	    model.addAttribute("userForm", userForm);

	    // 確認画面を表示（フォワード）
	    return "/client/user/update_check";
	   	}
	 
	//登録ボタン押下時処理
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete(HttpSession session) {
	    
	    // セッションスコープから入力フォーム情報を取得
	    UserForm userForm = (UserForm) session.getAttribute("userForm");
	    if (userForm == null) {
	        // セッション情報がない場合はエラー画面へリダイレクトなど
	        return "redirect:/syserror";
	    }
	    
	    // DB登録用エンティティを取得（IDで既存ユーザを取得）
	    User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);
	    if (user == null) {
	        // 対象ユーザが存在しない場合はエラー処理
	        return "redirect:/syserror";
	    }
	    
	    // 入力フォーム情報をエンティティにコピー（更新用）
	    BeanUtils.copyProperties(userForm, user);
	    
	    // DB更新処理
	    userRepository.save(user);
	    
	    // ログインユーザの会員変更の場合、セッションスコープの会員情報も更新
	    UserBean loginUser = (UserBean) session.getAttribute("user");
	    if (loginUser != null && loginUser.getId().equals(userForm.getId())) {
	        loginUser.setName(userForm.getName());
	        loginUser.setEmail(userForm.getEmail());
	        // 他に必要な情報も更新
	        session.setAttribute("user", loginUser);
	    }
	    
	    // セッションスコープの入力フォーム情報を削除
	    session.removeAttribute("userForm");
	    
	    // 変更完了画面表示処理にリダイレクト
	    return "redirect:/client/user/update/complete";
	}
	 
	 //変更完了画面
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
	    // 登録完了画面を表示（フォワード）
	    return "/client/user/update_complete";
	}
}
