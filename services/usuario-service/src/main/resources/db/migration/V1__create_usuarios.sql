CREATE TABLE IF NOT EXISTS usuarios (
    id bigint not null auto_increment,
    nombre_completo varchar(120) not null,
    email varchar(120) not null unique,
    password_hash varchar(255) not null,
    activo bit(1) not null,
    fecha_registro datetime(6) not null,
    primary key (id)
);
