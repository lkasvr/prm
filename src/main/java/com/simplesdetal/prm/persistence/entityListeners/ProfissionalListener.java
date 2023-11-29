package com.simplesdetal.prm.persistence.entityListeners;

import com.simplesdetal.prm.persistence.model.Profissional;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class ProfissionalListener {

    @PrePersist
    void setActive(Profissional profissional) {
        if (profissional.getActive() == null) profissional.setActive(1);
    }

}
