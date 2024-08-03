package ylab.com.model.car;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CarSearchParams {
    private String brand;
    private String model;
    private Year releaseYear;
    private PriceParam price;
    private CarStatus status;

    @Getter
    @Setter
    @EqualsAndHashCode
    @Builder
    public static class PriceParam {
        private Integer value;
        private boolean lower;
    }
}

