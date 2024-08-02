package ylab.com.model.order;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CarOrderSearchRequest {
    private UUID carId;
    private UUID customerId;
    private Instant orderDate;
    private CarOrderStatus status;
}
