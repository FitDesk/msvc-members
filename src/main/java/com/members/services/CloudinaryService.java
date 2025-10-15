package com.members.services;

import com.members.dto.image.ImageUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CloudinaryService {
    ImageUploadResponseDto uploadProfileImage(MultipartFile file, UUID userId);
    boolean deleteImage(String publicId);
    ImageUploadResponseDto updateProfileImage(MultipartFile file,UUID userId,String oldPublicId);
    String extractPublicIdFromUrl(String imageUrl);
}
