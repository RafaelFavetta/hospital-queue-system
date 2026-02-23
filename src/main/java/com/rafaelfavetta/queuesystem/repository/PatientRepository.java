package com.rafaelfavetta.queuesystem.repository;

import com.rafaelfavetta.queuesystem.domain.Patient;
import com.rafaelfavetta.queuesystem.domain.PriorityLevel;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Ulid;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class PatientRepository {

    private static final String INSERT_PATIENT = """
            INSERT INTO patients (id, name, age, priority_level_id, arrival_order)
            VALUES (?, ?, ?, (SELECT id FROM priority_levels WHERE name = ?), ?)
            """;

    private static final String INSERT_INTO_QUEUE = """
            INSERT INTO queue (patient_id, priority_score, arrival_order)
            VALUES (?, ?, ?)
            """;

    private static final String INSERT_HISTORY = """
            INSERT INTO queue_history (patient_id, action)
            VALUES (?, ?)
            """;

    private static final String SELECT_NEXT_PATIENT = """
            SELECT p.id, p.name, p.age, pl.name as priority_level, p.arrival_order
            FROM queue q
            JOIN patients p ON q.patient_id = p.id
            JOIN priority_levels pl ON p.priority_level_id = pl.id
            ORDER BY q.priority_score DESC, q.arrival_order ASC
            LIMIT 1
            """;

    private static final String DELETE_FROM_QUEUE = """
            DELETE FROM queue WHERE patient_id = ?
            """;

    private static final String SELECT_ALL_IN_QUEUE = """
            SELECT p.id, p.name, p.age, pl.name as priority_level, p.arrival_order
            FROM queue q
            JOIN patients p ON q.patient_id = p.id
            JOIN priority_levels pl ON p.priority_level_id = pl.id
            ORDER BY q.priority_score DESC, q.arrival_order ASC
            """;

    private static final String COUNT_QUEUE = """
            SELECT COUNT(*) FROM queue
            """;

    private static final String SELECT_MAX_ARRIVAL_ORDER = """
            SELECT COALESCE(MAX(arrival_order), 0) FROM patients
            """;

    private static final String SELECT_PATIENT_BY_ID = """
            SELECT p.id, p.name, p.age, pl.name as priority_level, p.arrival_order
            FROM patients p
            JOIN priority_levels pl ON p.priority_level_id = pl.id
            WHERE p.id = ?
            """;

    public void addPatient(Patient patient) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            long arrivalOrder = getNextArrivalOrder(conn);
            patient.setArrivalOrder(arrivalOrder);

            // Insert patient
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_PATIENT)) {
                stmt.setString(1, patient.getId().getUlid());
                stmt.setString(2, patient.getName().name());
                stmt.setInt(3, patient.getAge().age());
                stmt.setString(4, patient.getPriorityLevel().name());
                stmt.setLong(5, arrivalOrder);
                stmt.executeUpdate();
            }

            // Insert into queue
            int priorityScore = calculatePriorityScore(patient);
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_INTO_QUEUE)) {
                stmt.setString(1, patient.getId().getUlid());
                stmt.setInt(2, priorityScore);
                stmt.setLong(3, arrivalOrder);
                stmt.executeUpdate();
            }

            // Insert history
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY)) {
                stmt.setString(1, patient.getId().getUlid());
                stmt.setString(2, "ADDED");
                stmt.executeUpdate();
            }

            conn.commit();
            log.info("Patient added to database: {}", patient.getName().name());

        } catch (SQLException e) {
            log.error("Error adding patient: {}", e.getMessage());
            throw new RuntimeException("Error adding patient to database", e);
        }
    }

    public Optional<Patient> callNextPatient() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Patient patient = null;

            // Get next patient
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_NEXT_PATIENT);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }

            if (patient == null) {
                conn.commit();
                return Optional.empty();
            }

            // Remove from queue
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_FROM_QUEUE)) {
                stmt.setString(1, patient.getId().getUlid());
                stmt.executeUpdate();
            }

            // Insert history
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY)) {
                stmt.setString(1, patient.getId().getUlid());
                stmt.setString(2, "CALLED");
                stmt.executeUpdate();
            }

            conn.commit();
            log.info("Patient called from database: {}", patient.getName().name());
            return Optional.of(patient);

        } catch (SQLException e) {
            log.error("Error calling next patient: {}", e.getMessage());
            throw new RuntimeException("Error calling next patient from database", e);
        }
    }

    public List<Patient> getAllInQueue() {
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_IN_QUEUE);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }

        } catch (SQLException e) {
            log.error("Error fetching queue: {}", e.getMessage());
            throw new RuntimeException("Error fetching queue from database", e);
        }

        return patients;
    }

    public boolean isQueueEmpty() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_QUEUE);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;

        } catch (SQLException e) {
            log.error("Error checking queue: {}", e.getMessage());
            throw new RuntimeException("Error checking queue in database", e);
        }
    }

    public int getQueueSize() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_QUEUE);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            log.error("Error getting queue size: {}", e.getMessage());
            throw new RuntimeException("Error getting queue size from database", e);
        }
    }

    public Optional<Patient> findById(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PATIENT_BY_ID)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPatient(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding patient by id: {}", e.getMessage());
            throw new RuntimeException("Error finding patient in database", e);
        }
    }

    private long getNextArrivalOrder(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_MAX_ARRIVAL_ORDER);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
            return 1;
        }
    }

    private int calculatePriorityScore(Patient patient) {
        int priorityScore = patient.getPriorityLevel().getLevel() * 10;
        if (patient.isElderly()) {
            priorityScore += 5;
        }
        return priorityScore;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Ulid id = Ulid.of(rs.getString("id"));
        Name name = new Name(rs.getString("name"));
        Age age = new Age(rs.getInt("age"));
        PriorityLevel priorityLevel = PriorityLevel.valueOf(rs.getString("priority_level"));
        long arrivalOrder = rs.getLong("arrival_order");

        return Patient.builder()
                .id(id)
                .name(name)
                .age(age)
                .priorityLevel(priorityLevel)
                .arrivalOrder(arrivalOrder)
                .build();
    }
}