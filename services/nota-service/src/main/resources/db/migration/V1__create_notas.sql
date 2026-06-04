CREATE TABLE IF NOT EXISTS notas (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    titulo varchar(150) not null,
    categoria varchar(80) null,
    contenido text not null,
    creada_en datetime(6) not null,
    actualizada_en datetime(6) null,
    primary key (id)
);
