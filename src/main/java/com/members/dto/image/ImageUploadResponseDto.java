package com.members.dto.image;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadResponseDto {
    private String url;
    private String publicId;
    private String format;
    private Long size;
    private Integer width;
    private Integer height;
}
