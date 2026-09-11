import { useState } from 'react';
import { Leaf, Activity, Package, Warehouse, ShoppingCart, Users, Server, LogOut } from 'lucide-react';
import Login from './pages/Login.jsx';
import Home from './pages/Home.jsx';
import Products from './pages/Products.jsx';
import Stock from './pages/Stock.jsx';
import Orders from './pages/Orders.jsx';
import Customers from './pages/Customers.jsx';
import Monitoring from './pages/Monitoring.jsx';

const NAV_ITEMS = [
  ['dashboard', 'Dashboard', Activity],
  ['products', 'Productos', Package],
  ['stock', 'Inventario', Warehouse],
  ['orders', 'Órdenes', ShoppingCart],
  ['customers', 'Clientes', Users],
  ['monitoring', 'Monitoreo', Server],
];

export default function App() {
  const [session, setSession] = useState(() => {
    const token = localStorage.getItem('nevoa_token');
    const user = JSON.parse(localStorage.getItem('nevoa_user') || 'null');
    return token ? { token, user } : null;
  });

  if (!session) return <Login onLogin={setSession} />;
  return <Dashboard session={session} onLogout={() => { localStorage.clear(); setSession(null); }} />;
}

function Dashboard({ session, onLogout }) {
  const [active, setActive] = useState('dashboard');
  const { token, user } = session;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="side-logo"><Leaf /><div><strong>NEVOA</strong><span>Comercio & Stock</span></div></div>
        <nav>
          {NAV_ITEMS.map(([id, label, Icon]) => (
            <button key={id} className={active === id ? 'active' : ''} onClick={() => setActive(id)}>
              <Icon size={19} />{label}
            </button>
          ))}
        </nav>
        <div className="user-box">
          <div className="avatar">{(user?.firstName || 'A')[0]}</div>
          <div><strong>{user?.firstName || 'Admin'} {user?.lastName || ''}</strong><span>{(user?.roles || []).join(', ') || 'Usuario'}</span></div>
        </div>
      </aside>

      <section className="main-area">
        <header className="topbar">
          <div className="topbar-title">
            <h2>{NAV_ITEMS.find(([id]) => id === active)?.[1]}</h2>
          </div>
          <button className="logout-btn" onClick={onLogout}><LogOut size={17} /> Salir</button>
        </header>

        {active === 'dashboard' && <Home token={token} />}
        {active === 'products' && <Products token={token} />}
        {active === 'stock' && <Stock token={token} />}
        {active === 'orders' && <Orders token={token} />}
        {active === 'customers' && <Customers token={token} />}
        {active === 'monitoring' && <Monitoring token={token} />}
      </section>
    </div>
  );
}
