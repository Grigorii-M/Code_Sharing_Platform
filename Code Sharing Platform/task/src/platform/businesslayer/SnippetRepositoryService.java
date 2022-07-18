package platform.businesslayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.persistencelayer.SnippetRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SnippetRepositoryService {

    private final SnippetRepository snippetRepository;

    @Autowired
    public SnippetRepositoryService(SnippetRepository repository) {
        snippetRepository = repository;
    }

    public Snippet save(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    public List<Snippet> find10LatestUnrestrictedSnippets() {
        return StreamSupport
                .stream(snippetRepository
                        .findTop10ByHasAccessDeadlineFalseAndHasViewsThresholdFalseOrderByIdDesc()
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    public Snippet findByUuid(UUID uuid) throws  NoSuchElementException {
        return snippetRepository.findByUuid(uuid).orElseThrow();
    }

    @Transactional
    public void deleteByUuid(UUID uuid) {
        snippetRepository.deleteByUuid(uuid);
    }
}
