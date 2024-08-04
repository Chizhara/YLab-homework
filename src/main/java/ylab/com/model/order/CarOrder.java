package ylab.com.model.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ylab.com.model.Entity;
import ylab.com.model.car.Car;
import ylab.com.model.user.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CarOrder implements Entity {
    private UUID id;
    private User customer;
    private Car car;
    private Instant date;
    private CarOrderStatus carOrderStatus;
}
