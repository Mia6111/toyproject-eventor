package me.toyproject.mia.persistence;

import javax.validation.constraints.NotNull;

public interface AuthFinder {
    @NotNull AuthByAccount getAuth();
}
