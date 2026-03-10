package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.response.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BasicBinaryContentServiceTest {

    @Autowired
    BasicBinaryContentService binaryContentService;

    @Autowired
    BinaryContentRepository binaryContentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("BinaryContent 생성 성공")
    void createBinaryContent_success() {
        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "bytes-data.png", "image/png",
                        "bytes-data".getBytes())
        );

        UUID binaryContentId = binaryContentService.createBinaryContent(request);
        flushAndClear();

        Optional<BinaryContent> saved = binaryContentRepository.findById(binaryContentId);
        assertThat(saved).isPresent();
    }

    @Test
    @DisplayName("BinaryContent 단건 조회 성공")
    void findBinaryContent_success() {
        byte[] image = "hello".getBytes();

        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "hello.png", "image/png", image)
        );

        UUID id = binaryContentService.createBinaryContent(request);
        flushAndClear();

        BinaryContentDto response = binaryContentService.findBinaryContent(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.fileName()).isEqualTo("hello.png");
        assertThat(response.size()).isEqualTo((long) image.length);
        assertThat(response.contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("존재하지 않는 BinaryContent 조회시 예외 발생")
    void findBinaryContent_fail_notFound() {
        assertThatThrownBy(() -> binaryContentService.findBinaryContent(UUID.randomUUID()))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("여러 BinaryContent 조회 성공")
    void findAllByIdIn_success() {
        UUID id1 = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "a.png", "image/png", "a".getBytes())
                )
        );

        UUID id2 = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "b.png", "image/png", "b".getBytes())
                )
        );

        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BinaryContentDto::id).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    @DisplayName("BinaryContent 삭제 성공")
    void deleteBinaryContent_success() {
        UUID id = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "delete.png", "image/png",
                                "delete".getBytes())
                )
        );

        binaryContentService.deleteBinaryContent(id);
        flushAndClear();

        assertThat(binaryContentRepository.findById(id)).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
