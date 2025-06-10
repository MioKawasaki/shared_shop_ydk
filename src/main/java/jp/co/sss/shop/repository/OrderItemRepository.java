package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	/**
	 * カテゴリIDと削除フラグを条件に売上順で取得
	 * 
	 * @param categoryId カテゴリID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT o.item FROM OrderItem o WHERE (:categoryId IS NULL OR :categoryId = 0 OR o.item.category.id = :categoryId) AND o.item.deleteFlag = :deleteFlag GROUP BY o.item ORDER BY SUM(o.quantity) DESC")
	List<Item> findByOrderItemSumQuantity(@Param("categoryId") Integer categoryId, @Param("deleteFlag") int deleteFlag);
}
