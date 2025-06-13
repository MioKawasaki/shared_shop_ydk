package jp.co.sss.shop.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * ほしいものリスト情報のエンティティクラス
 *
 * @author YDK
 */
@Entity
@Table(name = "wishlist_items")
public class WishlistItem {
	
	/**
	 * ほしいものID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_wishlist_items_gen")
	@SequenceGenerator(name = "seq_wishlist_items_gen", sequenceName = "seq_wishlist_items", allocationSize = 1)
	private Integer id;
	
	/**
	 * 登録日時
	 */
	@Column(insertable = false, updatable = false)
	private Date insertDate;
	
	/**
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;
	
	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	/**
	 * ほしいものIDの取得
	 * @return ほしいものID
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * ほしいものIDのセット
	 * @param id ほしいものID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Date getInsertDate() {
		return insertDate;
	}
	
	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}
	
	/**
	 * 商品エンティティの取得
	 * @return 商品エンティティ
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * 商品エンティティのセット
	 * @param item 商品エンティティ
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	
	/**
	 * 会員エンティティの取得
	 * @return 会員エンティティ
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * 会員エンティティのセット
	 * @param user 会員エンティティ
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
