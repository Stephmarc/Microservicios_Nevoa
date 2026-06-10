package com.nevoa.pomodoro.repository;

import com.nevoa.pomodoro.domain.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {
    List<Pomodoro> findByUsuarioId(Long usuarioId);
    Optional<Pomodoro> findByIdAndUsuarioId(Long id, Long usuarioId);
}
