package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.service.AiPipelineService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiPipelineService aiPipelineService;

    @PostMapping("/analyze")
    public AiResponseDto analyze(@RequestParam("object_id") String objectId) {
        return aiPipelineService.analyzeArticle(new ObjectId(objectId));
    }
}
