package ylab.com.model.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ylab.com.model.car.Car;
import ylab.com.model.user.User;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CarOrderSearchParams {
    private Car car;
    private User customer;
    private Instant orderDate;
    private List<CarOrderStatus> status;
}
