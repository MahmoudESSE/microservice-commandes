package github.mahmoudesse.microservicecommandes.web.controller;

import github.mahmoudesse.microservicecommandes.config.ApplicationPropertiesConfiguration;
import github.mahmoudesse.microservicecommandes.dao.OrderDao;
import github.mahmoudesse.microservicecommandes.model.Order;
import github.mahmoudesse.microservicecommandes.web.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController implements HealthIndicator {
  @Autowired
  OrderDao orderDao;

  @Autowired
  ApplicationPropertiesConfiguration applicationPropertiesConfiguration;

  @GetMapping(value = "/getAll")
  public List<Order> getOrders() {
    System.out.println("*** *** Actuator: OrderController.getOrders()");

    int lastOrderDate = applicationPropertiesConfiguration.getLastOrder();

    LocalDateTime startDateTime = LocalDateTime.now().minusDays(lastOrderDate);
    System.out.println("*** *** Actuator: startDateTime: " + startDateTime);

    List<Order> orders = orderDao.findByCreatedDateAfter(startDateTime);

    System.out.println("*** *** Actuator: Orders");
    orders.forEach(System.out::println);

    if (orders.isEmpty()) {
      throw new OrderNotFoundException("No orders found");
    }

    return orders;
  }

  @GetMapping(value = "/getById/{id}")
  public Order getOrder(@PathVariable int id) {
    System.out.println("*** *** Actuator: OrderController.getOrder()");
    System.out.println("*** *** Actuator: id: " + id);

    Order order = orderDao.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));

    System.out.println("*** *** Actuator: order: " + order);

    return order;
  }

  @PostMapping("/create")
  public Order createOrder(@RequestBody Order order) {

    System.out.println("*** *** Actuator: OrderController.createOrder()");
    System.out.println("*** *** Actuator: order: " + order);

    order.setCreatedDate(LocalDateTime.now());
    Order savedOrder = orderDao.save(order);

    System.out.println("*** *** Actuator: savedOrder: " + savedOrder);

    return savedOrder;
  }

  @PutMapping("/update")
  public Order updateOrder(@RequestBody Order order) {
    System.out.println("*** *** Actuator: OrderController.updateOrder()");
    System.out.println("*** *** Actuator: order: " + order);

    Order savedOrder = orderDao.save(order);

    System.out.println("*** *** Actuator: savedOrder: " + savedOrder);

    return savedOrder;
  }

  @DeleteMapping("/delteById/{id}")
  public void deleteOrder(@PathVariable int id) {
    System.out.println("*** *** Actuator: OrderController.deleteOrder()");
    System.out.println("*** *** Actuator: id: " + id);

    orderDao.deleteById(id);
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
