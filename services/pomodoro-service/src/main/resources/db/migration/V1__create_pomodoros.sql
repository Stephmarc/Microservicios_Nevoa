CREATE TABLE IF NOT EXISTS pomodoros (
    id bigint not null auto_increment,
    usuario_id bigint not null,
    minutos_trabajo int not null,
    minutos_descanso int not null,
    estado varchar(30) not null,
    inicio datetime(6) null,
    fin datetime(6) null,
    creado_en datetime(6) not null,
    primary key (id)
);
