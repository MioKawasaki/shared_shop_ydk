package jp.co.sss.shop.bean;

/**
 * ほしいもの情報クラス
 * 
 * @author YDK
 */
public class WishlistItemBean {
	
	/**
	 * ほしいものID
	 */
	private Integer id;
	
	/**
	 * ほしいものリストへの登録日付
	 */
	private String insertDate;
	
	/**
	 * 商品ID
	 */
	private Integer itemId;
	
	/**
	 * 商品画像
	 */
	private String itemImage;
	
	/**
	 * 商品名
	 */
	private String itemName;
	
	/**
	 * 商品価格
	 */
	private Integer itemPrice;
	
	/**
	 * 商品在庫
	 */
	private Integer itemStock;

	/**
	 * ほしいものIDの取得
	 * 
	 * @return ほしいものID
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * ほしいものIDのセット
	 * 
	 * @param id ほしいものID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * ほしいものリストへの登録日付の取得
	 * 
	 * @return ほしいものリストへの登録日付
	 */
	public String getInsertDate() {
		return insertDate;
	}

	/**
	 * ほしいものリストへの登録日付のセット
	 * 
	 * @param insertDate ほしいものリストへの登録日付
	 */
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	
	/**
	 * 商品IDの取得
	 * 
	 * @return 商品ID
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * 商品IDのセット
	 * 
	 * @param itemId 商品ID
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * 商品画像の取得
	 * 
	 * @return 商品画像
	 */
	public String getItemImage() {
		return itemImage;
	}

	/**
	 * 商品画像のセット
	 * 
	 * @param itemImage 商品画像
	 */
	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	/**
	 * 商品名の取得
	 * 
	 * @return 商品名
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * 商品名のセット
	 * 
	 * @param itemName 商品名
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * 商品価格の取得
	 * 
	 * @return 商品価格
	 */
	public Integer getItemPrice() {
		return itemPrice;
	}

	/**
	 * 商品価格のセット
	 * 
	 * @param itemPrice 商品価格
	 */
	public void setItemPrice(Integer itemPrice) {
		this.itemPrice = itemPrice;
	}

	/**
	 * 商品在庫の取得
	 * 
	 * @return 商品在庫
	 */
	public Integer getItemStock() {
		return itemStock;
	}

	/**
	 * 商品在庫のセット
	 * 
	 * @param itemStock 商品在庫
	 */
	public void setItemStock(Integer itemStock) {
		this.itemStock = itemStock;
	}

}
