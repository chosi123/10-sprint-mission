package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary ="단일 이미지 조회",
            operationId = "find"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = BinaryContent.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "BinaryContent with id {binaryContentId} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @PathVariable UUID binaryContentId
    ){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    //다건조회
    @Operation(
            summary ="다수 이미지 조회",
            operationId = "findAllByIdIn"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ){
        return ResponseEntity.ok(binaryContentIds.stream()
                .map(binaryContentService::find)
                .toList());
    }
}
