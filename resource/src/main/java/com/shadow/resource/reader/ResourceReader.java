package com.shadow.resource.reader;

import java.util.List;

/**
 * @author nevermore on 2015/1/15
 */
public interface ResourceReader {

    <T> List<T> read(Class<T> type);
}
