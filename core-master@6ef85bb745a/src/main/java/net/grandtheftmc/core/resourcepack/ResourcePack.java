package net.grandtheftmc.core.resourcepack;

/**
 * Created by Luke Bingham on 06/08/2017.
 */
public class ResourcePack {

	/** The URL for the pack */
	private final String pack;
	/** The 20 byte hash that identifies the pack */
	private final String hash;

	/**
	 * Create a new ResourcePack.
	 * 
	 * @param pack - the pack url
	 * @param hash - the hash associated with the pack
	 */
	public ResourcePack(String pack, String hash) {
		this.pack = pack;
		this.hash = hash;
	}

	/**
	 * Get the url for the pack.
	 * 
	 * @return The URL for the pack.
	 */
	public String getPack() {
		return pack;
	}

	/**
	 * Get the id hash in US-ASCII characters and should be encoded as per RFC
	 * 1738.
	 * 
	 * @return The hash for the pack.
	 */
	public String getHash() {
		return hash;
	}
}
