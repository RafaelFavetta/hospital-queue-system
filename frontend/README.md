# Hospital Queue System - Frontend

A modern, real-time web application for managing hospital patient queues with priority-based ordering.

## Features

- **Real-time Updates**: Queue automatically updates across all connected clients using Supabase real-time subscriptions
- **Priority-based Sorting**: Patients are automatically ordered by priority level and arrival time
- **Elderly Priority Bonus**: Patients aged 60+ receive additional priority scoring
- **Input Validation**: Client-side validation for names and ages
- **Responsive Design**: Works seamlessly on desktop and mobile devices
- **Modern UI**: Clean, professional interface with smooth animations and transitions

## Tech Stack

- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Supabase** - Backend database and real-time subscriptions
- **CSS3** - Modern styling with gradients, animations, and responsive design

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

1. Install dependencies:
```bash
npm install
```

2. The Supabase configuration is already set up in the `.env` file.

### Development

Run the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Build

Build for production:
```bash
npm run build
```

The built files will be in the `dist` directory.

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── AddPatientForm.tsx    # Form to add new patients
│   │   ├── QueueList.tsx         # Display current queue
│   │   └── CalledPatientModal.tsx # Modal for called patient
│   ├── services/
│   │   └── queueService.ts       # Queue operations and real-time subscriptions
│   ├── lib/
│   │   └── supabase.ts          # Supabase client and types
│   ├── App.tsx                   # Main app component
│   ├── App.css                   # App-specific styles
│   ├── index.css                 # Global styles
│   └── main.tsx                  # Entry point
├── .env                          # Environment variables
└── package.json
```

## How It Works

### Priority Calculation

The system calculates patient priority using:

```
Priority Score = (Priority Level × 10) + Elderly Bonus
```

- **Priority Levels**:
  - LOW: 10 points
  - MEDIUM: 20 points
  - HIGH: 30 points
  - EXTREME: 40 points
- **Elderly Bonus**: +5 points for patients aged 60 or older
- **Tie-breaking**: Earlier arrival time takes precedence when scores are equal

### Real-time Updates

The application uses Supabase's real-time features to automatically update the queue when:
- A new patient is added
- A patient is called
- Any queue changes occur

All connected clients see updates instantly without refreshing.

### Data Flow

1. User adds a patient via the form
2. `queueService.addPatient()` creates a patient record and queue entry
3. Supabase triggers real-time event
4. All clients receive the update and refresh their queue display
5. When calling next patient, highest priority patient is removed and shown in modal

## Components

### AddPatientForm

Handles patient registration with:
- Name validation (letters and spaces only)
- Age validation (0-130)
- Priority level selection with visual buttons
- Error handling and loading states

### QueueList

Displays the current queue with:
- Priority badges with color coding
- Elderly priority indicators
- Priority scores
- Call next patient button
- Empty state when no patients

### CalledPatientModal

Shows the called patient information:
- Patient name and details
- Auto-dismisses after 5 seconds
- Click to dismiss manually

## Database Schema

The application uses these Supabase tables:

- **priority_levels**: Reference data for priority levels
- **patients**: Patient information
- **queue**: Active queue entries with calculated priority scores
- **queue_history**: Audit log of all queue actions

All tables have Row Level Security (RLS) enabled for data protection.

## Environment Variables

```
VITE_SUPABASE_URL=your_supabase_url
VITE_SUPABASE_SUPABASE_ANON_KEY=your_supabase_anon_key
```

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+

## License

This project is open source and available for educational purposes.
