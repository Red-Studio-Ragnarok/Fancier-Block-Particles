package io.redstudioragnarok.FBP.util;

import java.util.EnumMap;
import java.util.Map;

public class ObfuscationUtil {

	public enum ObfMaterial {
		field_151579_a("Air"),
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
		field_151587_i("Lava"),
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
		field_151586_h("Water"),
		field_151569_G("Web"),
		field_151575_d("Wood");

		private static final Map<ObfMaterial, String> map = new EnumMap<>(ObfMaterial.class);

		static {
			for (ObfMaterial material : values()) {
				map.put(material, material.clearTextName);
			}
		}

		private final String clearTextName;

		ObfMaterial(String clearTextName) {
			this.clearTextName = clearTextName;
		}

		public static String getHumanReadableName(String fieldName) {
			ObfMaterial material = ObfMaterial.valueOf(fieldName);
			return map.getOrDefault(material, material.clearTextName);
		}
	}
}
