# 🏥 Hospital Queue System

A complete hospital queue management system with both backend (Java) and frontend (React) that prioritizes patients based on urgency level and age. Features real-time updates, modern UI, and robust data persistence.

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

### Core Features
- **Priority-based Queue Management**: Patients are ordered by priority level (LOW, MEDIUM, HIGH, EXTREME)
- **Elderly Priority Bonus**: Patients aged 60+ receive additional priority
- **ULID Identifiers**: Uses ULID (Universally Unique Lexicographically Sortable Identifier) for patient IDs
- **Input Validation**: Validation for names (letters only) and age (0-130)
- **Queue History**: Tracks all queue actions (ADDED, CALLED, REMOVED)

### Frontend Features
- **Real-time Updates**: Queue automatically updates across all connected clients
- **Modern Web UI**: Professional, responsive interface with smooth animations
- **Modal Notifications**: Visual feedback when calling patients
- **Mobile-Friendly**: Works seamlessly on desktop and mobile devices

### Backend Options
- **Console Interface**: User-friendly command-line interface (Java)
- **Supabase Backend**: Cloud database with real-time capabilities (Frontend)
- **PostgreSQL Persistence**: Robust data storage with Docker support

## 🛠 Technologies

### Backend (Java Console Application)
- **Java 17**
- **Maven** - Dependency management
- **PostgreSQL 16** - Database
- **Docker & Docker Compose** - Container orchestration
- **Lombok** - Boilerplate code reduction
- **Log4j2** - Logging
- **JDBC** - Database connectivity
- **ULID Creator** - Unique identifier generation

### Frontend (Web Application)
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Supabase** - Backend database and real-time subscriptions
- **CSS3** - Modern styling with animations

## 📦 Prerequisites

### For Java Console Application
- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose

### For Web Frontend
- [Node.js 18+](https://nodejs.org/)
- npm or yarn

## 🚀 Getting Started

You can run either the Java console application or the web frontend (or both).

### Option 1: Java Console Application

#### 1. Start the PostgreSQL database

```bash
docker-compose up -d
```

This will start a PostgreSQL 16 container on port **5433** with the schema automatically created.

#### 2. Build and run

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.rafaelfavetta.queuesystem.Main"
```

Or run directly from your IDE by executing the `Main.java` class.

### Option 2: Web Frontend

#### 1. Navigate to the frontend directory

```bash
cd frontend
```

#### 2. Install dependencies

```bash
npm install
```

#### 3. Run the development server

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

The frontend uses Supabase as the database backend, which is already configured.

## 📁 Project Structure

```
hospital-queue-system/
├── src/                                  # Java backend source
│   ├── main/java/com/rafaelfavetta/queuesystem/
│   │   ├── Main.java                    # Console app entry point
│   │   ├── domain/                      # Domain entities
│   │   ├── repository/                  # Data access layer
│   │   ├── service/                     # Business logic
│   │   └── ui/                          # Console interface
│   └── main/resources/
│       ├── database.properties          # Database config
│       └── db/init.sql                  # Database schema
├── frontend/                            # React web application
│   ├── src/
│   │   ├── components/                  # React components
│   │   │   ├── AddPatientForm.tsx
│   │   │   ├── QueueList.tsx
│   │   │   └── CalledPatientModal.tsx
│   │   ├── services/
│   │   │   └── queueService.ts          # API calls and real-time
│   │   ├── lib/
│   │   │   └── supabase.ts              # Supabase client
│   │   ├── App.tsx                      # Main component
│   │   └── main.tsx                     # Entry point
│   ├── .env                             # Supabase configuration
│   └── package.json
├── docker-compose.yml                   # PostgreSQL container
├── pom.xml                              # Maven config
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

This project was made for the purpose of learning and is open source and available

---
