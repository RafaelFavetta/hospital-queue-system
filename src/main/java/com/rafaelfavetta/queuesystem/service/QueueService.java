package com.rafaelfavetta.queuesystem.service;

import com.rafaelfavetta.queuesystem.domain.Patient;
import com.rafaelfavetta.queuesystem.repository.PatientRepository;

import java.util.List;
import java.util.Optional;

public class QueueService {

    private final PatientRepository patientRepository;

    public QueueService() {
        this.patientRepository = new PatientRepository();
    }

    public QueueService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void addPatient(Patient patient) {
        patientRepository.addPatient(patient);
    }

    public Patient callNextPatient() {
        Optional<Patient> patient = patientRepository.callNextPatient();
        return patient.orElse(null);
    }

    public boolean isEmpty() {
        return patientRepository.isQueueEmpty();
    }

    public List<Patient> getSnapshotQueue() {
        return patientRepository.getAllInQueue();
    }

    public int getQueueSize() {
        return patientRepository.getQueueSize();
    }
}

