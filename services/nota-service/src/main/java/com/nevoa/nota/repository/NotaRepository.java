package com.nevoa.nota.repository;

import com.nevoa.nota.domain.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByUsuarioId(Long usuarioId);
    Optional<Nota> findByIdAndUsuarioId(Long id, Long usuarioId);
}
