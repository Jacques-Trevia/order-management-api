package ecommerce_order_api.entities.enums;

public enum PaymentStatus {

	PENDING,
	APPROVED,
	REJECTED;
	
	public boolean canTransitionTo(PaymentStatus newStatus) {
		if (this == PENDING) {
			return newStatus == APPROVED || newStatus == REJECTED;
		}
		return false; // APPROVED e REJECTED são finais
	}
}
