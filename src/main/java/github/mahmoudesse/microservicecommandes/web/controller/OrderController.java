package github.mahmoudesse.microservicecommandes.web.controller;

import github.mahmoudesse.microservicecommandes.config.ApplicationPropertiesConfiguration;
import github.mahmoudesse.microservicecommandes.dao.OrderDao;
import github.mahmoudesse.microservicecommandes.model.Order;
import github.mahmoudesse.microservicecommandes.web.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@RestController
public class OrderController implements HealthIndicator {
  @Autowired
  OrderDao orderDao;

  @Autowired
  ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

  @GetMapping(value = "/Orders")
  public List<Order> getOrders() {
    System.out.println("*** *** Actuator: OrderController.getOrders()");

    // Fetch the number of days to subtract from application properties
    int lastOrderDate = applicationPropertiesConfiguration.getLastOrder();

    // Calculate the LocalDateTime for the start date
    LocalDateTime startDateTime = LocalDateTime.now().minusDays(lastOrderDate);
    System.out.println("*** *** Actuator: startDateTime: " + startDateTime);

    // Fetch orders from the repository
    List<Order> orders = orderDao.findByCreatedDateAfter(startDateTime);

    System.out.println("*** *** Actuator: Orders");
    orders.forEach(System.out::println);

    if (orders.isEmpty()) {
      throw new OrderNotFoundException("No orders found");
    }

    return orders;
  }

  @Override
  public Health health() {
    System.out.println("*** **** Actuator: OrderController.health()");
    System.out.println("*** **** Actuator: orderLast: " + applicationPropertiesConfiguration.getLastOrder());

    List<Order> orders = orderDao.findAll();
    if (orders.isEmpty()) {
      return Health.down().build();
    }

    return Health.up().build();
  }


}
