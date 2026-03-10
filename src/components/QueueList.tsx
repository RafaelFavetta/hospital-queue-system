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

const getPriorityColor = (priorityName: string) => {
  switch (priorityName) {
    case 'LOW':
      return '#10b981';
    case 'MEDIUM':
      return '#f59e0b';
    case 'HIGH':
      return '#ef4444';
    case 'EXTREME':
      return '#dc2626';
    default:
      return '#6b7280';
  }
};

export function QueueList({ queue, onCallNext }: { queue: QueueItem[]; onCallNext: () => void }) {
  const isElderlyAge = (age: number) => age >= 60;

  return (
    <div className="card">
      <div className="card-header">
        <h2 className="card-title">Current Queue</h2>
        <span className="queue-count">{queue.length} patient{queue.length !== 1 ? 's' : ''}</span>
      </div>

      {queue.length === 0 ? (
        <div className="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
            <circle cx="9" cy="7" r="4" />
            <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
            <path d="M16 3.13a4 4 0 0 1 0 7.75" />
          </svg>
          <p>No patients in queue</p>
        </div>
      ) : (
        <>
          <button className="btn btn-primary btn-call" onClick={onCallNext}>
            Call Next Patient
          </button>
          <div className="queue-items">
            {queue.map((item, index) => (
              <div key={item.id} className="queue-item">
                <div className="queue-item-number">{index + 1}</div>
                <div className="queue-item-content">
                  <div className="queue-item-header">
                    <h3 className="queue-item-name">{item.patients.name}</h3>
                    <span
                      className="priority-badge"
                      style={{ backgroundColor: getPriorityColor(item.patients.priority_levels.name) }}
                    >
                      {item.patients.priority_levels.name}
                    </span>
                  </div>
                  <div className="queue-item-details">
                    <span className="detail">
                      Age: {item.patients.age}
                      {isElderlyAge(item.patients.age) && (
                        <span className="elderly-badge">Elderly Priority</span>
                      )}
                    </span>
                    <span className="detail">Score: {item.priority_score}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
