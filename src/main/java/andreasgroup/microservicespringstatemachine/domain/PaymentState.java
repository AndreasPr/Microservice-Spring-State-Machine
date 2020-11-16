package andreasgroup.microservicespringstatemachine.domain;

/**
 * Created on 16/Nov/2020 to microservice-spring-state-machine
 */
public enum PaymentState {

    NEW, PRE_AUTH, PRE_AUTH_ERROR, AUTH, AUTH_ERROR
}
