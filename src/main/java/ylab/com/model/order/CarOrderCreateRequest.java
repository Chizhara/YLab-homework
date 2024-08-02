package ylab.com.model.order;

import lombok.Data;

import java.util.UUID;

@Data
public class CarOrderCreateRequest {
    private UUID customerId;
    private UUID carId;
}
