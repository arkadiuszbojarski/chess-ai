package org.bojarski.chess;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum MoveType {
    MOVE(),
    CAPTURE(),
    CHECK(true),
    PROMOTION();

    private boolean check;

    MoveType(boolean check) {
        this.check = check;
    }

    MoveType() {
        this(false);
    }
}
