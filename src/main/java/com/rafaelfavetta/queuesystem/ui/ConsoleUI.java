package com.rafaelfavetta.queuesystem.ui;

import com.rafaelfavetta.queuesystem.domain.Patient;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.service.QueueService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Log4j2
public class ConsoleUI {

    private final QueueService queueService;
    private final BufferedReader br;
    private long patientCounter = 1;

    public ConsoleUI() {
        this.queueService = new QueueService();
        this.br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    public ConsoleUI(QueueService queueService, BufferedReader br) {
        this.queueService = queueService;
        this.br = br;
    }

    public void start() {
        int option;

        do {
            showOptions();
            option = readInt().age();

            switch (option) {
                case 1 -> addPatient();
                case 2 -> callNextPatient();
                case 3 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 3);
    }

    private void showOptions() {
        System.out.println("\n=== HOSPITAL QUEUE SYSTEM ===\n");
        System.out.println("1 - Add patient");
        System.out.println("2 - Call next patient");
        System.out.println("3 - Exit");
        System.out.print("Choice: ");
    }

    private void addPatient() {

        System.out.println("Name: ");
        Name name;
        try {
            name = new Name(br.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Age: ");
        Age age = readInt();

        System.out.println("Priority (1-LOW, 2-MEDIUM, 3-HIGH, 4-EXTREME)");
        System.out.println("Choice: ");
        int priorityChoice = readInt().age();

        Patient patient = Patient.builder()
                .name(new Name(name.name()))
                .age(age)
                .priorityLevel(switch (priorityChoice) {
                    case 1 -> com.rafaelfavetta.queuesystem.domain.PriorityLevel.LOW;
                    case 2 -> com.rafaelfavetta.queuesystem.domain.PriorityLevel.MEDIUM;
                    case 3 -> com.rafaelfavetta.queuesystem.domain.PriorityLevel.HIGH;
                    case 4 -> com.rafaelfavetta.queuesystem.domain.PriorityLevel.EXTREME;
                    default -> throw new IllegalArgumentException("Invalid priority level.");
                })
                .build();

        queueService.addPatient(patient);

        System.out.println("Patient added: " + patient.getName().name() + " | Priority: " + patient.getPriorityLevel() + " | Age: " + patient.getAge().age());
    }

    private Age readInt() {
        try {
            return new Age(Integer.parseInt(br.readLine()));
        } catch (IOException e) {
            throw new RuntimeException("Invalid input, please type a valid number.", e);
        }
    }

    private void callNextPatient() {
        if (queueService.isEmpty()) {
            System.out.println("No patients in the queue.");
            return;
        }

        Patient nextPatient = queueService.callNextPatient();
        System.out.println("Next patient: " + nextPatient.getName().name() + " | Priority: " + nextPatient.getPriorityLevel() + " | Age: " + nextPatient.getAge().age());
    }
}
