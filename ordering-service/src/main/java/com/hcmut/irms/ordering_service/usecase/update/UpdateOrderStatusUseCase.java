package com.hcmut.irms.ordering_service.usecase.update;

public interface UpdateOrderStatusUseCase {
    /**
     * Updates the order status based on an event received from KDS.
     *
     * @param orderId   string representation of the order's Long ID
     * @param newStatus new status string (e.g. "READY")
     */
    void updateStatus(String orderId, String newStatus);
}
