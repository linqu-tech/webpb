package tech.linqu.webpb.runtime.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link Pageable}.
 */
@Accessors(chain = true)
@Getter
@Setter
public class Pageable {

    private int size = 234;

    private int page = 345;
}
