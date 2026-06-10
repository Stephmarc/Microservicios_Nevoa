CREATE TABLE IF NOT EXISTS proyectos (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    nombre varchar(150) not null,
    descripcion varchar(600) null,
    progreso int not null,
    total_tareas int not null,
    tareas_completadas int not null,
    estado varchar(30) not null,
    creado_en datetime(6) not null,
    actualizado_en datetime(6) null,
    primary key (id)
);
