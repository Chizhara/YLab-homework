package ylab.com.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.UUID;

@Getter
@Setter
public class Car {
    private UUID id;
    private String brand;
    private String model;
    private Year releaseYear;
    private String price;

}
