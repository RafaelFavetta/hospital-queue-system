import { useState, useEffect } from 'react';
import { AddPatientForm } from './components/AddPatientForm';
import { QueueList } from './components/QueueList';
import { CalledPatientModal } from './components/CalledPatientModal';
import { queueService } from './services/queueService';
import './App.css';

type QueueItem = {
  id: number;
  patient_id: string;
  priority_score: number;
  arrival_order: number;
  patients: {
    name: string;
    age: number;
    priority_levels: {
      name: string;
    };
  };
};

function App() {
  const [queue, setQueue] = useState<QueueItem[]>([]);
  const [calledPatient, setCalledPatient] = useState<QueueItem | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const loadQueue = async () => {
    try {
      const data = await queueService.getQueue();
      setQueue(data as QueueItem[]);
    } catch (error) {
      console.error('Error loading queue:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadQueue();
    const unsubscribe = queueService.subscribeToQueue(loadQueue);
    return () => unsubscribe();
  }, []);

  const handleCallNext = async () => {
    try {
      const patient = await queueService.callNextPatient();
      if (patient) {
        setCalledPatient(patient as QueueItem);
      }
    } catch (error) {
      console.error('Error calling next patient:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading queue system...</p>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <div className="header-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
            </svg>
          </div>
          <h1>Hospital Queue System</h1>
        </div>
      </header>

      <main className="app-main">
        <div className="container">
          <div className="grid">
            <AddPatientForm onPatientAdded={loadQueue} />
            <QueueList queue={queue} onCallNext={handleCallNext} />
          </div>
        </div>
      </main>

      <CalledPatientModal
        patient={calledPatient}
        onClose={() => setCalledPatient(null)}
      />
    </div>
  );
}

export default App;
