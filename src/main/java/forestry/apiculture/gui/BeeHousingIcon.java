package forestry.apiculture.gui;

public enum BeeHousingIcon {
	APIARY("/apiary.png"),
	BEE_HOUSE("/alveary.png");

	public static final BeeHousingIcon[] VALUES = values();
	private final String path;

	BeeHousingIcon(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
