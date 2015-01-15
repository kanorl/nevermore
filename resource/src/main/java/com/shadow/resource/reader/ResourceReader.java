package com.shadow.resource.reader;

import java.util.Set;

/**
 * @author nevermore on 2015/1/15
 */
public interface ResourceReader {

    <T> Set<T> read(Class<T> type);
}
