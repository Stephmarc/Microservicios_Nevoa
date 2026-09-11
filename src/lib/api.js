// Capa de acceso a datos. Todas las peticiones pasan por el API Gateway
// (proxy /api -> http://localhost:9090 configurado en vite.config.js),
// por lo que el frontend nunca conoce los puertos internos de cada microservicio.
export const API_BASE_URL = '';

/**
 * Cliente HTTP mínimo compartido por todos los módulos.
 * Adjunta el JWT como Bearer y normaliza los errores del backend
 * (ApiErrorResponse de com.common.library.exception) a un Error de JS legible.
 */
export async function request(path, options = {}, token) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });
  const text = await res.text();
  let body = null;
  try { body = text ? JSON.parse(text) : null; } catch { body = text; }
  if (!res.ok) {
    const message = typeof body === 'string' ? body : body?.message || body?.error || `Error HTTP ${res.status}`;
    const error = new Error(message);
    error.status = res.status;
    error.body = body;
    throw error;
  }
  return body;
}

/** Convierte un `Page` de Spring Data (o una lista simple) en un estado uniforme para las tablas. */
export function toPageState(data) {
  if (data && Array.isArray(data.content)) {
    return {
      items: data.content,
      page: data.number ?? 0,
      totalPages: data.totalPages ?? 1,
      totalElements: data.totalElements ?? data.content.length,
    };
  }
  if (Array.isArray(data)) {
    return { items: data, page: 0, totalPages: 1, totalElements: data.length };
  }
  return { items: data ? [data] : [], page: 0, totalPages: 1, totalElements: data ? 1 : 0 };
}

export const money = (n) =>
  new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 0 }).format(Number(n || 0));

export const formatDate = (value) => {
  if (!value) return '—';
  try { return new Intl.DateTimeFormat('es-PE', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)); }
  catch { return String(value); }
};

// ---------------------------------------------------------------------------
// Auth — ms-common-person (/api/person/auth)
// ---------------------------------------------------------------------------
export const AuthAPI = {
  login: (document, password) =>
    request('/api/person/auth/login', { method: 'POST', body: JSON.stringify({ document, password }) }),
};

// ---------------------------------------------------------------------------
// Productos — ms-common-product (/api/product) — MongoDB
// ---------------------------------------------------------------------------
export const ProductsAPI = {
  list: (token, page = 0, size = 10) =>
    request(`/api/product/get-all-products?page=${page}&size=${size}`, {}, token),
  getById: (token, productId) =>
    request(`/api/product/get-product-by-id/${encodeURIComponent(productId)}`, {}, token),
  create: (token, payload) =>
    request('/api/product/create-product', { method: 'POST', body: JSON.stringify(payload) }, token),
  update: (token, productId, payload) =>
    request(`/api/product/update-product/${encodeURIComponent(productId)}`, { method: 'PUT', body: JSON.stringify(payload) }, token),
  remove: (token, productId) =>
    request(`/api/product/delete-product/${encodeURIComponent(productId)}`, { method: 'DELETE' }, token),
};

// ---------------------------------------------------------------------------
// Inventario — ms-common-stock (/api/stock) — PostgreSQL
// ---------------------------------------------------------------------------
export const StockAPI = {
  list: (token, page = 0, size = 10) =>
    request(`/api/stock/get-all-stock-entries?page=${page}&size=${size}`, {}, token),
  getById: (token, stockId) =>
    request(`/api/stock/get-stock-entry-by-id/${stockId}`, {}, token),
  create: (token, payload) =>
    request('/api/stock/create-stock-entry', { method: 'POST', body: JSON.stringify(payload) }, token),
  update: (token, stockId, payload) =>
    request(`/api/stock/update-stock-entry/${stockId}`, { method: 'PUT', body: JSON.stringify(payload) }, token),
  remove: (token, stockId) =>
    request(`/api/stock/delete-stock-entry/${stockId}`, { method: 'DELETE' }, token),
};

// ---------------------------------------------------------------------------
// Órdenes — ms-common-order (/api/order) — PostgreSQL
// El backend NO expone actualización de órdenes (son documentos de venta
// inmutables una vez creadas), por eso este módulo es Crear + Leer + Eliminar.
// ---------------------------------------------------------------------------
export const OrdersAPI = {
  list: (token, page = 0, size = 10) =>
    request(`/api/order/get-all-orders?page=${page}&size=${size}`, {}, token),
  getById: (token, orderId) =>
    request(`/api/order/get-order-by-id/${orderId}`, {}, token),
  getByNumber: (token, orderNumber) =>
    request(`/api/order/get-order-by-number/${encodeURIComponent(orderNumber)}`, {}, token),
  create: (token, payload) =>
    request('/api/order/create-order', { method: 'POST', body: JSON.stringify(payload) }, token),
  remove: (token, orderId) =>
    request(`/api/order/delete-order/${orderId}`, { method: 'DELETE' }, token),
};

// ---------------------------------------------------------------------------
// Clientes — ms-common-person (/api/person/customers) — PostgreSQL
// ---------------------------------------------------------------------------
export const DOCUMENT_TYPES = ['CC', 'TI', 'CE', 'CEX', 'NIT'];

export const CustomersAPI = {
  list: (token, { page = 0, size = 10, document = '', name = '' } = {}) => {
    const params = new URLSearchParams({ page, size });
    if (document) params.set('document', document);
    if (name) params.set('name', name);
    return request(`/api/person/customers/get-all-customers?${params.toString()}`, {}, token);
  },
  getById: (token, customerId) =>
    request(`/api/person/customers/get-customer-by-id/${customerId}`, {}, token),
  create: (token, payload) =>
    request('/api/person/customers/create-customer', { method: 'POST', body: JSON.stringify(payload) }, token),
  update: (token, customerId, payload) =>
    request(`/api/person/customers/update-customer/${customerId}`, { method: 'PUT', body: JSON.stringify(payload) }, token),
  remove: (token, customerId) =>
    request(`/api/person/customers/delete-customer/${customerId}`, { method: 'DELETE' }, token),
};

// ---------------------------------------------------------------------------
// Monitoreo — health-check agregado vía Gateway
// ---------------------------------------------------------------------------
export const HealthAPI = {
  checkAll: async (token) => {
    const checks = [
      ['Gateway', '/actuator/health'],
      ['Productos', '/api/product/actuator/health'],
      ['Inventario', '/api/stock/actuator/health'],
      ['Órdenes', '/api/order/actuator/health'],
      ['Personas', '/api/person/actuator/health'],
    ];
    return Promise.all(checks.map(async ([name, path]) => {
      try { await request(path, {}, token); return { name, ok: true }; }
      catch { return { name, ok: false }; }
    }));
  },
};
