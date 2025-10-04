package pencil_utensil.organaut.organization;

import java.util.Random;

public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

	private static final Random random = new Random();

	public static OrganizationType random() {
		OrganizationType[] values = OrganizationType.values();
		return values[random.nextInt(values.length)];
	}
}