/*
 * Copyright (c) 2018-2020 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import java.awt.*;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetlib.vappearances.VAppearance;

/**
 * An object representing a specific appearance, including the current accent and highlight colors.
 */

public class BasicAquaAppearance implements VAppearance {

    private final @NotNull VAppearance appearance;
    private final @NotNull Colors colors;
    private final @NotNull Logger log;

    public BasicAquaAppearance(@NotNull VAppearance appearance,
                               @NotNull Colors colors,
                               @NotNull Logger log) {
        this.appearance = appearance;
        this.colors = colors;
        this.log = log;
    }

    @Override
    public @NotNull String getName() {
        return appearance.getName();
    }

    @Override
    public boolean isDark() {
        return appearance.isDark();
    }

    @Override
    public boolean isHighContrast() {
        return appearance.isHighContrast();
    }

    @Override
    public @NotNull Map<String,Color> getColors() {
        return appearance.getColors();
    }

    /**
     * Return the color with the specified name.
     * @param colorName The color name.
     * @return the color, as a ColorUIResource, or null if the color name not defined in this appearance.
     */

    public @Nullable Color getColor(@NotNull String colorName) {
        return colors.get(colorName);
    }

    public boolean isBasedOn(@NotNull VAppearance va) {
        return va == appearance;
    }

    protected @NotNull Colors buildColors(int OSVersion,
                                          @NotNull SystemColors systemColors,
                                          @NotNull Map<String,Color> nativeColors)
    {
        ColorsBuilder colors = new ColorsBuilder(log);
        colors.add(systemColors.defaultColors);
        colors.addAll(appearance.getColors());
        colors.addAll(nativeColors);
        colors.add("controlText_disabled", "disabledControlText");

        if (appearance.isDark()) {
            colors.add(systemColors.darkColors);
            if (appearance.isHighContrast()) {
                colors.add(systemColors.highContrastDarkColors);
            }
        } else {
            colors.add(systemColors.lightColors);
            if (appearance.isHighContrast()) {
                colors.add(systemColors.highContrastLightColors);
            }
        }
        installFixups(colors, appearance, OSVersion);
        return colors.getColors();
    }

    // Fixups are alterations that depend on existing definitions being present.

    private void installFixups(@NotNull ColorsBuilder colors, @NotNull VAppearance appearance, int OSVersion) {

        if (OSVersion < 1014) {
            // Some appearance based colors prior to 10.14 are not represented as system colors.
            // These colors are not used in 10.14 or later.
            Color c = colors.get("controlAccent");
            boolean isGraphite = c != null && (c.getBlue() - c.getRed()) < 30;
            if (isGraphite) {
                colors.add("menuSelectedBackground", 162, 162, 168);
                colors.add("menuBackground", 240);
            } else {
                colors.add("menuSelectedBackground", 54, 148, 253);
                colors.add("menuBackground", 0, 0);
            }
            colors.add("menuForeground", 61);
        }

        if (appearance.isDark()) {
            // A workaround for an apparently bogus color:
            {
                Color c = colors.get("alternatingContentBackground_1_disabled");
                if (c != null && c.getRed() == 255 && c.getAlpha() == 128) {
                    colors.add("alternatingContentBackground_1_disabled", 128, 13);
                }
            }
        }
    }

    @Override
    public @NotNull String toString() {
        return super.toString() + "[" + appearance.getName() + "]";
    }
}
