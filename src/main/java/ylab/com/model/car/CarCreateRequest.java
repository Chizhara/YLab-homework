package ylab.com.model.car;

import lombok.Builder;
import lombok.Data;

import java.time.Year;

@Data
@Builder
public class CarCreateRequest {
    private String brand;
    private String model;
    private Year releaseYear;
    private Integer price;
    private CarStatus status;
    private String statusDescription;
}
