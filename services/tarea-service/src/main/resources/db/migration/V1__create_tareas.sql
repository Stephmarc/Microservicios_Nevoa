CREATE TABLE IF NOT EXISTS tareas (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    proyecto_id bigint null,
    titulo varchar(150) not null,
    descripcion varchar(500) null,
    prioridad varchar(20) not null,
    estado varchar(20) not null,
    fecha_limite date null,
    creada_en datetime(6) not null,
    actualizada_en datetime(6) null,
    primary key (id)
);
