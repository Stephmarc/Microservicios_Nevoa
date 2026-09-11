import { useEffect, useMemo, useState } from 'react';
import { Eye, Trash2, Plus, X } from 'lucide-react';
import { OrdersAPI, ProductsAPI, toPageState, money } from '../lib/api.js';
import { Modal, ConfirmDialog, Pagination, Banner, EmptyState } from '../components/Common.jsx';
import { PageTitle } from '../components/PageTitle.jsx';

const lineTotal = (lines) => (lines || []).reduce((acc, l) => acc + Number(l.price || 0) * Number(l.quantity || 0), 0);

export default function Orders({ token }) {
  const [pageState, setPageState] = useState({ items: [], page: 0, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);
  const [query, setQuery] = useState('');
  const [catalog, setCatalog] = useState([]);

  const [formOpen, setFormOpen] = useState(false);
  const [lines, setLines] = useState([{ code: '', quantity: 1 }]);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const [viewing, setViewing] = useState(null);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async (page = pageState.page) => {
    setLoading(true);
    try {
      const data = await OrdersAPI.list(token, page, 8);
      setPageState(toPageState(data));
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo cargar las órdenes: ${err.message}` });
    } finally { setLoading(false); }
  };

  useEffect(() => {
    load(0);
    ProductsAPI.list(token, 0, 50).then((d) => setCatalog(toPageState(d).items)).catch(() => {});
    // eslint-disable-next-line
  }, []);

  const openCreate = () => { setLines([{ code: '', quantity: 1 }]); setFormError(''); setFormOpen(true); };
  const addLine = () => setLines([...lines, { code: '', quantity: 1 }]);
  const removeLine = (idx) => setLines(lines.filter((_, i) => i !== idx));
  const updateLine = (idx, field, value) => setLines(lines.map((l, i) => (i === idx ? { ...l, [field]: value } : l)));

  const submit = async (e) => {
    e.preventDefault();
    const clean = lines.filter((l) => l.code && Number(l.quantity) > 0);
    if (clean.length === 0) { setFormError('Agrega al menos una línea con código y cantidad mayor a 0.'); return; }
    setSaving(true); setFormError('');
    try {
      const payload = { orderLineItemsList: clean.map((l) => ({ code: l.code, quantity: Number(l.quantity) })) };
      const created = await OrdersAPI.create(token, payload);
      const exclNote = created?.inventoryExclusions?.length
        ? ` Algunas líneas se excluyeron por inventario: ${created.inventoryExclusions.join(', ')}.`
        : '';
      setNotice({ type: 'success', text: `Orden ${created?.orderNumber || ''} creada correctamente.${exclNote}` });
      setFormOpen(false);
      await load(0);
    } catch (err) {
      setFormError(err.message);
    } finally { setSaving(false); }
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    setDeleting(true);
    try {
      await OrdersAPI.remove(token, toDelete.orderId);
      setNotice({ type: 'success', text: `Orden ${toDelete.orderNumber} eliminada y stock restaurado.` });
      setToDelete(null);
      const nextPage = pageState.items.length === 1 && pageState.page > 0 ? pageState.page - 1 : pageState.page;
      await load(nextPage);
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo eliminar: ${err.message}` });
      setToDelete(null);
    } finally { setDeleting(false); }
  };

  const openView = async (o) => {
    setViewing({ loading: true });
    try { setViewing(await OrdersAPI.getById(token, o.orderId)); }
    catch (err) { setNotice({ type: 'error', text: `No se pudo obtener el detalle: ${err.message}` }); setViewing(null); }
  };

  const visible = useMemo(() => {
    if (!query.trim()) return pageState.items;
    const q = query.toLowerCase();
    return pageState.items.filter((o) => `${o.orderNumber} ${o.orderId}`.toLowerCase().includes(q));
  }, [pageState.items, query]);

  return (
    <div className="page fade-in">
      <PageTitle
        title="Órdenes"
        subtitle="ms-common-order valida líneas contra el catálogo y descuenta stock vía WebClient."
        actionLabel="Nueva orden"
        onAction={openCreate}
        searchValue={query}
        onSearch={setQuery}
        searchPlaceholder="Buscar por número u id..."
      />
      <div className="info-note">Las órdenes no admiten edición: una vez creadas son documentos de venta inmutables. El backend solo expone crear, consultar y eliminar (la eliminación restaura el stock descontado).</div>
      <Banner notice={notice} />

      <div className="panel">
        {loading ? <EmptyState text="Cargando órdenes..." /> : visible.length === 0 ? (
          <EmptyState text="Sin órdenes todavía. Crea la primera con “Nueva orden”." />
        ) : (
          <table>
            <thead><tr><th>ID</th><th>Número de orden</th><th>Líneas</th><th>Total</th><th></th></tr></thead>
            <tbody>
              {visible.map((o) => (
                <tr key={o.orderId}>
                  <td>{o.orderId}</td>
                  <td className="mono">{o.orderNumber}</td>
                  <td>{(o.orderLineItemsList || []).length}</td>
                  <td>{money(lineTotal(o.orderLineItemsList))}</td>
                  <td className="row-actions">
                    <button type="button" onClick={() => openView(o)} title="Ver detalle"><Eye size={16} /></button>
                    <button type="button" className="danger" onClick={() => setToDelete(o)} title="Eliminar"><Trash2 size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <Pagination page={pageState.page} totalPages={pageState.totalPages} totalElements={pageState.totalElements} onChange={load} />

      {formOpen && (
        <Modal title="Nueva orden" subtitle="POST /create-order — precio y descripción se completan desde el catálogo." onClose={() => setFormOpen(false)} width="640px">
          <form className="modal-form" onSubmit={submit}>
            <label>Líneas del pedido</label>
            {lines.map((l, idx) => (
              <div className="order-line-row" key={idx}>
                <select value={l.code} onChange={(e) => updateLine(idx, 'code', e.target.value)}>
                  <option value="">Selecciona un SKU...</option>
                  {catalog.map((p) => <option key={p.productId} value={p.skuCode}>{p.skuCode} — {p.name}</option>)}
                </select>
                <input
                  type="number" min="1" value={l.quantity}
                  onChange={(e) => updateLine(idx, 'quantity', e.target.value)}
                  placeholder="Cant."
                />
                <button type="button" className="icon-btn" onClick={() => removeLine(idx)} disabled={lines.length === 1} title="Quitar línea"><X size={16} /></button>
              </div>
            ))}
            <button type="button" className="ghost-btn small" onClick={addLine}><Plus size={15} /> Agregar línea</button>
            {formError && <div className="error-box small"><span>{formError}</span></div>}
            <div className="modal-actions">
              <button type="button" className="ghost-btn" onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className="primary-small" disabled={saving}>{saving ? 'Creando...' : 'Crear orden'}</button>
            </div>
          </form>
        </Modal>
      )}

      {viewing && (
        <Modal title="Detalle de la orden" subtitle="GET /get-order-by-id/{id}" onClose={() => setViewing(null)} width="640px">
          {viewing.loading ? <p className="muted">Cargando...</p> : (
            <>
              <div className="detail-grid">
                <div><span>Order ID</span><strong>{viewing.orderId}</strong></div>
                <div><span>Número</span><strong className="mono">{viewing.orderNumber}</strong></div>
                <div><span>Total</span><strong>{money(lineTotal(viewing.orderLineItemsList))}</strong></div>
              </div>
              <table className="detail-table">
                <thead><tr><th>Código</th><th>Descripción</th><th>Cant.</th><th>Precio</th></tr></thead>
                <tbody>
                  {(viewing.orderLineItemsList || []).map((l) => (
                    <tr key={l.orderLineItemId}><td>{l.code}</td><td>{l.description}</td><td>{l.quantity}</td><td>{money(l.price)}</td></tr>
                  ))}
                </tbody>
              </table>
              {viewing.inventoryExclusions?.length > 0 && (
                <p className="muted">Excluidas en creación: {viewing.inventoryExclusions.join(', ')}</p>
              )}
            </>
          )}
        </Modal>
      )}

      {toDelete && (
        <ConfirmDialog
          title="Eliminar orden"
          message={`Se eliminará la orden ${toDelete.orderNumber} y se restaurará el stock descontado.`}
          onCancel={() => setToDelete(null)}
          onConfirm={confirmDelete}
          loading={deleting}
        />
      )}
    </div>
  );
}
