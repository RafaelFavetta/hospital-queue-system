package com.rafaelfavetta.queuesystem;

import com.rafaelfavetta.queuesystem.domain.Patient;
import com.rafaelfavetta.queuesystem.domain.PriorityLevel;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.service.QueueService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {

        QueueService queueService = new QueueService();

        Patient p1 = Patient.builder()
                .name(new Name("Rafael"))
                .age(new Age(19))
                .priorityLevel(PriorityLevel.LOW)
                .arrivalOrder(1)
                .build();

        Patient p2 = Patient.builder()
                .name(new Name("Maria"))
                .age(new Age(65))
                .priorityLevel(PriorityLevel.MEDIUM)
                .arrivalOrder(2)
                .build();

        Patient p3 = Patient.builder()
                .name(new Name("João"))
                .age(new Age(45))
                .priorityLevel(PriorityLevel.HIGH)
                .arrivalOrder(3)
                .build();

        Patient p4 = Patient.builder()
                .name(new Name("Ana"))
                .age(new Age(70))
                .priorityLevel(PriorityLevel.EXTREME)
                .arrivalOrder(4)
                .build();

        queueService.addPatient(p1);
        queueService.addPatient(p2);
        queueService.addPatient(p3);
        queueService.addPatient(p4);


        log.info("Current Queue:");
        queueService.showQueue();

        log.info("\nNext patients:");
        while (!queueService.isEmpty()){
            Patient nextPatient = queueService.callNextPatient();
            log.info(nextPatient.getName().name() + " | Priority: " + nextPatient.getPriorityLevel() + " | Age: " + nextPatient.getAge().age());
        }
    }
}