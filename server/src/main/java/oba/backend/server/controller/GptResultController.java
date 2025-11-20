package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.gpt.GptResult;
import oba.backend.server.service.GptResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gpt")
@RequiredArgsConstructor
public class GptResultController {

    private final GptResultService gptResultService;

    @GetMapping("/latest")
    public ResponseEntity<GptResult> getLatestReport() {
        GptResult result = gptResultService.getLatestGptResult();

        if (result == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }
}
