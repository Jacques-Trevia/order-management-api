package ecommerce_order_api.factories;

import ecommerce_order_api.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		return new Category(1L, "Eletrônicos");
	}
	
	public static Category createCategory(Long id, String name) {
		return new Category(id, name);
	}
}
