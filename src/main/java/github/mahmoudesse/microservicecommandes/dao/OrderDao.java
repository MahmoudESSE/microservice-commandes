package github.mahmoudesse.microservicecommandes.dao;

import github.mahmoudesse.microservicecommandes.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {

  List<Order> findByCreatedDateAfter(LocalDateTime createdDateBefore);
}

