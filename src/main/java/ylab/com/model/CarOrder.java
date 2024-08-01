package ylab.com.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Data
public class CarOrder {
    private UUID id;
    private User customer;
    private Car car;
    private Instant date;
    private CarOrderStatus carOrderStatus;
}
