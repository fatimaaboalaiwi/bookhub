package com.company.bookhub.integration.numverify.client;

import com.company.bookhub.common.exception.NumverifyException;
import com.company.bookhub.integration.numverify.dto.NumverifyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NumverifyClient {

    private final RestClient restClient;
    private final String apiKey;

    public NumverifyClient(
            RestClient restClient,
            @Value("${numverify.api-key}") String apiKey) {

        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    public NumverifyResponse validatePhoneNumber(String number) {

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/number_verification/validate")
                            .queryParam("number", number)
                            .build())
                    .header("apikey", apiKey)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, response) -> {
                                throw new NumverifyException(
                                        "phone.validation_failed"
                                );
                            }
                    )
                    .body(NumverifyResponse.class);

        } catch (NumverifyException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new NumverifyException(
                    "phone.service_unavailable",
                    ex
            );
        }
    }
}