
import './App.css'

function App() {
  const services = [
    {
      name: 'AWS Lambda',
      detail: 'Invocations, errors, duration and throttles',
    },
    {
      name: 'Amazon API Gateway',
      detail: '4XX/5XX responses and integration latency',
    },
    {
      name: 'Amazon DynamoDB',
      detail: 'Request throttling and capacity signals',
    },
  ]

  return (
    <main className="dashboard">
      <header className="hero">
        <div>
          <p className="eyebrow">FITCLOUD OPERATIONS</p>
          <h1>CloudOps Agent</h1>
          <p className="hero-copy">
            AWS monitoring foundation for FitCloud's serverless backend.
          </p>
        </div>
        <span className="status-badge">CloudWatch connection pending</span>
      </header>

      <section className="notice" aria-labelledby="telemetry-status">
        <div className="notice-icon" aria-hidden="true">i</div>
        <div>
          <h2 id="telemetry-status">No live telemetry connected</h2>
          <p>
            This dashboard intentionally shows no simulated metrics. Live values
            will appear after read-only AWS CloudWatch access and resource scope
            are configured.
          </p>
        </div>
      </section>

      <section className="scope" aria-labelledby="monitoring-scope">
        <div className="section-heading">
          <div>
            <p className="eyebrow">PLANNED SIGNALS</p>
            <h2 id="monitoring-scope">Monitoring scope</h2>
          </div>
          <p>Data source: Amazon CloudWatch</p>
        </div>

        <div className="service-grid">
          {services.map((service) => (
            <article className="service-card" key={service.name}>
              <span className="service-dot" aria-hidden="true" />
              <h3>{service.name}</h3>
              <p>{service.detail}</p>
              <span className="not-configured">Not configured</span>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}

export default App
