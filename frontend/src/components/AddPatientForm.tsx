import { useState, type FormEvent } from 'react';
import { queueService } from '../services/queueService';

type PriorityOption = {
  id: number;
  name: string;
  label: string;
  color: string;
};

const priorityOptions: PriorityOption[] = [
  { id: 1, name: 'LOW', label: 'Low Priority', color: '#10b981' },
  { id: 2, name: 'MEDIUM', label: 'Medium Priority', color: '#f59e0b' },
  { id: 3, name: 'HIGH', label: 'High Priority', color: '#ef4444' },
  { id: 4, name: 'EXTREME', label: 'Extreme Priority', color: '#dc2626' },
];

export function AddPatientForm({ onPatientAdded }: { onPatientAdded: () => void }) {
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [priorityLevel, setPriorityLevel] = useState(2);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    const namePattern = /^[a-zA-ZÀ-ÿ\s]+$/;
    if (!name.trim()) {
      setError('Name is required');
      return;
    }
    if (!namePattern.test(name)) {
      setError('Name can only contain letters and spaces');
      return;
    }

    const ageNum = parseInt(age);
    if (isNaN(ageNum) || ageNum < 0 || ageNum > 130) {
      setError('Age must be between 0 and 130');
      return;
    }

    setIsSubmitting(true);
    try {
      await queueService.addPatient(name, ageNum, priorityLevel);
      setName('');
      setAge('');
      setPriorityLevel(2);
      onPatientAdded();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to add patient');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="card">
      <h2 className="card-title">Add New Patient</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="name">Patient Name</label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter patient name"
            disabled={isSubmitting}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="age">Age</label>
          <input
            id="age"
            type="number"
            value={age}
            onChange={(e) => setAge(e.target.value)}
            placeholder="Enter age"
            min="0"
            max="130"
            disabled={isSubmitting}
            required
          />
        </div>

        <div className="form-group">
          <label>Priority Level</label>
          <div className="priority-grid">
            {priorityOptions.map((option) => (
              <button
                key={option.id}
                type="button"
                className={`priority-btn ${priorityLevel === option.id ? 'active' : ''}`}
                style={{
                  '--priority-color': option.color,
                } as React.CSSProperties}
                onClick={() => setPriorityLevel(option.id)}
                disabled={isSubmitting}
              >
                <span className="priority-name">{option.label}</span>
              </button>
            ))}
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
          {isSubmitting ? 'Adding...' : 'Add to Queue'}
        </button>
      </form>
    </div>
  );
}
