package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEForge;
import city.windmill.ingameime.forge.ScreenEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import kotlin.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EditBox.class)
abstract class MixinEditBox extends AbstractWidget {
    @Shadow
    private boolean bordered;

    private MixinEditBox(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(method = {"setFocused"}, at = @At("HEAD"))
    private void onSelected(boolean selected, CallbackInfo info) {
        int x = getX();
        int y = getY();
        int caretX = bordered ? getX() + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (selected)
            IngameIMEForge.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(caretX, caretY)));
        else
            IngameIMEForge.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditClose(this));
    }

    @Inject(method = "onClick", at = @At(value = "INVOKE",
            target = "net/minecraft/util/Mth.floor(D)I",
            shift = At.Shift.BEFORE,
            ordinal = 0))
    private void onFocused(double double_1, double double_2, int int_1, CallbackInfoReturnable<Boolean> cir) {
        int x = getX();
        int y = getY();
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        IngameIMEForge.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(caretX, caretY)));
    }

    @Inject(method = "renderWidget",
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret(GuiGraphics arg, int arg1, int arg2, float arg3, CallbackInfo ci, int l, int m, int n, String string, boolean bl, boolean bl2, int o, int p, int q, boolean bl3, int r) {
        IngameIMEForge.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(r, p)));
    }
}
