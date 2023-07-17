package city.windmill.ingameime.client.gui.widget

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics

class CompositionWidget(font: Font) : Widget(font) {
    /**
     * Composition String, Caret pos
     * the String is the composition text
     * the caret is the position where the composition text is editing
     */
    var compositionData: Pair<String, Int>? = null

    private val caretWidth = 3

    override val active get() = compositionData != null

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override val width
        get() = super.width + font.width(compositionData?.first) + caretWidth
    override val height
        get() = super.height + font.lineHeight
    override val padding: Pair<Int, Int>
        get() = 1 to 1

    @Suppress("NAME_SHADOWING")
    override fun draw(guiGraphics: GuiGraphics, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        compositionData?.let {
            val text = it.first
            val caret = it.second

            super.draw(guiGraphics, offsetX, offsetY, mouseX, mouseY, delta)

            val part1 = text.substring(0, caret)
            val part2 = text.substring(caret)

            var offsetX = offsetX + padding.first
            val offsetY = offsetY + padding.second
            guiGraphics.drawString(font, part1, offsetX, offsetY, textColor)
            //Caret-blink 0.5s
            if ((System.currentTimeMillis() % 1000) > 500) {
                guiGraphics.fill(
                    offsetX + 1, //1 pixel width
                    offsetY,
                    offsetX + 2, //with 2 pixel margin
                    offsetY + font.lineHeight,
                    textColor
                )
            }
            offsetX += caretWidth
            guiGraphics.drawString(font, part2, offsetX, offsetY, textColor)
        }
    }
}
