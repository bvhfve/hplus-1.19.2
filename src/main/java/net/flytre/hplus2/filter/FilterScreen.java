package net.flytre.hplus2.filter;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.ToggleButton;
import net.flytre.flytre_lib.common.inventory.FilterInventory;
import net.flytre.hplus2.RegistryHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FilterScreen extends HandledScreen<FilterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("hplus:textures/gui/container/filter.png");
    private static final Identifier MODE_BUTTON = new Identifier("hplus:textures/gui/button/check_ex.png");

    private final int startFrame;

    public FilterScreen(FilterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;

        //3 = row
        this.backgroundHeight = 114 + 3 * 18;


        assert MinecraftClient.getInstance().player != null;
        this.startFrame = FilterInventory.fromTag(MinecraftClient.getInstance().player.getMainHandStack().getOrCreateSubTag("filter"), 3).getFilterType();
    }

    @Override
    public void init() {
        super.init();


        ToggleButton modeButton = new ToggleButton(this.x + 177, this.height / 2 - 80, 16, 16, startFrame, MODE_BUTTON, (buttonWidget) -> {

            if (buttonWidget instanceof ToggleButton) {
                ((ToggleButton) buttonWidget).toggleState();
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeInt(((ToggleButton) buttonWidget).getState());
                ClientPlayNetworking.send(RegistryHandler.FILTER_MODE, passedData);
            }

        }, "");
        modeButton.setTooltips(new TranslatableText("item.hplus.filter_upgrade.whitelist"), new TranslatableText("item.hplus.filter_upgrade.blacklist"));
        modeButton.setTooltipRenderer(this::renderTooltip);

        this.addButton(modeButton);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

    }

    protected void drawForeground(MatrixStack matrixStack, int i, int j) {
        this.textRenderer.draw(matrixStack, this.title, 8.0F, 6.0F, (120 * 256) + 255);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float) (this.backgroundHeight - 96 + 2), (120 * 256) + 255);
    }
}
