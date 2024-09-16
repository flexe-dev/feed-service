package com.flexe.feedservice.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HttpService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Value("${INTERACTION_SERVICE_URL}")
    private String INTERACTION_SERVICE_URL;

    @Value("${POST_SERVICE_URL}")
    private String POST_SERVICE_URL;

    public enum TargetController{
        POST,
        NODE
    }

    public String getServerURL(TargetController controller){
        return switch(controller){
            case POST -> POST_SERVICE_URL;
            case NODE -> INTERACTION_SERVICE_URL;
        };
    }

    public WebClient generateWebClient(TargetController server){
        return webClientBuilder.baseUrl(getServerURL(server)).build();
    }

    public <T>ResponseEntity<T> get(WebClient client, String uri, Class<T> responseType){
        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public <T> ResponseEntity<T> post(WebClient client, String uri, Object body, Class<T> responseType){
        return client.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public <T> ResponseEntity<T> post(WebClient client, String uri, Class<T> responseType){
        return client.put()
                .uri(uri)
                .retrieve()
                .toEntity(responseType)
                .block();
    }
}
