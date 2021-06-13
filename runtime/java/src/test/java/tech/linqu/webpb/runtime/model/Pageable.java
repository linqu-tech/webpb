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

    private Integer size = 234;

    private Integer page = 345;
}
