# Microservice-Spring-State-Machine

## Table of Contents

* [General info](#general-info)
* [UML Diagram](#uml-diagram)
* [Technologies](#technologies)
* [Scope of functionalities](#scope-of-functionalities)
* [States](#states)
* [Events](#events)
* [Project Status](#project-status)

## General Info
The project implements a credit card processing using the features of the Spring State Machine. 

## UML Diagram
![photo1](https://github.com/AndreasPr/Microservice-Spring-State-Machine/blob/master/payment%20state%20machine.PNG)

## Technologies
* Spring Boot
* Spring Data JPA
* Spring Web
* Spring State Machine
* H2 Database
* Project Lombok
* JUnit5

## Scope of functionalities
* State Configuration.
* Transition Configuration.
* Store machine config in a persistent storage.
* Payment Service.
* Usage of guards and actions.
* Sending Events to the State Machine.
* State Change Interceptor.

## States

| States | Description |
| ------ | ------ |
| New | New Payment |
| Pre Authorized | Charge Pre Authorized with processor |
| Pre Authorization Error | Pre Authorization rejected by processor |
| Authorized | Charge approved by processor |
| Authorization Error | Charge rejected by processor |

## Events

| Events | Description |
| ------ | ------ |
| Pre Authorize Charge | Call processor for pre auth transaction |
| Pre Authorize Approved | Processor approved pre authorize |
| Pre Authorize Declined | Processor declined pre authorize |
| Authorize Charge | Call processor for charge authorization |
| Authorization Approved | Processor approved charge |
| Authorization Declined | Processor rejected charge |

## Project Status
The project is still being developed.
