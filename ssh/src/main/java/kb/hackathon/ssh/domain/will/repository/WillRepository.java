package kb.hackathon.ssh.domain.will.repository;

import kb.hackathon.ssh.domain.will.entity.Will;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WillRepository extends JpaRepository<Will,Long> {

    List<Will> findByUserId(Long userId);

    Optional<Will> findByWillIdAndUserId(Long willId, Long userId);

    boolean existsByWillIdAndUserId(Long willId,Long userId);

}
