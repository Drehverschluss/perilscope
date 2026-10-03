package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * A freely positionable HUD element: anchor + offset (GUI pixels) + scale.
 * Phase 1 renders plain text lines on a semi-transparent background.
 */
public abstract class HudElement {
    public static final float MIN_SCALE = 0.25F;
    public static final float MAX_SCALE = 4.0F;

    private static final int PADDING = 3;
    private static final int LINE_SPACING = 1;
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int DIMMED_BACKGROUND_COLOR = 0x40000000;
    private static final int DIMMED_TEXT_COLOR = 0x80FFFFFF;

    /**
     * Position and size of an element on screen, in GUI pixels (scale already applied).
     */
    public record Bounds(int x, int y, int width, int height) {
        public boolean contains(double px, double py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
    }

    private final String id;
    private final HudAnchor defaultAnchor;
    private final int defaultOffsetX;
    private final int defaultOffsetY;
    private HudAnchor anchor;
    private int offsetX;
    private int offsetY;
    private float scale = 1.0F;
    private boolean visible = true;
    private boolean colorByDifficulty = true;
    private final DifficultyColors.Palette palette;
    private int colorMaxLevel;

    protected HudElement(String id, HudAnchor anchor, int offsetX, int offsetY,
                         DifficultyColors.Palette palette, int defaultColorMaxLevel) {
        this.id = id;
        this.defaultAnchor = anchor;
        this.defaultOffsetX = offsetX;
        this.defaultOffsetY = offsetY;
        this.anchor = anchor;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.palette = palette;
        this.colorMaxLevel = defaultColorMaxLevel;
    }

    /**
     * Colors the text by difficulty level (if enabled), see {@link DifficultyColors}.
     */
    protected final MutableComponent colored(MutableComponent text, int level) {
        return colorWith(text, DifficultyColors.forLevel(palette, level, colorMaxLevel));
    }

    /**
     * Colors the text as harmless (if coloring is enabled).
     */
    protected final MutableComponent coloredSafe(MutableComponent text) {
        return colorWith(text, DifficultyColors.SAFE);
    }

    private MutableComponent colorWith(MutableComponent text, int rgb) {
        return colorByDifficulty ? text.withStyle(style -> style.withColor(rgb)) : text;
    }

    /**
     * @return the text lines to show for the given state, empty to render nothing
     */
    protected abstract List<Component> getLines(DifficultyState state);

    /**
     * @return sample lines shown in the layout editor while {@link #getLines} has nothing to show
     */
    protected abstract List<Component> getPreviewLines();

    /**
     * @return the real lines if there are any, otherwise the sample lines
     */
    public List<Component> getEditorLines(DifficultyState state) {
        List<Component> lines = getLines(state);
        return lines.isEmpty() ? getPreviewLines() : lines;
    }

    public void render(GuiGraphics graphics, Font font, DifficultyState state) {
        if (!visible) {
            return;
        }
        List<Component> lines = getLines(state);
        if (lines.isEmpty()) {
            return;
        }
        renderLines(graphics, font, lines, false);
    }

    /**
     * Renders the given lines at the position of this element.
     */
    public void renderLines(GuiGraphics graphics, Font font, List<Component> lines, boolean dimmed) {
        Bounds bounds = getBounds(font, lines, graphics.guiWidth(), graphics.guiHeight());
        int width = contentWidth(font, lines);
        int height = contentHeight(font, lines);

        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 0);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.fill(0, 0, width, height, dimmed ? DIMMED_BACKGROUND_COLOR : BACKGROUND_COLOR);
        int lineHeight = font.lineHeight + LINE_SPACING;
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), PADDING, PADDING + i * lineHeight,
                    dimmed ? DIMMED_TEXT_COLOR : TEXT_COLOR, !dimmed);
        }
        graphics.pose().popPose();
    }

    /**
     * @return where the element is drawn for the given lines and screen size
     */
    public Bounds getBounds(Font font, List<Component> lines, int screenWidth, int screenHeight) {
        int scaledWidth = Mth.ceil(contentWidth(font, lines) * scale);
        int scaledHeight = Mth.ceil(contentHeight(font, lines) * scale);
        // Keep the element on screen even if the window got smaller than the configured offset
        int x = Mth.clamp(anchor.resolveX(screenWidth, scaledWidth, offsetX), 0, Math.max(0, screenWidth - scaledWidth));
        int y = Mth.clamp(anchor.resolveY(screenHeight, scaledHeight, offsetY), 0, Math.max(0, screenHeight - scaledHeight));
        return new Bounds(x, y, scaledWidth, scaledHeight);
    }

    /**
     * Moves the element so that its top left corner is at the given position (clamped to the screen).
     * The anchor is set to the one closest to the element, so the element keeps its relative position
     * when the window size or GUI scale changes.
     */
    public void moveTo(int x, int y, int width, int height, int screenWidth, int screenHeight) {
        int clampedX = Mth.clamp(x, 0, Math.max(0, screenWidth - width));
        int clampedY = Mth.clamp(y, 0, Math.max(0, screenHeight - height));
        anchor = HudAnchor.nearest(clampedX + width / 2, clampedY + height / 2, screenWidth, screenHeight);
        offsetX = anchor.offsetX(clampedX, screenWidth, width);
        offsetY = anchor.offsetY(clampedY, screenHeight, height);
    }

    public void resetLayout() {
        anchor = defaultAnchor;
        offsetX = defaultOffsetX;
        offsetY = defaultOffsetY;
        scale = 1.0F;
        visible = true;
    }

    private static int contentWidth(Font font, List<Component> lines) {
        int textWidth = 0;
        for (Component line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }
        return textWidth + PADDING * 2;
    }

    private static int contentHeight(Font font, List<Component> lines) {
        return lines.size() * (font.lineHeight + LINE_SPACING) - LINE_SPACING + PADDING * 2;
    }
    public String getId() {
        return id;
    }

    public HudAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(HudAnchor anchor) {
        this.anchor = anchor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isColorByDifficulty() {
        return colorByDifficulty;
    }

    public void setColorByDifficulty(boolean colorByDifficulty) {
        this.colorByDifficulty = colorByDifficulty;
    }

    public int getColorMaxLevel() {
        return colorMaxLevel;
    }

    public void setColorMaxLevel(int colorMaxLevel) {
        this.colorMaxLevel = colorMaxLevel;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
