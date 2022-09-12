package net.grandtheftmc.core.util.nbt;

/**
 * Created by ThatAbstractWolf on 2017-08-04.
 */
public interface CoreNbt {

	Object getNBTTag(String key);

	boolean hasNBTTag(String key);
}
