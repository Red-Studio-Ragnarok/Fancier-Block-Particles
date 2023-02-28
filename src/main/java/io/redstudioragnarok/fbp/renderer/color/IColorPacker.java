package io.redstudioragnarok.fbp.renderer.color;

import java.nio.ByteOrder;

public interface IColorPacker {

	IColorPacker INSTANCE = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
			? (r, g, b, a) -> a << 24 | b << 16 | g << 8 | r
			: (r, g, b, a) -> r << 24 | g << 16 | b << 8 | a;

	int pack(int r, int g, int b, int a);

}
