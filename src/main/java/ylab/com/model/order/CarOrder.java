package ylab.com.model.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ylab.com.model.car.Car;
import ylab.com.model.user.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CarOrder {
    private UUID id;
    private User customer;
    private Car car;
    private Instant date;
    private CarOrderStatus carOrderStatus;
}
