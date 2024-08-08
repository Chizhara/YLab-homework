package ylab.com.model.order;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CarOrderCreateRequest {
    private Long carId;
}
