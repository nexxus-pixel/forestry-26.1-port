package forestry.core.gui.minimal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Functional Forestry machine screen with the correct GUI background texture.
 * Used while full {@code GuiForestry} screens remain excluded from the 26.1 port build.
 */
public class ForestryMachineScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
	private final Identifier texture;

	public ForestryMachineScreen(M menu, Inventory inventory, Component title, Identifier texture, int width, int height) {
		super(menu, inventory, title, width, height);
		this.texture = texture;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		// MC 26.1 blit(texture, x, y, u, v, width, height, textureWidth, textureHeight)
		graphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
	}
}
