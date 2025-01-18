package github.mahmoudesse.microservicecommandes.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("my-config-ms")
@RefreshScope
public class ApplicationPropertiesConfiguration {
  private int lastOrder;

  public int getLastOrder() {
    return lastOrder;
  }

  public void setLastOrder(int lastOrder) {
    this.lastOrder = lastOrder;
  }
}
