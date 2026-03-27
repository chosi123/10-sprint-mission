package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.exception.binarycontent.FileDownloadException;
import com.sprint.mission.discodeit.exception.binarycontent.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {
    @Value("${discodeit.storage.local.root-path}")
    private Path root;

    @PostConstruct
    private void init(){
        try{
            if(Files.notExists(root)){
                Files.createDirectories(root);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Path resolvePath(UUID id){
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        log.info("파일 업로드 작업을 시작합니다.");
        try{
            Path path = resolvePath(id);
            try(FileOutputStream outputStream = new FileOutputStream(path.toFile())){
                outputStream.write(bytes);
            }
            log.info("파일 업로드 작업이 완료되었습니다.");
            return id;
        }catch (Exception e){
            throw new FileStorageException();
        }
    }

    @Override
    public InputStream get(UUID id) {
        try{
            return new FileInputStream(resolvePath(id).toFile());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentResponseDto binaryContentResponseDto) {
        try{
            log.info("파일 다운로드 작업을 시작합니다.");
            Resource resource = new FileSystemResource(resolvePath(binaryContentResponseDto.id()));

            MediaType contentType = MediaType.parseMediaType(binaryContentResponseDto.contentType());

            log.info("파일 다운로드 작업이 완료되었습니다.");

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .cacheControl(CacheControl.noCache())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentResponseDto.fileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new FileDownloadException();
        }
    }
}
