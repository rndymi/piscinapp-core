package com.rndymi.es.piscinapp.core.crews.application;

import java.util.UUID;

public interface CrewLookup {

    CrewReference requireCrew(
            UUID crewId
    );
}
