package no.nav.pam.annonsemottak.migration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MigrationService {

    private final Logger LOG = LoggerFactory.getLogger(MigrationService.class);

    @Autowired
    private StillingRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${migrate.url}")
    private String url;

    public void migrateStilling(LocalDateTime startDate) throws Exception {
        LocalDateTime updatedSince = LocalDateTime.now().minusYears(10);
        if (startDate!=null) {
            updatedSince = startDate;
        }
        String feedUrl=url+"/api/stillinger/feed?updatedSince="+updatedSince.toString()+"&size=1000&sort=updated,asc";
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(FeedTransport.class, new Class[]{StillingFeedItem.class});
        String content = restTemplate.getForObject(feedUrl,String.class);
        FeedTransport<StillingFeedItem> feedTransport = objectMapper.readValue(content,javaType);
        List<Stilling> stillingList = feedTransport.content.stream().map(StillingFeedItemMapper::toStilling)
                .collect(Collectors.toList());
        LocalDateTime lastUpdatedDate = updatedSince;
        if (!stillingList.isEmpty()) {
            stillingList.sort(Comparator.comparing(Stilling::getUpdated));
            lastUpdatedDate = stillingList.get(stillingList.size() - 1).getUpdated();
        }
        while (!stillingList.isEmpty() && updatedSince.isBefore(lastUpdatedDate)) {
            LOG.info("saving {}  stillinger and updatedsince {} ", stillingList.size(), updatedSince);
            repository.saveAll(stillingList);
            updatedSince = lastUpdatedDate;
            feedUrl=url+"/api/stillinger/feed?updatedSince="+updatedSince.toString()+"&size=1000&sort=updated,asc";
            LOG.info("start fetching {}",feedUrl);
            feedTransport = objectMapper.readValue(restTemplate.getForObject(feedUrl,String.class),javaType);
            stillingList = feedTransport.content.stream().map(StillingFeedItemMapper::toStilling).collect(Collectors.toList());
            stillingList.sort(Comparator.comparing(Stilling::getUpdated));
            lastUpdatedDate = stillingList.get(stillingList.size() - 1).getUpdated();
        }
        LOG.info("LastUpdateTime was: {}", lastUpdatedDate);
    }
}
