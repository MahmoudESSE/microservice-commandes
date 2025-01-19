package github.mahmoudesse.microservicecommandes.web.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import github.mahmoudesse.microservicecommandes.config.ApplicationPropertiesConfiguration;
import github.mahmoudesse.microservicecommandes.dao.OrderDao;
import github.mahmoudesse.microservicecommandes.model.Order;
import github.mahmoudesse.microservicecommandes.web.exceptions.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableCircuitBreaker
@EnableHystrixDashboard
@Configuration
@RequestMapping("orders")
public class OrderController implements HealthIndicator {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  OrderDao orderDao;

  @Autowired
  ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

  @GetMapping(value = "/getAll")
  @HystrixCommand(fallbackMethod = "getOrdersFallback", commandProperties = {
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
  }, threadPoolKey = "orderThreadPool")
  public List<Order> getOrders() throws InterruptedException {
    log.info("Actuator: OrderController.getOrders()");

//    Thread.sleep(4000);
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

  public List<Order> getOrdersFallback() {
    log.warn("Actuator: OrderController.getOrdersFallback()");
    log.warn("Actuator: OrderController.getOrdersFallback() - Returning empty list");

    return new ArrayList<>();
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
