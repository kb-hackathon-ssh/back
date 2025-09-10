package kb.hackathon.ssh.domain.atm.client;

import kb.hackathon.ssh.domain.atm.client.dto.KakaoLocalSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.time.Duration;

@Component
public class KakaoLocalClient {

    private final WebClient webClient;

    public KakaoLocalClient(
            @Value("${kakao.rest.api.key}") String apiKey,
            @Value("${kakao.local.base-url:https://dapi.kakao.com}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.create()
                .compress(true)
                .responseTimeout(Duration.ofSeconds(4))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(4))
                        .addHandlerLast(new WriteTimeoutHandler(4)));

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public KakaoLocalSearchResponse searchBanks(double lat, double lng, int radius, int page, int size) {
        String path = UriComponentsBuilder
                .fromPath("/v2/local/search/category.json")
                .queryParam("category_group_code", "BK9")
                .queryParam("y", lat)
                .queryParam("x", lng)
                .queryParam("radius", Math.min(Math.max(radius, 0), 20000))
                .queryParam("sort", "distance")
                .queryParam("page", Math.max(page, 1))
                .queryParam("size", Math.min(Math.max(size, 1), 15))
                .toUriString();

        return webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(KakaoLocalSearchResponse.class)
                .block(Duration.ofSeconds(5));
    }
}