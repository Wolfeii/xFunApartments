package se.xfunserver.xfunapartments.apartments.expansions;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ExpansionCommand {

    @Getter
    private final String executeAs;

    @Getter
    private final String command;
}
