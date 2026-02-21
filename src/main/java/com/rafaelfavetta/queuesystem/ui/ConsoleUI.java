package com.rafaelfavetta.queuesystem.ui;

import com.rafaelfavetta.queuesystem.domain.Patient;
import com.rafaelfavetta.queuesystem.domain.PriorityLevel;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.service.QueueService;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Log4j2
public class ConsoleUI {

    private static final int EXIT_OPTION = 4;

    private final QueueService queueService;
    private final BufferedReader br;

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
            option = readInt();

            switch (option) {
                case 1 -> addPatient();
                case 2 -> callNextPatient();
                case 3 -> showQueue();
                case 4 -> log.info("Exiting...");
                default -> log.warn("Invalid option, please try again.");
            }
        } while (option != EXIT_OPTION);
    }

    private void showOptions() {
        System.out.println("\n=== HOSPITAL QUEUE SYSTEM ===\n");
        System.out.println("1 - Add patient");
        System.out.println("2 - Call next patient");
        System.out.println("3 - Show current queue");
        System.out.println("4 - Exit");
        System.out.print("Choice: ");
    }

    private void addPatient() {
        System.out.print("Name: ");
        Name name = readName();

        System.out.print("Age: ");
        Age age = readAge();

        System.out.println("Priority (1-LOW, 2-MEDIUM, 3-HIGH, 4-EXTREME)");
        System.out.print("Choice: ");
        PriorityLevel priorityLevel = readPriority();

        Patient patient = Patient.builder()
                .name(name)
                .age(age)
                .priorityLevel(priorityLevel)
                .build();

        queueService.addPatient(patient);

        log.info("Patient added: {} | Priority: {} | Age: {}",
                patient.getName().name(), patient.getPriorityLevel(), patient.getAge().age());
    }

    private PriorityLevel readPriority() {
        while (true) {
            int choice = readInt();
            switch (choice) {
                case 1 -> { return PriorityLevel.LOW; }
                case 2 -> { return PriorityLevel.MEDIUM; }
                case 3 -> { return PriorityLevel.HIGH; }
                case 4 -> { return PriorityLevel.EXTREME; }
                default -> {
                    log.warn("Invalid priority level. Choose between 1 and 4.");
                    System.out.print("Choice: ");
                }
            }
        }
    }

    private Name readName() {
        while (true) {
            try {
                return new Name(br.readLine());
            } catch (IOException e) {
                throw new RuntimeException("Error reading name.", e);
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage());
                System.out.print("Name: ");
            }
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(br.readLine());
            } catch (IOException e) {
                throw new RuntimeException("Error reading input.", e);
            } catch (NumberFormatException e) {
                log.warn("Invalid input, please type a valid number.");
                System.out.print("Try again: ");
            }
        }
    }

    private Age readAge() {
        while (true) {
            try {
                return new Age(readInt());
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage());
                System.out.print("Age: ");
            }
        }
    }

    private void showQueue() {
        if (queueService.isEmpty()) {
            log.info("No patients in the queue.");
            return;
        }
        queueService.showQueue();
    }

    private void callNextPatient() {
        if (queueService.isEmpty()) {
            log.info("No patients in the queue.");
            return;
        }

        Patient nextPatient = queueService.callNextPatient();
        log.info("Next patient: {} | Priority: {} | Age: {}",
                nextPatient.getName().name(), nextPatient.getPriorityLevel(), nextPatient.getAge().age());
    }
}
