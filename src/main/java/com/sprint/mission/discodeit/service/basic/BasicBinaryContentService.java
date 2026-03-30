package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    public final BinaryContentRepository binaryContentRepository;
    public final BinaryContentStorage binaryContentStorage;
    public final BinaryContentResponseMapper binaryContentResponseMapper;

    //어차피 안 쓰여서 임시로 처리함.
    @Override
    public BinaryContentResponseDto create(byte[] content) {
        BinaryContent binaryContent = binaryContentRepository.save(new BinaryContent("", "", 0L));

        return binaryContentResponseMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException("BinaryContent with id " + binaryContentId + " not found"));
        return binaryContentResponseMapper.toDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList) {
        List<BinaryContent> binaryContents = new ArrayList<>();

        idList.forEach(id -> binaryContents.add(binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException("BinaryContent not found"))));

        return binaryContents.stream()
                .map(b->binaryContentResponseMapper.toDto(b))
                .toList();

    }

    @Override
    public void delete(UUID binaryContentId) {
        binaryContentRepository.deleteById(binaryContentId);
    }
}
