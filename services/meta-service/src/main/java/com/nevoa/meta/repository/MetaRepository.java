package com.nevoa.meta.repository;

import com.nevoa.meta.domain.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByUsuarioId(Long usuarioId);
    Optional<Meta> findByIdAndUsuarioId(Long id, Long usuarioId);
}
