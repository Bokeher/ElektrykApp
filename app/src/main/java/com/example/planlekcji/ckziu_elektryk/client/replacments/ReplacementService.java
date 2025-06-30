package com.example.planlekcji.ckziu_elektryk.client.replacments;

import java.util.List;

public interface ReplacementService {

    List<Replacement> getLatestReplacements(ReplacementType replacementType);

    default List<Replacement> getLatestReplacements() {
        return this.getLatestReplacements(ReplacementType.TEACHERS);
    }
}
