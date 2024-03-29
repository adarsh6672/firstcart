package com.firstcart_ecommerce.firstcart.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.firstcart_ecommerce.firstcart.model.ProductImage;
import com.firstcart_ecommerce.firstcart.repository.ProductImageRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Value("${bucketName}")
    private String bucketName;

    private ProductImageRepo productImageRepo;

    private final AmazonS3 s3;

    public S3Service(AmazonS3 s3){
        this.s3=s3;
    }
    public String saveFile(MultipartFile file){

        String originalFilename=file.getOriginalFilename();


        try {
           File file1= convertMultiPartToFile(file);
            PutObjectResult putObjectResult=s3.putObject(bucketName,originalFilename,file1);
            return putObjectResult.getContentMd5();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public byte[] downloadFile(String filename){
        S3Object object=s3.getObject(bucketName,filename);
        S3ObjectInputStream objectContent=object.getObjectContent();
        try {
            return IOUtils.toByteArray(objectContent);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String deleteFile(String filename){
        s3.deleteObject(bucketName,filename);
        return "file deleted";
    }

    public List<ProductImage> getAllImages() {
        return productImageRepo.findAll();
    }

    public String getImageUrl(String fileName) {
        return s3.getUrl(bucketName, fileName).toString();
    }

    public List<String> listAllFiles(){
        ListObjectsV2Result listObjectsV2Result=s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(o->o.getKey()).collect(Collectors.toList());
    }
    private File convertMultiPartToFile(MultipartFile file)throws IOException{
        File convFile=new File(file.getOriginalFilename());
        FileOutputStream fos=new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
