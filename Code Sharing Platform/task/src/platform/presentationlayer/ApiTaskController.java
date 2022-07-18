package platform.presentationlayer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import platform.businesslayer.Snippet;
import platform.businesslayer.SnippetRepositoryService;

import java.time.LocalDateTime;
import java.util.*;

@RestController()
@RequestMapping("/api/code")
public class ApiTaskController {

    final SnippetRepositoryService snippetRepositoryService;

    public ApiTaskController(SnippetRepositoryService snippetRepositoryService) {
        this.snippetRepositoryService = snippetRepositoryService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Snippet.SnippetJsonRepresentation> getSnippetByIndex(@PathVariable String uuid) {
        Snippet snippet;
        try {
            snippet = snippetRepositoryService.findByUuid(UUID.fromString(uuid));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        if (snippet.isHasAccessDeadline() && snippet.getAccessDeadline().isBefore(LocalDateTime.now())
                || snippet.isHasViewsThreshold() && snippet.getViewThreshold() == snippet.getTimesViewed()
        ) {
            snippetRepositoryService.deleteByUuid(UUID.fromString(uuid));
            return ResponseEntity.notFound().build();
        }

        snippet.setTimesViewed(snippet.getTimesViewed() + 1);
        snippetRepositoryService.save(snippet);

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(snippet.getJsonRepresentation());

    }

    @GetMapping("/latest")
    public ResponseEntity<List<Snippet.SnippetJsonRepresentation>> getLatestSnippets() {
        List<Snippet> snippets = snippetRepositoryService.find10LatestUnrestrictedSnippets();
        List<Snippet.SnippetJsonRepresentation> responseList = new ArrayList<>();
        for (Snippet snippet : snippets) {
            responseList.add(snippet.getJsonRepresentation());
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(responseList);
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, String>> postCodeSnippet(@RequestBody Map<String, String> requestBody) {
        String uuidString = snippetRepositoryService.save(
                new Snippet(
                        requestBody.get("code"),
                        Integer.parseInt(requestBody.get("time")),
                        Integer.parseInt(requestBody.get("views"))
                )
        ).getUuid().toString();

        return ResponseEntity.ok().body(Map.of("id", uuidString));
    }
}
