package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.WishlistItem;

/**
 * wishlist_itemsテーブル用リポジトリ
 * 
 * @author YDK
 */
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
	
	// 商品IDと会員IDを条件にほしいもの情報を検索
	@Query("SELECT w FROM WishlistItem w WHERE w.item.id = :itemId AND w.user.id = :userId")
	WishlistItem findItemIdByWishlistItem(@Param("itemId") Integer itemId, @Param("userId") Integer userId);
	
	// 会員IDを条件に新着順でほしいもの情報を検索
	@Query("SELECT w FROM WishlistItem w WHERE w.user.id = :id ORDER BY w.insertDate DESC")
	List<WishlistItem> findUserIdByWishlistItemOrderByInsertDate(@Param("id") Integer id);
}
