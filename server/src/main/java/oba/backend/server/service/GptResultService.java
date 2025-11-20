package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.gpt.GptResult;
import oba.backend.server.repository.GptResultRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GptResultService {

    private final GptResultRepository gptResultRepository;

    public GptResult getLatestGptResult() {
        return gptResultRepository.findTopByOrderByCreatedAtDesc();
    }
}
