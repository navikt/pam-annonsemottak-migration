package no.nav.pam.annonsemottak.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class StillingFeedItemMapper {

    private static final Logger LOG = LoggerFactory.getLogger(StillingFeedItemMapper.class);


    public static Stilling toStilling(StillingFeedItem item) {
        Stilling stilling = new Stilling(
                item.id,
                item.stillingstittel,
                item.arbeidssted,
                item.arbeidsgiver,
                item.arbeidsgiveromtale,
                item.annonsetekst,
                item.soeknadsfrist,
                item.kilde,
                item.medium,
                item.url,
                item.externalId,
                item.expires,
                item.properties,
                item.systemModifiedDate,
                item.uuid
        );
        stilling.setCreated(item.created);
        stilling.setCreatedBy(item.createdBy);
        stilling.setCreatedByDisplayName(item.createdByDisplayName);
        stilling.setUpdated(item.updated);
        stilling.setUpdatedBy(item.updatedBy);
        stilling.setUpdatedByDisplayName(item.updatedByDisplayName);
        stilling.setPublished(item.published);
        if (item.annonseStatus == AnnonseStatus.INAKTIV) {
            stilling.deactivate();
        } else if (item.annonseStatus == AnnonseStatus.STOPPET) {
            stilling.stop();
        }

        Map<String, String> saksbehandlingMap = new HashMap<>();
        saksbehandlingMap.put(OppdaterSaksbehandlingCommand.STATUS, item.status.getKodeAsString());
        saksbehandlingMap.put(OppdaterSaksbehandlingCommand.KOMMENTARER, item.kommentarer);
        saksbehandlingMap.put(OppdaterSaksbehandlingCommand.SAKSBEHANDLER, item.saksbehandler);
        saksbehandlingMap.put(OppdaterSaksbehandlingCommand.MERKNADER, item.merknader);
        try {
            stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(saksbehandlingMap));
        } catch (IllegalSaksbehandlingCommandException e) {
            LOG.warn("Couldn't map saksbehandler from Feed");
        }

        return stilling;
    }
}
