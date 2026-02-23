# 🏥 Hospital Queue System

A hospital queue management system that prioritizes patients based on urgency level and age. Built with Java, Lombok, PostgreSQL and Docker.

## 📋 Table of Contents

- [Features](#-features)
- [Technologies](#-technologies)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [How It Works](#-how-it-works)
- [Database Schema](#-database-schema)
- [Usage](#-usage)
- [Configuration](#-configuration)

## ✨ Features

- **Priority-based Queue Management**: Patients are ordered by priority level (LOW, MEDIUM, HIGH, EXTREME)
- **Elderly Priority Bonus**: Patients aged 60+ receive additional priority
- **ULID Identifiers**: Uses ULID (Universally Unique Lexicographically Sortable Identifier) for patient IDs
- **Input Validation**: Validation for names (letters only) and age (0-130)
- **Queue History**: Tracks all queue actions (ADDED, CALLED, REMOVED)
- **Console Interface**: User-friendly command-line interface
- **PostgreSQL Persistence**: Data is persisted in a PostgreSQL database

## 🛠 Technologies

- **Java 17**
- **Maven** - Dependency management
- **PostgreSQL 16** - Database
- **Docker & Docker Compose** - Container orchestration
- **Lombok** - Boilerplate code reduction
- **Log4j2** - Logging
- **JDBC** - Database connectivity
- **ULID Creator** - Unique identifier generation

## 📦 Prerequisites

Before running this project, make sure you have installed:

- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (or any JDK 17+ distribution)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/hospital-queue-system.git
cd hospital-queue-system
```

### 2. Start the PostgreSQL database

```bash
docker-compose up -d
```

This will:
- Start a PostgreSQL 16 container on port **5433**
- Automatically create the database schema using `init.sql`

### 3. Build the project

```bash
mvn clean compile
```

### 4. Run the application

```bash
mvn exec:java -Dexec.mainClass="com.rafaelfavetta.queuesystem.Main"
```

Or run directly from your IDE by executing the `Main.java` class.

## 📁 Project Structure

```
hospital-queue-system/
├── src/
│   ├── main/
│   │   ├── java/com/rafaelfavetta/queuesystem/
│   │   │   ├── Main.java                 # Application entry point
│   │   │   ├── domain/                   # Domain entities
│   │   │   │   ├── Patient.java          # Patient entity
│   │   │   │   ├── PriorityLevel.java    # Priority enum (LOW, MEDIUM, HIGH, EXTREME)
│   │   │   │   └── valueObjects/         # Value objects
│   │   │   │       ├── Age.java          # Age validation (0-130)
│   │   │   │       ├── Name.java         # Name validation (letters only)
│   │   │   │       └── Ulid.java         # ULID wrapper
│   │   │   ├── repository/               # Data access layer
│   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── PatientRepository.java
│   │   │   │   └── QueueRepository.java
│   │   │   ├── service/                  # Business logic
│   │   │   │   └── QueueService.java
│   │   │   └── ui/                       # User interface
│   │   │       └── ConsoleUI.java
│   │   └── resources/
│   │       ├── database.properties       # Database configuration
│   │       ├── log4j2.xml               # Logging configuration
│   │       └── db/
│   │           └── init.sql             # Database schema
│   └── test/
├── docker-compose.yml
├── pom.xml
└── README.md
```

## ⚙️ How It Works

### Priority Calculation

The system calculates patient priority using the following formula:

```
Priority Score = (Priority Level × 10) + Elderly Bonus
```

| Priority Level | Base Score | With Elderly Bonus (60+) |
|---------------|------------|--------------------------|
| LOW (1)       | 10         | 15                       |
| MEDIUM (2)    | 20         | 25                       |
| HIGH (3)      | 30         | 35                       |
| EXTREME (4)   | 40         | 45                       |

Patients with higher scores are called first. If two patients have the same score, the one who arrived first is called.

### Queue Operations

1. **Add Patient**: Register a new patient with name, age, and priority level
2. **Call Next Patient**: Retrieves and removes the highest-priority patient from the queue
3. **Show Queue**: Displays all patients currently waiting, sorted by priority
4. **Exit**: Exit the application

## 🗄 Database Schema

The system uses 4 tables:

- **priority_levels**: Stores the 4 priority levels
- **patients**: Stores patient information (ULID, name, age, priority)
- **queue**: Tracks patients currently in the queue
- **queue_history**: Audit log of all queue actions

```
┌─────────────────┐     ┌─────────────────┐
│ priority_levels │     │    patients     │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ priority_level_id│
│ name            │     │ id (PK - ULID)  │
│ level           │     │ name            │
└─────────────────┘     │ age             │
                        │ arrival_order   │
                        └────────┬────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
        ▼                        ▼                        │
┌───────────────┐       ┌─────────────────┐              │
│     queue     │       │  queue_history  │              │
├───────────────┤       ├─────────────────┤              │
│ id (PK)       │       │ id (PK)         │              │
│ patient_id(FK)│───────│ patient_id (FK) │──────────────┘
│ priority_score│       │ action          │
│ arrival_order │       │ action_timestamp│
└───────────────┘       └─────────────────┘
```

## 💻 Usage

When you run the application, you'll see the main menu:

```
=== HOSPITAL QUEUE SYSTEM ===

1 - Add patient
2 - Call next patient
3 - Show current queue
4 - Exit
Choice:
```

### Adding a Patient

```
Choice: 1
Name: João Silva
Age: 65
Priority (1-LOW, 2-MEDIUM, 3-HIGH, 4-EXTREME)
Choice: 3
Patient added: João Silva | Priority: HIGH | Age: 65
```

### Calling Next Patient

```
Choice: 2
Next patient: João Silva | Priority: HIGH | Age: 65
```

### Showing the Queue

```
Choice: 3
Current queue:
1. Maria Santos | Priority: EXTREME | Age: 70
2. Pedro Oliveira | Priority: HIGH | Age: 45
3. Ana Costa | Priority: MEDIUM | Age: 30
```

## ⚙️ Configuration

### Database Configuration

Edit `src/main/resources/database.properties`:

```properties
db.url=jdbc:postgresql://localhost:5433/postgres
db.user=queue
db.password=system
```

### Docker Configuration

Edit `docker-compose.yml` to change database settings:

```yaml
environment:
  POSTGRES_DB: queue_system
  POSTGRES_USER: queue
  POSTGRES_PASSWORD: system
ports:
  - "5433:5432"  # Change host port if needed
```

## 🛑 Stopping the Application

To stop the PostgreSQL container:

```bash
docker-compose down
```

To stop and remove all data:

```bash
docker-compose down -v
```

## 📝 License

This project was made for learning and is open source and available under the [MIT License](LICENSE).

---