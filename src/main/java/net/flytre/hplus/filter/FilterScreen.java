package net.flytre.hplus.filter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.api.storage.inventory.filter.FilteredScreen;
import net.flytre.hplus.network.FilterC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FilterScreen extends FilteredScreen<FilterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("hplus:textures/gui/container/filter.png");

    private final FilterInventory startInfo;

    public FilterScreen(FilterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;

        //3 = row
        this.backgroundHeight = 114 + 3 * 18;


        assert MinecraftClient.getInstance().player != null;
        ItemStack stack = MinecraftClient.getInstance().player.getOffHandStack();
        if (!(stack.getItem() instanceof FilterUpgrade))
            stack = MinecraftClient.getInstance().player.getMainHandStack();
        this.startInfo = FilterInventory.readNbt(stack.getOrCreateSubNbt("filter"), 3);
    }

    @Override
    public void init() {
        super.init();

        addButton(startInfo.getFilterType(), 0, MODE_BUTTON, (__, state) -> new FilterC2SPacket(FilterC2SPacket.Type.MODE,state), () -> null, new TranslatableText("item.hplus.filter_upgrade.whitelist"), new TranslatableText("item.hplus.filter_upgrade.blacklist"));
        addButton(startInfo.isMatchMod() ? 1 : 0, 1, MOD_BUTTON, (__, state) -> new FilterC2SPacket(FilterC2SPacket.Type.MOD,state), () -> null, new TranslatableText("item.hplus.filter_upgrade.mod_match.false"), new TranslatableText("item.hplus.filter_upgrade.mod_match.true"));
        addButton(startInfo.isMatchNbt() ? 1 : 0, 2, NBT_BUTTON, (__, state) -> new FilterC2SPacket(FilterC2SPacket.Type.NBT,state), () -> null, new TranslatableText("item.hplus.filter_upgrade.nbt_match.false"), new TranslatableText("item.hplus.filter_upgrade.nbt_match.true"));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

    }

    protected void drawForeground(MatrixStack matrixStack, int i, int j) {
        this.textRenderer.draw(matrixStack, this.title, 8.0F, 6.0F, (120 * 256) + 255);
        this.textRenderer.draw(matrixStack, playerInventoryTitle, 8.0F, (float) (this.backgroundHeight - 96 + 2), (120 * 256) + 255);
    }
}
