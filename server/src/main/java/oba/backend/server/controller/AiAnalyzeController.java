package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.service.AiPipelineService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAnalyzeController {

    private final AiPipelineService aiPipelineService;

    @PostMapping("/analyze")
    public AiResponseDto analyze(@RequestParam("object_id") String id) {
        return aiPipelineService.analyzeArticle(new ObjectId(id));
    }
}
