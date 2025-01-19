package github.mahmoudesse.microservicecommandes.web.controller;

import github.mahmoudesse.microservicecommandes.config.ApplicationPropertiesConfiguration;
import github.mahmoudesse.microservicecommandes.dao.OrderDao;
import github.mahmoudesse.microservicecommandes.model.Order;
import github.mahmoudesse.microservicecommandes.web.exceptions.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController implements HealthIndicator {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  OrderDao orderDao;

  @Autowired
  ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

  @GetMapping(value = "/getAll")
  public List<Order> getOrders() {
    log.info("Actuator: OrderController.getOrders()");

    int lastOrderDate = applicationPropertiesConfiguration.getLastOrder();

    LocalDateTime startDateTime = LocalDateTime.now().minusDays(lastOrderDate);
    log.info("Actuator: startDateTime: " + startDateTime);

    List<Order> orders = orderDao.findByCreatedDateAfter(startDateTime);

    log.info("Actuator: Orders");
    orders.forEach(System.out::println);

    if (orders.isEmpty()) {
      log.warn("Actuator: OrderController.getOrders() - No orders found");
      throw new OrderNotFoundException("No orders found");
    }

    return orders;
  }

  @GetMapping(value = "/getById/{id}")
  public Order getOrder(@PathVariable int id) {
    log.info("Actuator: OrderController.getOrder()");
    log.info("Actuator: id: " + id);

    Order order = orderDao.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));

    log.info("Actuator: order: " + order);

    return order;
  }

  @PostMapping("/create")
  public Order createOrder(@RequestBody Order order) {

    log.info("Actuator: OrderController.createOrder()");
    log.info("Actuator: order: " + order);

    order.setCreatedDate(LocalDateTime.now());
    Order savedOrder = orderDao.save(order);

    log.info("Actuator: savedOrder: " + savedOrder);

    return savedOrder;
  }

  @PutMapping("/update")
  public Order updateOrder(@RequestBody Order order) {
    log.info("Actuator: OrderController.updateOrder()");
    log.info("Actuator: order: " + order);

    Order savedOrder = orderDao.save(order);

    log.info("Actuator: savedOrder: " + savedOrder);

    return savedOrder;
  }

  @DeleteMapping("/delteById/{id}")
  public void deleteOrder(@PathVariable int id) {
    log.info("Actuator: OrderController.deleteOrder()");
    log.info("Actuator: id: " + id);

    orderDao.deleteById(id);
  }

  @Override
  public Health health() {
    log.info("Actuator: OrderController.health()");
    log.info("Actuator: orderLast: " + applicationPropertiesConfiguration.getLastOrder());

    List<Order> orders = orderDao.findAll();
    if (orders.isEmpty()) {
      log.warn("Actuator: OrderController.health() - DOWN");
      return Health.down().withDetail("count", 0).build();
    }

    log.info("Actuator: OrderController.health() - UP");
    return Health.up().withDetail("count", orders.size()).build();
  }


}
