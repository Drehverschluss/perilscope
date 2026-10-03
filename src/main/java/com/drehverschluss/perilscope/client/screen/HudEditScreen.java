package com.drehverschluss.perilscope.client.screen;

import com.drehverschluss.perilscope.client.ClientDifficultyState;
import com.drehverschluss.perilscope.client.hud.HudElement;
import com.drehverschluss.perilscope.client.hud.HudElements;
import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Layout editor: drag the HUD elements with the mouse, scroll to scale, right click to show / hide,
 * arrow keys to nudge. The layout is saved when the screen is closed.
 */
public final class HudEditScreen extends Screen {
    private static final int SNAP_DISTANCE = 4;
    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BACKGROUND_COLOR = 0x50000000;
    private static final int OUTLINE_COLOR = 0xFFFFFFFF;
    private static final int IDLE_OUTLINE_COLOR = 0x60FFFFFF;
    private static final int SELECTED_OUTLINE_COLOR = 0xFFFFFF55;
    private static final int GUIDE_COLOR = 0xA0FFFF55;
    private static final float SCALE_STEP = 0.1F;

    @Nullable
    private final Screen parent;
    private final Runnable onSave;
    @Nullable
    private HudElement selected;
    @Nullable
    private HudElement dragged;
    private int grabOffsetX;
    private int grabOffsetY;
    private int guideX = -1;
    private int guideY = -1;

    /**
     * @param parent screen to return to, may be {@code null} to return to the game
     * @param onSave called when the layout should be persisted
     */
    public HudEditScreen(@Nullable Screen parent, Runnable onSave) {
        super(Component.translatable("perilscope.editor.title"));
        this.parent = parent;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int y = height - BUTTON_HEIGHT - 8;
        addRenderableWidget(Button.builder(Component.translatable("perilscope.editor.reset"), button -> {
                    HudElements.ALL.forEach(HudElement::resetLayout);
                    selected = null;
                })
                .bounds(width / 2 - BUTTON_WIDTH - 2, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(width / 2 + 2, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // No blur, the HUD elements have to stay readable
        graphics.fill(0, 0, width, height, BACKGROUND_COLOR);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        DifficultyState state = ClientDifficultyState.get();
        for (HudElement element : HudElements.ALL) {
            List<Component> lines = element.getEditorLines(state);
            element.renderLines(graphics, font, lines, !element.isVisible());
            HudElement.Bounds bounds = bounds(element);
            int color = element == selected ? SELECTED_OUTLINE_COLOR
                    : bounds.contains(mouseX, mouseY) ? OUTLINE_COLOR : IDLE_OUTLINE_COLOR;
            graphics.renderOutline(bounds.x() - 1, bounds.y() - 1, bounds.width() + 2, bounds.height() + 2, color);
        }
        if (guideX >= 0) {
            graphics.vLine(guideX, 0, height, GUIDE_COLOR);
        }
        if (guideY >= 0) {
            graphics.hLine(0, width, guideY, GUIDE_COLOR);
        }
        int hintY = height - BUTTON_HEIGHT - 8 - 4 - font.lineHeight * 2 - 2;
        graphics.drawCenteredString(font, Component.translatable("perilscope.editor.hint.mouse"), width / 2, hintY, 0xFFFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("perilscope.editor.hint.keys"), width / 2,
                hintY + font.lineHeight + 2, 0xFFAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        HudElement element = elementAt(mouseX, mouseY);
        if (element == null) {
            selected = null;
            return false;
        }
        selected = element;
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            HudElement.Bounds bounds = bounds(element);
            dragged = element;
            grabOffsetX = (int) mouseX - bounds.x();
            grabOffsetY = (int) mouseY - bounds.y();
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            element.setVisible(!element.isVisible());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragged == null || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        HudElement.Bounds bounds = bounds(dragged);
        int x = (int) mouseX - grabOffsetX;
        int y = (int) mouseY - grabOffsetY;
        guideX = -1;
        guideY = -1;
        if (!hasShiftDown()) {
            x = snap(x, bounds.width(), width, true);
            y = snap(y, bounds.height(), height, false);
        }
        dragged.moveTo(x, y, bounds.width(), bounds.height(), width, height);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragged = null;
            guideX = -1;
            guideY = -1;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        HudElement element = elementAt(mouseX, mouseY);
        if (element == null || scrollY == 0) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        selected = element;
        float scale = element.getScale() + (scrollY > 0 ? SCALE_STEP : -SCALE_STEP);
        element.setScale(Math.round(scale * 10.0F) / 10.0F);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selected != null) {
            int step = hasShiftDown() ? 10 : 1;
            int dx = 0;
            int dy = 0;
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> dx = -step;
                case GLFW.GLFW_KEY_RIGHT -> dx = step;
                case GLFW.GLFW_KEY_UP -> dy = -step;
                case GLFW.GLFW_KEY_DOWN -> dy = step;
                default -> {
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }
            }
            HudElement.Bounds bounds = bounds(selected);
            selected.moveTo(bounds.x() + dx, bounds.y() + dy, bounds.width(), bounds.height(), width, height);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        onSave.run();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private HudElement.Bounds bounds(HudElement element) {
        return element.getBounds(font, element.getEditorLines(ClientDifficultyState.get()), width, height);
    }

    /**
     * @return the topmost element under the mouse, the last element of the list is drawn on top
     */
    @Nullable
    private HudElement elementAt(double mouseX, double mouseY) {
        for (int i = HudElements.ALL.size() - 1; i >= 0; i--) {
            HudElement element = HudElements.ALL.get(i);
            if (bounds(element).contains(mouseX, mouseY)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Snaps the position to the screen edges and the screen center and remembers the guide line to draw.
     */
    private int snap(int position, int size, int screenSize, boolean horizontal) {
        int[] targets = {0, (screenSize - size) / 2, screenSize - size};
        int best = position;
        int bestDistance = SNAP_DISTANCE + 1;
        int bestGuide = -1;
        for (int target : targets) {
            int distance = Math.abs(position - target);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = target;
                // Edges are marked at the screen border, the centered element at the screen center
                bestGuide = target == 0 ? 0 : target == screenSize - size ? screenSize - 1 : screenSize / 2;
            }
        }
        if (bestDistance <= SNAP_DISTANCE) {
            if (horizontal) {
                guideX = bestGuide;
            } else {
                guideY = bestGuide;
            }
        }
        return Mth.clamp(best, 0, Math.max(0, screenSize - size));
    }
}
