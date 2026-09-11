import { useEffect, useState } from 'react';
import { RefreshCcw } from 'lucide-react';
import { HealthAPI } from '../lib/api.js';
import { PageTitle } from '../components/PageTitle.jsx';

export default function Monitoring({ token }) {
  const [status, setStatus] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try { setStatus(await HealthAPI.checkAll(token)); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, []);

  return (
    <div className="page fade-in">
      <PageTitle title="Estado del sistema" subtitle="Verificación de /actuator/health en cada microservicio, vía Gateway." />
      <button type="button" className="ghost-btn small" onClick={load} disabled={loading}>
        <RefreshCcw size={15} /> {loading ? 'Verificando...' : 'Volver a verificar'}
      </button>
      <div className="status-grid" style={{ marginTop: 18 }}>
        {status.map((s) => (
          <div className="status-card" key={s.name}>
            <div className={s.ok ? 'dot ok' : 'dot bad'} />
            <strong>{s.name}</strong>
            <span>{s.ok ? 'UP' : 'Revisar'}</span>
          </div>
        ))}
      </div>
      <div className="links">
        <a href="http://localhost:8083" target="_blank" rel="noreferrer">Eureka</a>
        <a href="http://localhost:19000" target="_blank" rel="noreferrer">Kafdrop</a>
        <a href="http://localhost:3000" target="_blank" rel="noreferrer">Grafana</a>
        <a href="http://localhost:9411" target="_blank" rel="noreferrer">Zipkin</a>
      </div>
    </div>
  );
}
