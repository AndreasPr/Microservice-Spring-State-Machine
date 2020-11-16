package andreasgroup.microservicespringstatemachine.domain;

/**
 * Created on 16/Nov/2020 to microservice-spring-state-machine
 */
public enum PaymentEvent {

    PRE_AUTHORIZE, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTHORIZE, AUTH_APPROVED, AUTH_DECLINED
}
