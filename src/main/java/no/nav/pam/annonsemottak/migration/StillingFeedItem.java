package no.nav.pam.annonsemottak.migration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StillingFeedItem {
    public long id;
    public LocalDateTime created;
    public String createdBy;
    public String createdByDisplayName;

    public LocalDateTime updated;
    public String updatedBy;
    public String updatedByDisplayName;
    public String uuid;
    public String kilde;
    public String medium;
    public String hash;
    public String url;
    public String externalId;
    @JsonProperty("new")
    public boolean isNew;

    public LocalDateTime published;

    public AnnonseStatus annonseStatus;

    public LocalDateTime expires;

    public LocalDateTime systemModifiedDate;

    public String arbeidsgiver;
    public String stillingstittel;
    public String annonsetekst;
    public String arbeidsgiveromtale;
    public String arbeidssted;
    public String soeknadsfrist;

    //Saksbehandling
    public String saksbehandler;
    public Status status;
    public String merknader;
    public String kommentarer;

    public Map<String, String> properties = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StillingFeedItem that = (StillingFeedItem) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
