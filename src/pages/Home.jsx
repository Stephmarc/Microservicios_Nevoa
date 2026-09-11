import { useEffect, useState } from 'react';
import { Package, Warehouse, ShoppingCart, Users, Server } from 'lucide-react';
import { ProductsAPI, StockAPI, OrdersAPI, CustomersAPI, HealthAPI, toPageState, money } from '../lib/api.js';

const lineTotal = (lines) => (lines || []).reduce((acc, l) => acc + Number(l.price || 0) * Number(l.quantity || 0), 0);

export default function Home({ token }) {
  const [stats, setStats] = useState({ products: 0, stock: 0, orders: 0, customers: 0, health: 0, healthTotal: 5 });
  const [recentProducts, setRecentProducts] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    Promise.allSettled([
      ProductsAPI.list(token, 0, 5),
      StockAPI.list(token, 0, 100),
      OrdersAPI.list(token, 0, 5),
      CustomersAPI.list(token, { page: 0, size: 1 }),
      HealthAPI.checkAll(token),
    ]).then(([products, stock, orders, customers, health]) => {
      if (!alive) return;
      const productsPage = products.status === 'fulfilled' ? toPageState(products.value) : { items: [], totalElements: 0 };
      const stockPage = stock.status === 'fulfilled' ? toPageState(stock.value) : { items: [], totalElements: 0 };
      const ordersPage = orders.status === 'fulfilled' ? toPageState(orders.value) : { items: [], totalElements: 0 };
      const customersPage = customers.status === 'fulfilled' ? toPageState(customers.value) : { totalElements: 0 };
      const healthResult = health.status === 'fulfilled' ? health.value : [];

      setRecentProducts(productsPage.items);
      setRecentOrders(ordersPage.items);
      setStats({
        products: productsPage.totalElements,
        stock: stockPage.items.reduce((a, s) => a + Number(s.quantity || 0), 0),
        orders: ordersPage.totalElements,
        customers: customersPage.totalElements,
        health: healthResult.filter((s) => s.ok).length,
        healthTotal: healthResult.length || 5,
      });
      setLoading(false);
    });
    return () => { alive = false; };
  }, [token]);

  return (
    <div className="page fade-in">
      <div className="hero">
        <div>
          <p className="eyebrow">Resumen general</p>
          <h1>Panel de gestión comercial</h1>
          <p>Conectado al backend mediante API Gateway, JWT, microservicios y eventos Kafka.</p>
        </div>
        <div className="hero-badge">{loading ? 'Sincronizando...' : 'Operativo'}</div>
      </div>

      <div className="cards-grid">
        <Metric icon={<Package />} label="Productos" value={stats.products} />
        <Metric icon={<Warehouse />} label="Stock total (unid.)" value={stats.stock} />
        <Metric icon={<ShoppingCart />} label="Órdenes" value={stats.orders} />
        <Metric icon={<Users />} label="Clientes" value={stats.customers} />
        <Metric icon={<Server />} label="Servicios OK" value={`${stats.health}/${stats.healthTotal}`} />
      </div>

      <div className="two-col">
        <Panel title="Productos recientes">
          {recentProducts.length === 0 ? <p className="empty">Sin datos todavía.</p> : (
            <table>
              <thead><tr><th>SKU</th><th>Nombre</th><th>Precio</th></tr></thead>
              <tbody>{recentProducts.map((p) => <tr key={p.productId}><td>{p.skuCode}</td><td>{p.name}</td><td>{money(p.price)}</td></tr>)}</tbody>
            </table>
          )}
        </Panel>
        <Panel title="Órdenes recientes">
          {recentOrders.length === 0 ? <p className="empty">Sin datos todavía.</p> : (
            <table>
              <thead><tr><th>Número</th><th>Líneas</th><th>Total</th></tr></thead>
              <tbody>{recentOrders.map((o) => <tr key={o.orderId}><td className="mono">{o.orderNumber?.slice(0, 8)}…</td><td>{(o.orderLineItemsList || []).length}</td><td>{money(lineTotal(o.orderLineItemsList))}</td></tr>)}</tbody>
            </table>
          )}
        </Panel>
      </div>
    </div>
  );
}

function Metric({ icon, label, value }) {
  return (
    <div className="metric">
      <div className="metric-icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
      <small>Datos desde el backend</small>
    </div>
  );
}

function Panel({ title, children }) {
  return <section className="panel"><h3>{title}</h3>{children}</section>;
}
