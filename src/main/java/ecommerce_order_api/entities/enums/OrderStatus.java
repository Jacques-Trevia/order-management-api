package ecommerce_order_api.entities.enums;

public enum OrderStatus {

	WAITING_PAYMENT {
		@Override
		public boolean canTransitionTo(OrderStatus newStatus) {
			return newStatus == PAID || newStatus == CANCELED;
		}
	},
	PAID {
		@Override
		public boolean canTransitionTo(OrderStatus newStatus) {
			return newStatus == SHIPPED || newStatus == CANCELED;
		}
	},
	SHIPPED {
		@Override
		public boolean canTransitionTo(OrderStatus newStatus) {
			return newStatus == DELIVERED;
		}
	},
	DELIVERED {
		@Override
		public boolean canTransitionTo(OrderStatus newStatus) {
			return false; // status final, não transiciona pra mais nada
		}
	},
	CANCELED {
		@Override
		public boolean canTransitionTo(OrderStatus newStatus) {
			return false; // status final
		}
	};

	public abstract boolean canTransitionTo(OrderStatus newStatus);
}
