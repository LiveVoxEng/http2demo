package http2demo;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RestController
    @RequestMapping("/")
    public static class IndexController {

        @GetMapping
        public String post() throws ServletException, IOException {
            return "Ok\n";
        }

    }

    @RestController
    @RequestMapping("/upload")
    public static class FileUploadController {

        @PostMapping
        public String post(
            @RequestParam("file") MultipartFile file
        ) throws ServletException, IOException {
            IOUtils.copyLarge(file.getInputStream(), OutputStream.nullOutputStream());
            return "Received file named " + file.getOriginalFilename() + " of size " + file.getSize() + "\n";
        }

    }

}
