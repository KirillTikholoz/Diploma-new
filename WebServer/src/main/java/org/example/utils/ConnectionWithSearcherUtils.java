package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.dtos.DocumentIdRequestDto;
import org.example.dtos.DocumentIdResponseDto;
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

    @Value("${urls.searcher.provide-path}")
    private String pathSearcherProvide;

    @Value("${urls.searcher.search-path.project}")
    private String pathSearcherSearchProjects;

    @Value("${urls.searcher.search-path.solution}")
    private String pathSearcherSearchSolutions;

    @Value("${urls.searcher.search-path.document}")
    private String pathSearcherSearchDocuments;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<String> addDocumentInSearcher(Long id, MultipartFile multipartFile){
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
            String responseBody = EntityUtils.toString(response.getEntity());

            return ResponseEntity.status(response.getStatusLine().getStatusCode()).body(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<String> addSolution(DocumentIdRequestDto documentIdRequestDto){
        try {
            String urlSearcher = urlSearcherDomain + pathSearcherProvide;

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(urlSearcher);
            httpPost.setHeader("Content-Type", "application/json");

            String json = new ObjectMapper().writeValueAsString(documentIdRequestDto);
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            return ResponseEntity.status(response.getStatusLine().getStatusCode()).body(responseBody);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @Cacheable(value = "searchCache")
    private ResponseEntity<DocumentIdResponseDto> searchRequest(String query, String urlSearcher){
        try {
            HttpClient httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(urlSearcher);

            builder.setParameter("query", query);
            HttpGet httpGet = new HttpGet(builder.build());

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());

            DocumentIdResponseDto documentIdResponseDto = objectMapper.readValue(responseBody, DocumentIdResponseDto.class);

            return ResponseEntity.status(HttpStatus.OK).body(documentIdResponseDto);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Cacheable(value = "searchProjectsCache")
    public ResponseEntity<DocumentIdResponseDto> searchProjectsRequest(String query){
        String urlSearcher = urlSearcherDomain + pathSearcherSearchProjects;
        return searchRequest(query, urlSearcher);
    }

    @Cacheable(value = "searchSolutionsCache")
    public ResponseEntity<DocumentIdResponseDto> searchSolutionsRequest(String query){
        String urlSearcher = urlSearcherDomain + pathSearcherSearchSolutions;
        return searchRequest(query, urlSearcher);
    }

    @Cacheable(value = "searchDocumentsCache")
    public ResponseEntity<DocumentIdResponseDto> searchDocumentsRequest(String query){
        String urlSearcher = urlSearcherDomain + pathSearcherSearchDocuments;
        return searchRequest(query, urlSearcher);
    }

}
