package http2demo;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
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
        public String get() throws ServletException, IOException {
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

    @Bean
	WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServletCustomizer() {
		return tomcat -> {
			//get the upgrade protocol from connector
			tomcat.addConnectorCustomizers(connector -> {
				for(UpgradeProtocol proto : connector.findUpgradeProtocols()) {
					if (proto instanceof Http2Protocol h2) {
						h2.setOverheadContinuationThreshold(16384);
						h2.setOverheadDataThreshold(16384);
						h2.setOverheadWindowUpdateThreshold(16384);
					}
				}
			});
		};
	}
}
