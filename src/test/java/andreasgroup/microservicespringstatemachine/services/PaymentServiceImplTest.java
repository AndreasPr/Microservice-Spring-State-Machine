package andreasgroup.microservicespringstatemachine.services;

import andreasgroup.microservicespringstatemachine.domain.Payment;
import andreasgroup.microservicespringstatemachine.domain.PaymentEvent;
import andreasgroup.microservicespringstatemachine.domain.PaymentState;
import andreasgroup.microservicespringstatemachine.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 17/Nov/2020 to microservice-spring-state-machine
 */
@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp(){
        payment = Payment.builder().amount(new BigDecimal("12.67")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println("Should be NEW");
        System.out.println(savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println(sm.getState().getId());
        System.out.println(preAuthedPayment);
    }

    @Transactional
    @RepeatedTest(10)
    void testAuth(){
        Payment savedPayment = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(savedPayment.getId());

        if(preAuthSM.getState().getId() == PaymentState.PRE_AUTH){
            System.out.println("Payment is Pre Authorized");

            StateMachine<PaymentState, PaymentEvent> authSM = paymentService.authorizePayment(savedPayment.getId());
            System.out.println("Result of Auth: " + authSM.getState().getId());
        }
        else{
            System.out.println("Payment failed pre-auth...");
        }
    }


}
