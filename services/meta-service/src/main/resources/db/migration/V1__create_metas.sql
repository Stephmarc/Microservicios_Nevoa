CREATE TABLE IF NOT EXISTS metas (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    titulo varchar(150) not null,
    descripcion varchar(600) null,
    categoria varchar(30) not null,
    progreso int not null,
    fecha_limite date null,
    estado varchar(30) not null,
    creada_en datetime(6) not null,
    actualizada_en datetime(6) null,
    primary key (id)
);
