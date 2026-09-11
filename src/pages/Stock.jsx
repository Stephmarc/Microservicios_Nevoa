import { useEffect, useMemo, useState } from 'react';
import { Pencil, Trash2, Eye } from 'lucide-react';
import { StockAPI, toPageState } from '../lib/api.js';
import { Modal, ConfirmDialog, Pagination, Banner, EmptyState } from '../components/Common.jsx';
import { PageTitle } from '../components/PageTitle.jsx';

const emptyForm = { code: '', quantity: '' };

export default function Stock({ token }) {
  const [pageState, setPageState] = useState({ items: [], page: 0, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);
  const [query, setQuery] = useState('');

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const [viewing, setViewing] = useState(null);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async (page = pageState.page) => {
    setLoading(true);
    try {
      const data = await StockAPI.list(token, page, 8);
      setPageState(toPageState(data));
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo cargar el inventario: ${err.message}` });
    } finally { setLoading(false); }
  };

  useEffect(() => { load(0); /* eslint-disable-next-line */ }, []);

  const openCreate = () => { setEditing(null); setForm(emptyForm); setFormError(''); setFormOpen(true); };
  const openEdit = (row) => { setEditing(row); setForm({ code: row.code || '', quantity: row.quantity ?? '' }); setFormError(''); setFormOpen(true); };

  const submit = async (e) => {
    e.preventDefault();
    if (!form.code.trim()) { setFormError('El código es obligatorio.'); return; }
    if (form.quantity === '' || Number(form.quantity) < 0) { setFormError('La cantidad debe ser un número mayor o igual a 0.'); return; }
    setSaving(true); setFormError('');
    const payload = { code: form.code.trim(), quantity: Number(form.quantity) };
    try {
      if (editing) {
        await StockAPI.update(token, editing.stockId, payload);
        setNotice({ type: 'success', text: `Fila de inventario “${payload.code}” actualizada.` });
      } else {
        await StockAPI.create(token, payload);
        setNotice({ type: 'success', text: `Fila de inventario “${payload.code}” creada.` });
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
      await StockAPI.remove(token, toDelete.stockId);
      setNotice({ type: 'success', text: `Fila “${toDelete.code}” eliminada del inventario.` });
      setToDelete(null);
      const nextPage = pageState.items.length === 1 && pageState.page > 0 ? pageState.page - 1 : pageState.page;
      await load(nextPage);
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo eliminar: ${err.message}` });
      setToDelete(null);
    } finally { setDeleting(false); }
  };

  const openView = async (row) => {
    setViewing({ loading: true });
    try { setViewing(await StockAPI.getById(token, row.stockId)); }
    catch (err) { setNotice({ type: 'error', text: `No se pudo obtener el detalle: ${err.message}` }); setViewing(null); }
  };

  const visible = useMemo(() => {
    if (!query.trim()) return pageState.items;
    const q = query.toLowerCase();
    return pageState.items.filter((s) => `${s.code}`.toLowerCase().includes(q));
  }, [pageState.items, query]);

  return (
    <div className="page fade-in">
      <PageTitle
        title="Inventario"
        subtitle="Filas de stock persistidas en PostgreSQL por ms-common-stock."
        actionLabel="Nueva fila"
        onAction={openCreate}
        searchValue={query}
        onSearch={setQuery}
        searchPlaceholder="Buscar por código..."
      />
      <Banner notice={notice} />

      <div className="panel">
        {loading ? <EmptyState text="Cargando inventario..." /> : visible.length === 0 ? (
          <EmptyState text="Sin registros de inventario todavía." />
        ) : (
          <table>
            <thead><tr><th>ID</th><th>Código</th><th>Cantidad</th><th>Estado</th><th></th></tr></thead>
            <tbody>
              {visible.map((s) => (
                <tr key={s.stockId}>
                  <td>{s.stockId}</td>
                  <td>{s.code}</td>
                  <td>{s.quantity}</td>
                  <td><span className={`pill ${s.inStock ? 'pill-ok' : 'pill-bad'}`}>{s.inStock ? 'En stock' : 'Agotado'}</span></td>
                  <td className="row-actions">
                    <button type="button" onClick={() => openView(s)} title="Ver detalle"><Eye size={16} /></button>
                    <button type="button" onClick={() => openEdit(s)} title="Editar"><Pencil size={16} /></button>
                    <button type="button" className="danger" onClick={() => setToDelete(s)} title="Eliminar"><Trash2 size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <Pagination page={pageState.page} totalPages={pageState.totalPages} totalElements={pageState.totalElements} onChange={load} />

      {formOpen && (
        <Modal
          title={editing ? `Editar fila #${editing.stockId}` : 'Nueva fila de inventario'}
          subtitle={editing ? 'PUT /update-stock-entry/{id} (merge parcial).' : 'POST /create-stock-entry (código único).'}
          onClose={() => setFormOpen(false)}
        >
          <form className="modal-form" onSubmit={submit}>
            <label>Código de producto</label>
            <input value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} placeholder="IPHONE-15-128" />
            <label>Cantidad</label>
            <input type="number" min="0" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} placeholder="50" />
            {formError && <div className="error-box small"><span>{formError}</span></div>}
            <div className="modal-actions">
              <button type="button" className="ghost-btn" onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className="primary-small" disabled={saving}>{saving ? 'Guardando...' : editing ? 'Guardar cambios' : 'Crear fila'}</button>
            </div>
          </form>
        </Modal>
      )}

      {viewing && (
        <Modal title="Detalle de inventario" subtitle="GET /get-stock-entry-by-id/{id}" onClose={() => setViewing(null)}>
          {viewing.loading ? <p className="muted">Cargando...</p> : (
            <div className="detail-grid">
              <div><span>Stock ID</span><strong>{viewing.stockId}</strong></div>
              <div><span>Código</span><strong>{viewing.code}</strong></div>
              <div><span>Cantidad</span><strong>{viewing.quantity}</strong></div>
              <div><span>Estado</span><strong>{viewing.inStock ? 'En stock' : 'Agotado'}</strong></div>
            </div>
          )}
        </Modal>
      )}

      {toDelete && (
        <ConfirmDialog
          title="Eliminar fila de inventario"
          message={`Se eliminará el registro de “${toDelete.code}”. Esta acción no se puede deshacer.`}
          onCancel={() => setToDelete(null)}
          onConfirm={confirmDelete}
          loading={deleting}
        />
      )}
    </div>
  );
}
