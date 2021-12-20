package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.gui.widgets.glue.ISliderWidget
import org.anti_ad.mc.common.gui.widgets.glue.ITextFieldWidget
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.DrawableHelper
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.MathHelper
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.alias.TextRenderer
import org.anti_ad.mc.common.vanilla.render.glue.rDrawDynamicSizeSprite
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.glue.rVanillaButtonSprite
import org.anti_ad.mc.common.vanilla.render.rMatrixStack
import org.anti_ad.mc.common.vanilla.alias.SliderWidget as VanillaSliderWidget
import org.anti_ad.mc.common.vanilla.alias.TextFieldWidget as VanillaTextFieldWidget

// ============
// vanillamapping code depends on mappings
// ============

open class VanillaWidget<T : ClickableWidget>(
    val vanilla: T
                                             ) : Widget() {
    init {
        sizeChanged += {
            vanilla.width = width
            // TODO set height
        }
        screenLocationChanged += {
            vanilla.x = screenX
            vanilla.y = screenY
        }
    }

    var vanillaMessage: String
        get() = vanilla.message //asString()
        //    get() = vanilla.func_230458_i_().unformattedComponentText
        set(value) {
            vanilla.message = value
        }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        rStandardGlState() // added this todo (unknown reason fixing text field overflow)
        vanilla.render(mouseX,
                       mouseY,
                       partialTicks)
        super.render(mouseX,
                     mouseY,
                     partialTicks)
    }

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,
                                  y,
                                  button) || vanilla.mouseClicked(x.toDouble(),
                                                                  y.toDouble(),
                                                                  button)
    }

    override fun mouseReleased(x: Int,
                               y: Int,
                               button: Int): Boolean {
        return super.mouseReleased(x,
                                   y,
                                   button) || vanilla.mouseReleased(x.toDouble(),
                                                                    y.toDouble(),
                                                                    button)
    }

    override fun mouseScrolled(x: Int,
                               y: Int,
                               amount: Double): Boolean {
        return super.mouseScrolled(x,
                                   y,
                                   amount) || vanilla.mouseScrolled(x.toDouble(),
                                                                    y.toDouble(),
                                                                    amount)
    }

    override fun mouseDragged(x: Double,
                              y: Double,
                              button: Int,
                              dx: Double,
                              dy: Double): Boolean {
        return super.mouseDragged(x,
                                  y,
                                  button,
                                  dx,
                                  dy) || vanilla.mouseDragged(x,
                                                              y,
                                                              button,
                                                              dx,
                                                              dy)
    }

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean {
        return super.keyPressed(keyCode,
                                scanCode,
                                modifiers) || vanilla.keyPressed(keyCode,
                                                                 scanCode,
                                                                 modifiers)
    }

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean {
        return super.keyReleased(keyCode,
                                 scanCode,
                                 modifiers) || vanilla.keyReleased(keyCode,
                                                                   scanCode,
                                                                   modifiers)
    }

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean {
        return super.charTyped(charIn,
                               modifiers) || vanilla.charTyped(charIn,
                                                               modifiers)
    }
}

private class CustomVanillaSliderWidget(val minValue: Double,
                                        val maxValue: Double) : VanillaSliderWidget(0,
                                                                                    0,
                                                                                    0,
                                                                                    20,
                                                                                    0.5) {

    var valueChangedEvent: () -> Unit = { }

    override fun updateMessage() {}

    //override fun func_230979_b_() {}

    override fun applyValue() {
    //override fun func_230972_a_() {
        valueChangedEvent()
    }

    var translatedValue: Double
        get() = (maxValue - minValue) * super.value + minValue
        //    get() = (maxValue - minValue) * super.field_230683_b_ + minValue
        set(value) {
            super.value = (value - minValue) / (maxValue - minValue)
        }

    override fun renderButton (i: Int,
                               j: Int,
                               f: Float) {
        // fix slider width > 400
        val hovered = isHovered
        val absoluteBounds = Rectangle(x,
                                       y,
                                       width,
                                       height)

//    val k = if (active) if (hovered) 2 else 1 else 0
        val k = 0
        val sprite = rVanillaButtonSprite.down(k)
        rDrawDynamicSizeSprite(sprite,
                               absoluteBounds)

        // ref: AbstractButtonWidget.renderButton()
        //renderBackground(
        renderBg(Vanilla.mc(),
                 i,
                 j)
//    val l = if (active) 16777215 else 10526880
        val l = if (active) if (hovered) 16777120 else 14737632 else 10526880
        this.drawCenteredString(Vanilla.textRenderer(),
                                message,
                                x + width / 2,
                                y + (height - 8) / 2,
                                l or (MathHelper.ceil(alpha * 255.0f) shl 24)
                               )
    }
}

fun newSliderWidget(minValue: Double = 0.0,
                    maxValue: Double = 1.0): ISliderWidget = SliderWidget(minValue, maxValue)

private class SliderWidget(override val minValue: Double = 0.0,
                   override val maxValue: Double = 1.0) : ISliderWidget,
                                                          VanillaWidget<VanillaSliderWidget>(CustomVanillaSliderWidget(minValue,
                                                                                                                       maxValue)) {

    private val silder
        get() = vanilla as CustomVanillaSliderWidget

    override var valueChangedEvent: () -> Unit
        get() = silder.valueChangedEvent
        set(value) {
            silder.valueChangedEvent = value
        }

    override var value: Double
        get() = silder.translatedValue
        set(value) {
            silder.translatedValue = value
        }
}

private class CustomTextFieldWidget(textRenderer: TextRenderer,
                                    i: Int,
                                    j: Int,
                                    k: Int,
                                    l: Int,
                                    string: String) :
    VanillaTextFieldWidget(textRenderer,
                           i,
                           j,
                           k,
                           l,
                           LiteralText(string).text) {
    public override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
    }

    init {
//        setMaxLength(32767)
        setMaxStringLength(32767) // setMaxLength() = forge setMaxStringLength()
    }
}

fun newTextFieldWidget(height: Int): ITextFieldWidget = TextFieldWidget(height)

private class TextFieldWidget(height: Int) : ITextFieldWidget,
                                             VanillaWidget<VanillaTextFieldWidget>(CustomTextFieldWidget(Vanilla.textRenderer(),
                                                                                                         0,
                                                                                                         0,
                                                                                                         0,
                                                                                                         height,
                                                                                                         "")) {

    override var textPredicate: (string: String) -> Boolean = { true }
        set(value) {
            field = value
            vanilla.setValidator(value)
            //vanilla.setFilter(value)
        }
    override var changedEvent: (string: String) -> Unit = { }
        set(value) {
            field = value
            vanilla.setResponder { // setChangedListener
                value(it)
            }
        }

    override var vanillaText: String
        get() = vanilla.text
        set(value) {
            if (vanilla.text != value) {
                vanilla.text = value
            }
        }

    override var vanillaFocused: Boolean
        get() = vanilla.isFocused
        set(value) {
            (vanilla as CustomTextFieldWidget).isFocused = value
        }

    override fun lostFocus() {
        super.lostFocus()
        vanillaFocused = false
    }

    override fun editing(): Boolean =
        vanilla.isFocused// canWrite() // func_212955_f() = method_20315() = isActive = forge canWrite()

    init {
        textPredicate = textPredicate
        changedEvent = changedEvent
    }
}