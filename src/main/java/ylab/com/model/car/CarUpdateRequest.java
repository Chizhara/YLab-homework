package ylab.com.model.car;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarUpdateRequest {
    private Integer price;
    private CarStatus status;
    private String statusDescription;
}
