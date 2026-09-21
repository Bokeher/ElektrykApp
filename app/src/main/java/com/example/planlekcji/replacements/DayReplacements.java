package com.example.planlekcji.replacements;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;

import java.util.Date;
import java.util.List;

public record DayReplacements(Date date, List<Replacement> replacements) {
}
