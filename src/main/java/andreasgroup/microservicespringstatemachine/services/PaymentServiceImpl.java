package andreasgroup.microservicespringstatemachine.services;

import andreasgroup.microservicespringstatemachine.domain.Payment;
import andreasgroup.microservicespringstatemachine.domain.PaymentEvent;
import andreasgroup.microservicespringstatemachine.domain.PaymentState;
import andreasgroup.microservicespringstatemachine.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 16/Nov/2020 to microservice-spring-state-machine
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;
    public static final String PAYMENT_ID_HEADER = "payment_id";

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        //We are going to get the state machine, we are going to restore that from the database then we are going to
        // send an event into that state machine. We are saying that with the PREAUTH method it is going to send an payment
        // event of pre-authorized to the state machine.

        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {

        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {

        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId){
        //We are going out to the database, getting a payment, then we are asking the state machine factory for an instance
        // of the state machine. We stopped it, we use an accessor to go in and set it to the state that is from the database
        // or a payment from the database. We restart the state machine and then we return it. This is a common method for
        // every method that we are going to use for each one of our methods that is going to work with state machine.

        Payment payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma->{
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
                });
        sm.start();

        return sm;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event){
        Message msg = MessageBuilder
                .withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        //Enrich the message to have the paymentId in order to know that the StateMachine to react in the event.
        sm.sendEvent(msg);
    }



}
