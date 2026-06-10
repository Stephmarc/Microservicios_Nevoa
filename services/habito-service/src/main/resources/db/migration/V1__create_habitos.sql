CREATE TABLE IF NOT EXISTS habitos (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    nombre varchar(150) not null,
    racha_actual int not null,
    dias_completados int not null,
    cumplido_hoy bit(1) not null,
    creado_en datetime(6) not null,
    actualizado_en datetime(6) null,
    primary key (id)
);
