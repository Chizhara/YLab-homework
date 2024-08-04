package ylab.com.model.car;

import lombok.Builder;
import lombok.Data;

import java.time.Year;

@Data
@Builder
public class CarSearchRequest {
    private String brand;
    private String model;
    private Year releaseYear;
    private PriceParam priceParam;
    private CarStatus status;

    @Data
    @Builder
    public static class PriceParam {
        private Integer value;
        private boolean lower;
    }
}
