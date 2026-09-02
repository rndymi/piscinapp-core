package com.rndymi.es.piscinapp.core.planning.application;

import java.util.UUID;

public interface VisitLookup {

    VisitReference requireVisit(
            UUID visitId
    );
}
