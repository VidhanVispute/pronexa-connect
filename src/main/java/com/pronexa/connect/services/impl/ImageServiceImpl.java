package com.pronexa.connect.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.pronexa.connect.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary; // 2️⃣ Cloudinary instance for handling image uploads

     // 3️⃣ Constructor-based dependency injection of Cloudinary instance
    public ImageServiceImpl(Cloudinary cloudinary) { 
        this.cloudinary = cloudinary;
    }

     // 4️⃣ Method to upload an image file to Cloudinary
    @Override
    public String uploadImage(MultipartFile contactImage, String filename) {
        try {
            // Upload image bytes to cloud
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    contactImage.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", filename, // 7️⃣ Set a custom filename (public ID) in Cloudinary
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            // 10️⃣ Return the secure URL of the uploaded image
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(300)   // example default width
                                .height(300)  // example default height
                                .crop("fill") // crop type
                )
                .generate(publicId); // 17️⃣ Generate URL for the given public ID
    }
}
