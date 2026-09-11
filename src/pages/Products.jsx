import { useEffect, useMemo, useState } from 'react';
import { Package, Pencil, Trash2, Eye } from 'lucide-react';
import { ProductsAPI, toPageState, money } from '../lib/api.js';
import { Modal, ConfirmDialog, Pagination, Banner, EmptyState } from '../components/Common.jsx';
import { PageTitle } from '../components/PageTitle.jsx';

const emptyForm = { skuCode: '', name: '', description: '', price: '', initialStockQuantity: '' };

export default function Products({ token }) {
  const [pageState, setPageState] = useState({ items: [], page: 0, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);
  const [query, setQuery] = useState('');

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null); // null = crear, objeto = editar
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const [viewing, setViewing] = useState(null);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async (page = pageState.page) => {
    setLoading(true);
    try {
      const data = await ProductsAPI.list(token, page, 8);
      setPageState(toPageState(data));
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo cargar el catálogo: ${err.message}` });
    } finally { setLoading(false); }
  };

  useEffect(() => { load(0); /* eslint-disable-next-line */ }, []);

  const openCreate = () => { setEditing(null); setForm(emptyForm); setFormError(''); setFormOpen(true); };
  const openEdit = (p) => {
    setEditing(p);
    setForm({ skuCode: p.skuCode || '', name: p.name || '', description: p.description || '', price: p.price ?? '', initialStockQuantity: '' });
    setFormError('');
    setFormOpen(true);
  };

  const submit = async (e) => {
    e.preventDefault();
    if (!form.skuCode.trim()) { setFormError('El SKU es obligatorio.'); return; }
    setSaving(true); setFormError('');
    try {
      const payload = {
        skuCode: form.skuCode.trim(),
        name: form.name.trim(),
        description: form.description.trim(),
        price: form.price === '' ? null : Number(form.price),
      };
      if (editing) {
        await ProductsAPI.update(token, editing.productId, payload);
        setNotice({ type: 'success', text: `Producto ${payload.skuCode} actualizado correctamente.` });
      } else {
        payload.initialStockQuantity = form.initialStockQuantity === '' ? 0 : Number(form.initialStockQuantity);
        await ProductsAPI.create(token, payload);
        setNotice({ type: 'success', text: `Producto ${payload.skuCode} creado. El stock inicial se generará vía evento Kafka.` });
      }
      setFormOpen(false);
      await load(editing ? pageState.page : 0);
    } catch (err) {
      setFormError(err.message);
    } finally { setSaving(false); }
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    setDeleting(true);
    try {
      await ProductsAPI.remove(token, toDelete.productId);
      setNotice({ type: 'success', text: `Producto ${toDelete.skuCode} eliminado.` });
      setToDelete(null);
      const nextPage = pageState.items.length === 1 && pageState.page > 0 ? pageState.page - 1 : pageState.page;
      await load(nextPage);
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo eliminar: ${err.message}` });
      setToDelete(null);
    } finally { setDeleting(false); }
  };

  const openView = async (p) => {
    setViewing({ loading: true, skuCode: p.skuCode });
    try {
      const data = await ProductsAPI.getById(token, p.productId);
      setViewing(data);
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo obtener el detalle: ${err.message}` });
      setViewing(null);
    }
  };

  const visible = useMemo(() => {
    if (!query.trim()) return pageState.items;
    const q = query.toLowerCase();
    return pageState.items.filter((p) => `${p.skuCode} ${p.name} ${p.description}`.toLowerCase().includes(q));
  }, [pageState.items, query]);

  return (
    <div className="page fade-in">
      <PageTitle
        title="Productos"
        subtitle="Catálogo persistido en MongoDB por ms-common-product."
        actionLabel="Nuevo producto"
        onAction={openCreate}
        searchValue={query}
        onSearch={setQuery}
        searchPlaceholder="Buscar por SKU o nombre..."
      />
      <Banner notice={notice} />

      {loading ? (
        <EmptyState text="Cargando productos..." />
      ) : visible.length === 0 ? (
        <EmptyState text="Sin productos todavía. Crea el primero con “Nuevo producto”." />
      ) : (
        <div className="product-grid">
          {visible.map((p) => (
            <div className="product-card" key={p.productId}>
              <div className="product-img"><Package /></div>
              <h3>{p.name || 'Producto sin nombre'}</h3>
              <p>{p.description || 'Sin descripción registrada.'}</p>
              <strong>{money(p.price)}</strong>
              <span>{p.skuCode}</span>
              <div className="card-actions">
                <button type="button" onClick={() => openView(p)} title="Ver detalle"><Eye size={16} /></button>
                <button type="button" onClick={() => openEdit(p)} title="Editar"><Pencil size={16} /></button>
                <button type="button" className="danger" onClick={() => setToDelete(p)} title="Eliminar"><Trash2 size={16} /></button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Pagination page={pageState.page} totalPages={pageState.totalPages} totalElements={pageState.totalElements} onChange={load} />

      {formOpen && (
        <Modal
          title={editing ? `Editar ${editing.skuCode}` : 'Nuevo producto'}
          subtitle={editing ? 'Los cambios se aplican con PUT /update-product/{id}.' : 'Se persiste con POST /create-product.'}
          onClose={() => setFormOpen(false)}
        >
          <form className="modal-form" onSubmit={submit}>
            <label>SKU</label>
            <input value={form.skuCode} onChange={(e) => setForm({ ...form, skuCode: e.target.value })} placeholder="IPHONE-15-128" disabled={!!editing} required />
            <label>Nombre</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Auriculares Bluetooth ANC" />
            <label>Descripción</label>
            <textarea rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Características, garantía, etc." />
            <div className="form-row">
              <div>
                <label>Precio (S/)</label>
                <input type="number" min="0" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} placeholder="129990" />
              </div>
              {!editing && (
                <div>
                  <label>Stock inicial</label>
                  <input type="number" min="0" value={form.initialStockQuantity} onChange={(e) => setForm({ ...form, initialStockQuantity: e.target.value })} placeholder="10" />
                </div>
              )}
            </div>
            {formError && <div className="error-box small"><span>{formError}</span></div>}
            <div className="modal-actions">
              <button type="button" className="ghost-btn" onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className="primary-small" disabled={saving}>{saving ? 'Guardando...' : editing ? 'Guardar cambios' : 'Crear producto'}</button>
            </div>
          </form>
        </Modal>
      )}

      {viewing && (
        <Modal title="Detalle del producto" subtitle="GET /get-product-by-id/{id}" onClose={() => setViewing(null)}>
          {viewing.loading ? <p className="muted">Cargando...</p> : (
            <div className="detail-grid">
              <div><span>Product ID</span><strong>{viewing.productId}</strong></div>
              <div><span>SKU</span><strong>{viewing.skuCode}</strong></div>
              <div><span>Nombre</span><strong>{viewing.name || '—'}</strong></div>
              <div><span>Precio</span><strong>{money(viewing.price)}</strong></div>
              <div className="detail-full"><span>Descripción</span><strong>{viewing.description || '—'}</strong></div>
            </div>
          )}
        </Modal>
      )}

      {toDelete && (
        <ConfirmDialog
          title="Eliminar producto"
          message={`Se eliminará “${toDelete.skuCode}” del catálogo. Esta acción no se puede deshacer.`}
          onCancel={() => setToDelete(null)}
          onConfirm={confirmDelete}
          loading={deleting}
        />
      )}
    </div>
  );
}
