package org.example.generatecodesum.Client;

import com.example.intelligentengineeringassistant.model.File;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final String token;
    private final ObjectMapper objectMapper;

    public GitHubClient(RestTemplate restTemplate, @Value("${github.token}") String token, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.token = token;
        this.objectMapper = objectMapper;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        return headers;
    }

    public String getRepositoryInfo(String owner, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s", owner, repo);

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    public List<File> getRepositoryContents(String owner, String repo, String path) throws Exception {
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return objectMapper.readValue(jsonNode.toString(), new TypeReference<List<File>>() {});
    }

    public byte[] downloadFile(String owner, String repo, String path) {
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        HttpHeaders headers = createHeaders(); // 不再设置 Accept 头部
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return response.getBody();
    }

}

