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
public class QueueRepository {

    private static final String INSERT_INTO_QUEUE = """
            INSERT INTO queue (patient_id, priority_score, arrival_order)
            VALUES (?, ?, ?)
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

    private static final String INSERT_HISTORY = """
            INSERT INTO queue_history (patient_id, action)
            VALUES (?, ?)
            """;

    public void addToQueue(Patient patient, int priorityScore) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_INTO_QUEUE)) {
            stmt.setString(1, patient.getId().getUlid());
            stmt.setInt(2, priorityScore);
            stmt.setLong(3, patient.getArrivalOrder());
            stmt.executeUpdate();
            log.debug("Patient added to queue: {}", patient.getId().getUlid());
        } catch (SQLException e) {
            log.error("Error adding patient to queue: {}", e.getMessage());
            throw new RuntimeException("Error adding patient to queue", e);
        }
    }

    public Optional<Patient> getNextPatient() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_NEXT_PATIENT);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return Optional.of(mapResultSetToPatient(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error fetching next patient: {}", e.getMessage());
            throw new RuntimeException("Error fetching next patient from queue", e);
        }
    }

    public void removeFromQueue(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_FROM_QUEUE)) {
            stmt.setString(1, patientId);
            stmt.executeUpdate();
            log.debug("Patient removed from queue: {}", patientId);
        } catch (SQLException e) {
            log.error("Error removing patient from queue: {}", e.getMessage());
            throw new RuntimeException("Error removing patient from queue", e);
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

    public boolean isEmpty() {
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

    public void logAction(String patientId, String action) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY)) {
            stmt.setString(1, patientId);
            stmt.setString(2, action);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error logging action: {}", e.getMessage());
            throw new RuntimeException("Error logging action to database", e);
        }
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


