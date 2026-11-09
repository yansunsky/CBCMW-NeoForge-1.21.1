package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.index.CBCGuiTextures;
import rbasamoyai.createbigcannons.multiloader.NetworkPlatform;
import rbasamoyai.createbigcannons.network.ServerboundSetContainerValuePacket;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks;

/**
 * 弹鼓界面（客户端）。
 *
 * <p>复用 CBC 的容器背景与选择器贴图（{@code CBCGuiTextures.AUTOCANNON_AMMO_CONTAINER_*}），
 * 所以不需要本项目自己出 GUI 贴图。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code GuiGraphics} 系列签名整体未变；
 * {@code Lang.builder(...).translate(...).component()} 与
 * {@code ScrollInput} 的链式 API 都已核实存在。
 */
public class AutocannonAmmoDrumScreen extends AbstractSimiContainerScreen<AutocannonAmmoDrumMenu> {

	protected ScrollInput setValue;
	protected int lastUpdated = -1;
	protected IconButton confirmButton;
	private List<Rect2i> extraAreas = Collections.emptyList();

	public AutocannonAmmoDrumScreen(AutocannonAmmoDrumMenu container, Inventory inv, Component title) {
		super(container, inv, title);
	}

	@Override
	protected void init() {
		CBCGuiTextures bg = CBCGuiTextures.AUTOCANNON_AMMO_CONTAINER_BG;
		this.setWindowSize(bg.width, bg.height + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
		this.setWindowOffset(1, 0);
		super.init();

		this.setValue = this.getScrollInput();
		this.setValue.onChanged();
		this.addRenderableWidget(this.setValue);

		this.confirmButton = new IconButton(this.leftPos + this.imageWidth - 33, this.topPos + 59, AllIcons.I_CONFIRM);
		this.confirmButton.withCallback(this::onClose);
		this.addRenderableWidget(this.confirmButton);

		this.extraAreas = ImmutableList.of(
				new Rect2i(this.leftPos + bg.width, this.topPos + bg.height - 68, 60, 60));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
		int invY = this.topPos + CBCGuiTextures.AUTOCANNON_AMMO_CONTAINER_BG.height + 4;
		this.renderPlayerInventory(graphics, invX, invY);

		int offsX = this.setValue.getState() * 8 - 8;
		CBCGuiTextures bg = CBCGuiTextures.AUTOCANNON_AMMO_CONTAINER_BG;
		bg.render(graphics, this.leftPos, this.topPos);
		graphics.drawCenteredString(this.font, this.title, this.leftPos + this.imageWidth / 2 - 4, this.topPos + 3,
				0xFFFFFF);

		CBCGuiTextures sel = CBCGuiTextures.AUTOCANNON_AMMO_CONTAINER_SELECTOR;
		sel.render(graphics, this.leftPos + 86 + offsX, this.topPos + 23);

		// 右下角画一个 3D 的弹鼓模型（跟着「空/满」变化）
		BlockState state = CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM.getDefaultState();
		state = state.setValue(AutocannonAmmoDrumBlock.CONTAINER_STATE,
				AutocannonAmmoDrumBlock.State.getFromFilled(this.menu.isFilled()));
		GuiGameElement.of(state)
				.scale(50.0)
				.rotate(30.0, 135.0, 0.0)
				.at(this.leftPos + bg.width + 32, this.topPos + bg.height, 200.0F)
				.render(graphics);
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int x, int y) {
		super.renderTooltip(graphics, x, y);
		// 曳光槽为空时给个说明（槽位索引 2 是曳光槽）
		if (this.hoveredSlot != null && this.hoveredSlot.index == AutocannonAmmoDrumMenu.TRACER_SLOT_INDEX
				&& !this.hoveredSlot.hasItem()) {
			graphics.renderTooltip(this.font,
					Lang.builder("createbigcannons").translate("gui.autocannon_ammo_container.tracer_slot").component(),
					x, y);
		}
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (this.lastUpdated >= 0) {
			this.lastUpdated++;
		}
		if (this.lastUpdated >= 20) {
			this.updateServer();
			this.lastUpdated = -1;
		}
	}

	@Override
	public void removed() {
		super.removed();
		this.updateServer();
	}

	@Override
	public void onClose() {
		this.updateServer();
		super.onClose();
	}

	private void updateServer() {
		NetworkPlatform.sendToServer(new ServerboundSetContainerValuePacket(this.setValue.getState()));
	}

	protected ScrollInput getScrollInput() {
		return new ScrollInput(this.leftPos + 87, this.topPos + 31, 47, 6)
				.withRange(1, 7)
				.calling(state -> {
					this.lastUpdated = 0;
					this.setValue.titled(Lang.builder("createbigcannons")
							.translate("gui.autocannon_ammo_container.tracer_spacing", state).component());
				})
				.setState(Mth.clamp(this.menu.getValue(), 1, 6));
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return this.extraAreas;
	}
}
