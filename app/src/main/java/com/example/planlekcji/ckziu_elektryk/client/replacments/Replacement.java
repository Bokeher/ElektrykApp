package com.example.planlekcji.ckziu_elektryk.client.replacments;

import java.util.List;

public record Replacement(String name, List<ReplacementChange> changes) {

}
