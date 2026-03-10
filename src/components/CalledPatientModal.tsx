import { useEffect } from 'react';

type CalledPatient = {
  patients: {
    name: string;
    age: number;
    priority_levels: {
      name: string;
    };
  };
};

export function CalledPatientModal({
  patient,
  onClose,
}: {
  patient: CalledPatient | null;
  onClose: () => void;
}) {
  useEffect(() => {
    if (patient) {
      const timer = setTimeout(() => {
        onClose();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [patient, onClose]);

  if (!patient) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Next Patient</h2>
          <button className="modal-close" onClick={onClose}>
            ×
          </button>
        </div>
        <div className="modal-body">
          <div className="called-patient-info">
            <div className="called-patient-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
            </div>
            <h3 className="called-patient-name">{patient.patients.name}</h3>
            <div className="called-patient-details">
              <p>Age: {patient.patients.age}</p>
              <p>Priority: {patient.patients.priority_levels.name}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
