package github.mahmoudesse.microservicecommandes.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String description;
  private int quantity;
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  private float price;
  @Column(name = "product_id")
  private int productId;
}
