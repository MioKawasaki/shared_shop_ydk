package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientUserShowController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpSession session;

    /**
     * 一般会員の詳細表示
     * 
     * @param model 画面へのデータ受け渡し
     * @return 詳細画面のビュー名
     */
    @RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
    public String showClientUserDetail(Model model) {

        // ログイン中のユーザー情報をセッションから取得
        UserBean loginUser = (UserBean) session.getAttribute("user");

        if (loginUser == null) {
            // ログインしていない、またはセッション切れ
            return "redirect:/syserror";
        }

        // 最新情報をDBから取得（念のため）
        User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);
        if (user == null) {
            // DBに該当ユーザーが存在しない
            return "redirect:/syserror";
        }

        // Entity → Bean に変換
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);

        // 詳細情報を画面に渡す
        model.addAttribute("userBean", userBean);

        return "client/user/detail"; // ここにJSPやHTMLがある前提
    }
}
