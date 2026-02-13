package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "파일 관련 API")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    //이미지 단순 조회(프로필, 메시지 내 파일 단일 모두 가능)
    @RequestMapping(value = "{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @PathVariable UUID binaryContentId
    ){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    //다건조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ){
        return ResponseEntity.ok(binaryContentIds.stream()
                .map(binaryContentService::find)
                .toList());
    }
}
