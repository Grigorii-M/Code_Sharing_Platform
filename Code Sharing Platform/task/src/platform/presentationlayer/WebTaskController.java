package platform.presentationlayer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import platform.businesslayer.Snippet;
import platform.businesslayer.SnippetRepositoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/code")
public class WebTaskController {

    final SnippetRepositoryService snippetRepositoryService;

    public WebTaskController(SnippetRepositoryService snippetRepositoryService) {
        this.snippetRepositoryService = snippetRepositoryService;
    }

    @GetMapping("/{uuid}")
    public String getSnippetByIndex(@PathVariable String uuid, Model model) {
        try {
            Snippet snippet = snippetRepositoryService.findByUuid(UUID.fromString(uuid));

            if (snippet.isHasAccessDeadline() && snippet.getAccessDeadline().isBefore(LocalDateTime.now())
                    || snippet.isHasViewsThreshold() && snippet.getViewThreshold() == snippet.getTimesViewed()
            ) {
                System.err.println("ready to remove");
                snippetRepositoryService.deleteByUuid(UUID.fromString(uuid));
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find snippet with the given UUID");
            }

            snippet.setTimesViewed(snippet.getTimesViewed() + 1);
            snippetRepositoryService.save(snippet);

            model.addAttribute("snippet", snippet);
            return "snippet";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find snippet with the given UUID");
        }
    }

    @GetMapping("/latest")
    public String getLatestSnippets(Model model) {
        List<Snippet> snippets = snippetRepositoryService.find10LatestUnrestrictedSnippets();
        model.addAttribute("snippets", snippets);

        return "recentSnippets";
    }

    @GetMapping("/new")
    public String getNewCodeForm() {
        return "createNewSnippet";
    }
}
