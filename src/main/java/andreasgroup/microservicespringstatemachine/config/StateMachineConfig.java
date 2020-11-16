package andreasgroup.microservicespringstatemachine.config;

import andreasgroup.microservicespringstatemachine.domain.PaymentEvent;
import andreasgroup.microservicespringstatemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import java.util.EnumSet;

/**
 * Created on 16/Nov/2020 to microservice-spring-state-machine
 */
@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))//Get a list of Enumerations from PaymentState
                .end(PaymentState.AUTH) //termination state
                .end(PaymentState.PRE_AUTH_ERROR) //termination state
                .end(PaymentState.AUTH_ERROR); //termination state
    }
}