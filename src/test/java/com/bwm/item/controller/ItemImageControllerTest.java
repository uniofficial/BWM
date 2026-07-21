package com.bwm.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.item.dto.response.ItemImageResponse;
import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(ItemImageController.class)
@Import(SecurityConfig.class)
public class ItemImageControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("이미지 업로드 성공 시 200 OK를 반환한다.")
    void uploadImages_success() throws Exception {
        // given
        Integer itemId = 1;
        MockMultipartFile file = new MockMultipartFile(
                "images",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        ItemImageResponse responseDto = new ItemImageResponse(1, 1, "/images/items/1/test.png", true, LocalDateTime.now());
        given(itemImageService.uploadItemImages(eq(itemId), eq("uuid-1234"), any(List.class), eq(0)))
                .willReturn(List.of(responseDto));

        // when & then
        mockMvc.perform(multipart(HttpMethod.POST, "/api/items/{itemId}/images", itemId)
                .file(file)
                .param("representativeIndex", "0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].imageUrl").value("/images/items/1/test.png"));
    }
}
