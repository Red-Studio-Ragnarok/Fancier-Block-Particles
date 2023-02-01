package io.redstudioragnarok.FBP.util;

public class ObfuscationUtil {

	private static ObfuscatedMaterialName tempObfuscatedMaterialName;
	private static MaterialName tempMaterialName;

	public enum MaterialName {
		Anvil("field_151574_g"),
		Barrier("field_175972_I"),
		Cactus("field_151570_A"),
		Cake("field_151568_F"),
		Carpet("field_151593_r"),
		Circuits("field_151594_q"),
		Clay("field_151571_B"),
		Cloth("field_151580_n"),
		Coral("field_151589_v"),
		CraftedSnow("field_151596_z"),
		DragonEgg("field_151566_D"),
		Fire("field_151581_o"),
		Glass("field_151592_s"),
		Gourd("field_151572_C"),
		Grass("field_151577_b"),
		Ground("field_151578_c"),
		Ice("field_151588_w"),
		Iron("field_151573_f"),
		Leaves("field_151584_j"),
		PackedIce("field_151598_x"),
		Piston("field_76233_E"),
		Plants("field_151585_k"),
		Portal("field_151567_E"),
		RedstoneLight("field_151591_t"),
		Rock("field_151576_e"),
		Sand("field_151595_p"),
		Snow("field_151597_y"),
		Sponge("field_151583_m"),
		StructureVoid("field_189963_J"),
		Tnt("field_151590_u"),
		Vine("field_151582_l"),
		Web("field_151569_G"),
		Wood("field_151575_d");

		private final String obfuscatedMaterialName;

		MaterialName(String obfuscatedMaterialName) {
			this.obfuscatedMaterialName = obfuscatedMaterialName;
		}

		// Todo: Javadoc
		//
		// Temp doc:
		// This is used to translate a material name into an obfuscated material name.
		//
		// Example:
		// Translate material name `Packed_Ice` into the obfuscated material name `field_151598_x`
		public static String translateMaterialName(String materialName) {
			tempMaterialName = MaterialName.valueOf(materialName);
			return tempMaterialName.obfuscatedMaterialName;
		}
	}

	public enum ObfuscatedMaterialName {
		field_151574_g("Anvil"),
		field_175972_I("Barrier"),
		field_151570_A("Cactus"),
		field_151568_F("Cake"),
		field_151593_r("Carpet"),
		field_151594_q("Circuits"),
		field_151571_B("Clay"),
		field_151580_n("Cloth"),
		field_151589_v("Coral"),
		field_151596_z("Crafted Snow"),
		field_151566_D("Dragon Egg"),
		field_151581_o("Fire"),
		field_151592_s("Glass"),
		field_151572_C("Gourd"),
		field_151577_b("Grass"),
		field_151578_c("Ground"),
		field_151588_w("Ice"),
		field_151573_f("Iron"),
		field_151584_j("Leaves"),
		field_151598_x("Packed Ice"),
		field_76233_E("Piston"),
		field_151585_k("Plants"),
		field_151567_E("Portal"),
		field_151591_t("Redstone Light"),
		field_151576_e("Rock"),
		field_151595_p("Sand"),
		field_151597_y("Snow"),
		field_151583_m("Sponge"),
		field_189963_J("Structure Void"),
		field_151590_u("Tnt"),
		field_151582_l("Vine"),
		field_151569_G("Web"),
		field_151575_d("Wood");

		private final String materialName;

		ObfuscatedMaterialName(String materialName) {this.materialName = materialName;}

		// Todo: Javadoc
		//
		// Temp doc:
		// This is used to translate an obfuscated material name into a material name.
		//
		// Example:
		// Translate obfuscated material name `field_151598_x` into the material name `Packed_Ice`
		public static String translateObfuscatedMaterialName(String fieldName) {
			tempObfuscatedMaterialName = ObfuscatedMaterialName.valueOf(fieldName);
			return tempObfuscatedMaterialName.materialName;
		}
	}
}
