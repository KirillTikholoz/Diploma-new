package org.example.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class ConnectionWithSearcherUtils {
    @Value("${urls.searcher.domain}")
    private String urlSearcherDomain;

    @Value("${urls.searcher.add-path}")
    private String pathSearcherAdd;

    @Value("${urls.searcher.search-path}")
    private String pathSearcherSearch;

    public ResponseEntity<HttpResponse> addDocumentInSearcher(Long id, MultipartFile multipartFile){
        try {
            String urlSearcher = urlSearcherDomain + pathSearcherAdd;

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(urlSearcher);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.addBinaryBody("file", multipartFile.getInputStream(), ContentType.DEFAULT_BINARY, multipartFile.getOriginalFilename());
            builder.addTextBody("id", id.toString(), ContentType.TEXT_PLAIN);

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            HttpResponse response = httpClient.execute(httpPost);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Cacheable(value = "searchCache")
    public ResponseEntity<String> searchRequest(String query){
        try {
            String urlSearcher = urlSearcherDomain + pathSearcherSearch;

            HttpClient httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(urlSearcher);

            builder.setParameter("query", query);
            HttpGet httpGet = new HttpGet(builder.build());

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
