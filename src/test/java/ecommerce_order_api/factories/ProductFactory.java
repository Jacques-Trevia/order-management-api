package ecommerce_order_api.factories;

import java.math.BigDecimal;

import ecommerce_order_api.entities.Category;
import ecommerce_order_api.entities.Product;

public class ProductFactory {

	public static Product createProduct() {
		Category category = CategoryFactory.createCategory();
		Product product = new Product(1L, "Notebook Dell", "Notebook i5, 8GB RAM", new BigDecimal("3000.00"), 10);
		product.getCategories().add(category);
		return product;
	}
	
	public static Product createProduct(String name) {
		Product product = createProduct();
		product.setName(name);
		return product;
	}
}
