package com.nevoa.tarea.repository;

import com.nevoa.tarea.domain.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByUsuarioId(Long usuarioId);
    Optional<Tarea> findByIdAndUsuarioId(Long id, Long usuarioId);
}
