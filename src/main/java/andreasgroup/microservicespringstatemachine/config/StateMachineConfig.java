package andreasgroup.microservicespringstatemachine.config;

import andreasgroup.microservicespringstatemachine.domain.PaymentEvent;
import andreasgroup.microservicespringstatemachine.domain.PaymentState;
import andreasgroup.microservicespringstatemachine.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

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

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(PaymentState.NEW)
                    .target(PaymentState.NEW)
                    .event(PaymentEvent.PRE_AUTHORIZE)
                    .action(preAuthAction())
                .and()
                .withExternal().source(PaymentState.NEW)
                    .target(PaymentState.PRE_AUTH)
                    .event(PaymentEvent.PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentState.NEW)
                    .target(PaymentState.PRE_AUTH_ERROR)
                    .event(PaymentEvent.PRE_AUTH_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("stateChanged(from %s, to: %s)", from, to));
            }
        };

        config.withConfiguration().listener(adapter);
    }

    public Action<PaymentState, PaymentEvent> preAuthAction(){

        //High versatility because in actions we could send a message to a different system, call some type of web service
        // right out to the database. Just implement the business logic that we need.
        
        // Here we simulate the authorization service either authorizing the transaction or declining the transaction.
        return context -> {
            System.out.println("PreAuth is called");
            // 80% of the times is AUTHORIZED
            // 20% of the times get DECLINED
            if(new Random().nextInt(10)<8){
                System.out.println("Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());
            }//if
            else {
                System.out.println("Declined! No credit card");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                        .build());
            }//else
        };//context
    }




}
