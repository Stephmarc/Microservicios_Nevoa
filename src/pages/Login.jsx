import { useState } from 'react';
import { Box, Eye, EyeOff, ShieldCheck, RadioTower, Activity, UserRound, LockKeyhole, AlertTriangle, Leaf } from 'lucide-react';
import { AuthAPI } from '../lib/api.js';

export default function Login({ onLogin }) {
  const [document, setDocument] = useState('1234567890');
  const [password, setPassword] = useState('admin');
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      const data = await AuthAPI.login(document, password);
      const token = data?.token || data?.accessToken || data?.jwt || data?.data?.token;
      if (!token) throw new Error('El backend respondió, pero no devolvió token JWT.');
      localStorage.setItem('nevoa_token', token);
      localStorage.setItem('nevoa_user', JSON.stringify(data));
      onLogin({ token, user: data });
    } catch (err) {
      setError(`No se pudo iniciar sesión. ${err.message}`);
    } finally { setLoading(false); }
  };

  return (
    <main className="login-shell">
      <section className="brand-panel">
        <div className="brand-overlay" />
        <div className="brand-content">
          <div className="brand-logo"><Leaf size={30} /><span>NEVOA</span></div>
          <p className="brand-subtitle">Gestión comercial para alojamientos, productos y órdenes.</p>
          <div className="brand-copy">
            <h1>Administra tu operación con calma.</h1>
            <p>Productos, inventario, órdenes y clientes conectados mediante servicios distribuidos.</p>
          </div>
          <div className="brand-features">
            <span><ShieldCheck size={18} /> Acceso seguro</span>
            <span><RadioTower size={18} /> Eventos Kafka</span>
            <span><Activity size={18} /> Monitoreo activo</span>
          </div>
        </div>
      </section>

      <section className="login-panel">
        <div className="language-pill">ES</div>
        <form className="login-card" onSubmit={submit}>
          <div className="login-icon"><Box size={34} /></div>
          <p className="eyebrow">Panel interno</p>
          <h2>Hola de nuevo</h2>
          <p className="muted">Ingresa con las credenciales del backend.</p>

          <label>Documento</label>
          <div className="input-wrap"><UserRound size={18} /><input value={document} onChange={(e) => setDocument(e.target.value)} placeholder="1234567890" /></div>
          <label>Contraseña</label>
          <div className="input-wrap">
            <LockKeyhole size={18} />
            <input type={show ? 'text' : 'password'} value={password} onChange={(e) => setPassword(e.target.value)} placeholder="admin" />
            <button type="button" onClick={() => setShow(!show)}>{show ? <EyeOff size={18} /> : <Eye size={18} />}</button>
          </div>

          <div className="login-row"><span>Admin: 1234567890 / admin</span></div>
          {error && <div className="error-box"><AlertTriangle size={18} />{error}</div>}
          <button className="primary-btn" disabled={loading}>{loading ? 'Conectando...' : 'Ingresar'}</button>
          <p className="api-note">Gateway: proxy /api → localhost:9090</p>
        </form>
      </section>
    </main>
  );
}
