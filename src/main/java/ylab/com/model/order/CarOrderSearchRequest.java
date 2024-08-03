package ylab.com.model.order;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CarOrderSearchRequest {
    private UUID carId;
    private UUID customerId;
    private Instant orderDate;
    private List<CarOrderStatus> status;
}
