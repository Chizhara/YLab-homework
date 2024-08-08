package ylab.com.model.car;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ylab.com.model.Entity;

import java.time.Year;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Car implements Entity {
    private Long id;
    private String brand;
    private String model;
    private Year releaseYear;
    private Integer price;
    private CarStatus status;
    private String statusDescription;
}
