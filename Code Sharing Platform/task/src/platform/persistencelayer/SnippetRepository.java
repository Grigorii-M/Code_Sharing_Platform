package platform.persistencelayer;

import org.springframework.data.repository.CrudRepository;
import platform.businesslayer.Snippet;

import java.util.Optional;
import java.util.UUID;

public interface SnippetRepository extends CrudRepository<Snippet, Long> {
    Iterable<Snippet> findTop10ByHasAccessDeadlineFalseAndHasViewsThresholdFalseOrderByIdDesc();
    Optional<Snippet> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
