import { useEffect, useState } from 'react';
import { Pencil, Trash2, Eye, Users } from 'lucide-react';
import { CustomersAPI, DOCUMENT_TYPES, toPageState, formatDate } from '../lib/api.js';
import { Modal, ConfirmDialog, Pagination, Banner, EmptyState } from '../components/Common.jsx';
import { PageTitle } from '../components/PageTitle.jsx';

const emptyForm = { fullName: '', document: '', documentType: 'CC', email: '', phoneNumber: '', notes: '' };

export default function Customers({ token }) {
  const [pageState, setPageState] = useState({ items: [], page: 0, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);
  const [search, setSearch] = useState('');

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const [viewing, setViewing] = useState(null);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async (page = pageState.page, name = search) => {
    setLoading(true);
    try {
      const data = await CustomersAPI.list(token, { page, size: 8, name });
      setPageState(toPageState(data));
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo cargar los clientes: ${err.message}` });
    } finally { setLoading(false); }
  };

  useEffect(() => { load(0, ''); /* eslint-disable-next-line */ }, []);

  // La búsqueda filtra en el servidor (parámetro `name`) con un pequeño debounce.
  useEffect(() => {
    const t = setTimeout(() => load(0, search), 350);
    return () => clearTimeout(t);
    // eslint-disable-next-line
  }, [search]);

  const openCreate = () => { setEditing(null); setForm(emptyForm); setFormError(''); setFormOpen(true); };
  const openEdit = (c) => {
    setEditing(c);
    setForm({
      fullName: c.fullName || '', document: c.document || '', documentType: c.documentType || 'CC',
      email: c.email || '', phoneNumber: c.phoneNumber || '', notes: c.notes || '',
    });
    setFormError(''); setFormOpen(true);
  };

  const submit = async (e) => {
    e.preventDefault();
    if (!form.fullName.trim() || !form.document.trim()) { setFormError('Nombre y documento son obligatorios.'); return; }
    setSaving(true); setFormError('');
    try {
      const payload = { ...form, fullName: form.fullName.trim(), document: form.document.trim() };
      if (editing) {
        await CustomersAPI.update(token, editing.customerId, payload);
        setNotice({ type: 'success', text: `Cliente ${payload.fullName} actualizado.` });
      } else {
        await CustomersAPI.create(token, payload);
        setNotice({ type: 'success', text: `Cliente ${payload.fullName} registrado.` });
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
      await CustomersAPI.remove(token, toDelete.customerId);
      setNotice({ type: 'success', text: `Cliente ${toDelete.fullName} eliminado.` });
      setToDelete(null);
      const nextPage = pageState.items.length === 1 && pageState.page > 0 ? pageState.page - 1 : pageState.page;
      await load(nextPage);
    } catch (err) {
      setNotice({ type: 'error', text: `No se pudo eliminar: ${err.message}` });
      setToDelete(null);
    } finally { setDeleting(false); }
  };

  const openView = async (c) => {
    setViewing({ loading: true });
    try { setViewing(await CustomersAPI.getById(token, c.customerId)); }
    catch (err) { setNotice({ type: 'error', text: `No se pudo obtener el detalle: ${err.message}` }); setViewing(null); }
  };

  return (
    <div className="page fade-in">
      <PageTitle
        title="Clientes"
        subtitle="Clientes de negocio (sin login) gestionados por ms-common-person."
        actionLabel="Nuevo cliente"
        onAction={openCreate}
        searchValue={search}
        onSearch={setSearch}
        searchPlaceholder="Buscar por nombre..."
      />
      <Banner notice={notice} />

      <div className="panel">
        {loading ? <EmptyState text="Cargando clientes..." /> : pageState.items.length === 0 ? (
          <EmptyState text="Sin clientes registrados todavía." />
        ) : (
          <table>
            <thead><tr><th>Nombre</th><th>Documento</th><th>Email</th><th>Teléfono</th><th></th></tr></thead>
            <tbody>
              {pageState.items.map((c) => (
                <tr key={c.customerId}>
                  <td>{c.fullName}</td>
                  <td>{c.documentType} {c.document}</td>
                  <td>{c.email || '—'}</td>
                  <td>{c.phoneNumber || '—'}</td>
                  <td className="row-actions">
                    <button type="button" onClick={() => openView(c)} title="Ver detalle"><Eye size={16} /></button>
                    <button type="button" onClick={() => openEdit(c)} title="Editar"><Pencil size={16} /></button>
                    <button type="button" className="danger" onClick={() => setToDelete(c)} title="Eliminar"><Trash2 size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <Pagination page={pageState.page} totalPages={pageState.totalPages} totalElements={pageState.totalElements} onChange={(p) => load(p, search)} />

      {formOpen && (
        <Modal
          title={editing ? `Editar ${editing.fullName}` : 'Nuevo cliente'}
          subtitle={editing ? 'PUT /update-customer/{id}' : 'POST /create-customer'}
          onClose={() => setFormOpen(false)}
        >
          <form className="modal-form" onSubmit={submit}>
            <label>Nombre completo</label>
            <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} placeholder="Juan Pérez García" />
            <div className="form-row">
              <div>
                <label>Tipo de documento</label>
                <select value={form.documentType} onChange={(e) => setForm({ ...form, documentType: e.target.value })}>
                  {DOCUMENT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
              <div>
                <label>Documento</label>
                <input value={form.document} onChange={(e) => setForm({ ...form, document: e.target.value })} placeholder="9876543210" />
              </div>
            </div>
            <div className="form-row">
              <div>
                <label>Email</label>
                <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="juan.perez@example.com" />
              </div>
              <div>
                <label>Teléfono</label>
                <input value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} placeholder="3001234567" />
              </div>
            </div>
            <label>Notas</label>
            <textarea rows={2} value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} placeholder="Cliente preferencial" />
            {formError && <div className="error-box small"><span>{formError}</span></div>}
            <div className="modal-actions">
              <button type="button" className="ghost-btn" onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className="primary-small" disabled={saving}>{saving ? 'Guardando...' : editing ? 'Guardar cambios' : 'Registrar cliente'}</button>
            </div>
          </form>
        </Modal>
      )}

      {viewing && (
        <Modal title="Detalle del cliente" subtitle="GET /get-customer-by-id/{id}" onClose={() => setViewing(null)}>
          {viewing.loading ? <p className="muted">Cargando...</p> : (
            <div className="detail-grid">
              <div><span>Customer ID</span><strong>{viewing.customerId}</strong></div>
              <div><span>Nombre</span><strong>{viewing.fullName}</strong></div>
              <div><span>Documento</span><strong>{viewing.documentType} {viewing.document}</strong></div>
              <div><span>Email</span><strong>{viewing.email || '—'}</strong></div>
              <div><span>Teléfono</span><strong>{viewing.phoneNumber || '—'}</strong></div>
              <div><span>Registrado</span><strong>{formatDate(viewing.createdAt)}</strong></div>
              <div className="detail-full"><span>Notas</span><strong>{viewing.notes || '—'}</strong></div>
            </div>
          )}
        </Modal>
      )}

      {toDelete && (
        <ConfirmDialog
          title="Eliminar cliente"
          message={`Se eliminará a “${toDelete.fullName}” del registro de clientes.`}
          onCancel={() => setToDelete(null)}
          onConfirm={confirmDelete}
          loading={deleting}
        />
      )}
    </div>
  );
}

export const CustomersIcon = Users;
