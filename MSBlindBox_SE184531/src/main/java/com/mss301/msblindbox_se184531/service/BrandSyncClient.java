package com.mss301.msblindbox_se184531.service;

import com.mss301.msblindbox_se184531.entity.BlindBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BrandSyncClient {

    private final RestTemplate restTemplate;
    private final String msbrandServiceUrl;

    public BrandSyncClient(RestTemplate restTemplate,
            @Value("${msbrand.service.url}") String msbrandServiceUrl) {
        this.restTemplate = restTemplate;
        this.msbrandServiceUrl = msbrandServiceUrl;
    }

    public void createBlindBox(BlindBox blindBox) {
        restTemplate.postForObject(msbrandServiceUrl + "/api/internal/blindboxes", blindBox, String.class);
    }

    public void updateBlindBox(Integer id, BlindBox blindBox) {
        restTemplate.put(msbrandServiceUrl + "/api/internal/blindboxes/" + id, blindBox);
    }

    public void deleteBlindBox(Integer id) {
        restTemplate.delete(msbrandServiceUrl + "/api/internal/blindboxes/" + id);
    }
}
