package com.rndymi.es.piscinapp.core.incidents.application;

import java.util.List;
import java.util.UUID;

public interface IncidentLookup {

    List<IncidentReference> findIncidentsByVisitId(
            UUID visitId
    );
}
