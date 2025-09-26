package com.example.planlekcji.ckziu_elektryk.client.replacements;

import java.util.Date;
import java.util.List;

public interface ReplacementService {

    List<Replacement> getReplacements(ReplacementType replacementType, Date date);

    default List<Replacement> getLatestReplacements(ReplacementType replacementType) {
        return this.getReplacements(replacementType, new Date());
    }

    default List<Replacement> getLatestReplacements() {
        return this.getLatestReplacements(ReplacementType.TEACHERS);
    }
}
