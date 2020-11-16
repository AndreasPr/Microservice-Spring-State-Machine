package andreasgroup.microservicespringstatemachine.repositories;

import andreasgroup.microservicespringstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 16/Nov/2020 to microservice-spring-state-machine
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
