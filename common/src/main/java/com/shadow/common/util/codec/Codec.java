package com.shadow.common.util.codec;

import javax.annotation.Nonnull;

/**
 * @author nevermore on 2015/3/5
 */
public interface Codec {

    <T> byte[] encode(@Nonnull T obj);

    <T> T decode(@Nonnull byte[] data, @Nonnull Class<T> type);
}
