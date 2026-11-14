
import './App.css';

type MetricCardProps = {
  title: string;
  value: number;
  color: string;
};

function MetricCard({ title, value, color }: MetricCardProps) {
  return (
      <div className="metric-card">
        <h3>{title}</h3>
        <h2 style={{ color }}>{value.toFixed(2)}%</h2>
      </div>
  );
}

function App() {
  return (
      <div className="dashboard">
        <h1>CloudOps Agent</h1>
        <p>Infrastructure Monitoring Dashboard</p>

        <div className="metrics-grid">
          <MetricCard
              title="CPU Usage"
              value={24.6}
              color="#60a5fa"
          />

          <MetricCard
              title="Memory Usage"
              value={67.8}
              color="#a78bfa"
          />
        </div>
      </div>
  );
}

export default App;
