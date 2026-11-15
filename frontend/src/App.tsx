
import { useEffect, useState } from 'react'
import './App.css'

type SystemMetric = {
    id: string
    cpuUsage: number
    memoryUsage: number
    collectedAt: string
}

function App() {
    const [metrics, setMetrics] = useState<SystemMetric[]>([])
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        let active = true

        async function loadMetrics() {
            try {
                const response = await fetch('/api/metrics?limit=1')

                if (!response.ok) {
                    throw new Error('Failed to fetch metrics')
                }

                const data: SystemMetric[] = await response.json()

                if (active) {
                    setMetrics(data)
                    setError(null)
                }
            } catch {
                if (active) {
                    setError('Failed to connect to backend')
                }
            }
        }

        loadMetrics()

        const timer = setInterval(loadMetrics, 10000)

        return () => {
            active = false
            clearInterval(timer)
        }
    }, [])

    const latest = metrics[0]

    return (
        <div className="dashboard">
            <h1>CloudOps Agent</h1>
            <p>Infrastructure Monitoring Dashboard</p>

            {error && <p>{error}</p>}

            {latest ? (
                <div className="metrics-grid">
                    <div className="metric-card">
                        <h3>CPU Usage</h3>
                        <h2>{latest.cpuUsage.toFixed(2)}%</h2>
                    </div>

                    <div className="metric-card">
                        <h3>Memory Usage</h3>
                        <h2>{latest.memoryUsage.toFixed(2)}%</h2>
                    </div>
                </div>
            ) : (
                <p>Waiting for monitoring data...</p>
            )}
        </div>
    )
}

export default App
