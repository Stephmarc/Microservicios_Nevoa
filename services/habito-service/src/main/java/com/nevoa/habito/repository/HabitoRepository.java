package com.nevoa.habito.repository;

import com.nevoa.habito.domain.Habito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabitoRepository extends JpaRepository<Habito, Long> {
    List<Habito> findByUsuarioId(Long usuarioId);
    Optional<Habito> findByIdAndUsuarioId(Long id, Long usuarioId);
}
