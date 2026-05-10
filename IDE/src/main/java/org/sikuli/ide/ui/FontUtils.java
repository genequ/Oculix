/*
 * Copyright (c) 2010-2026, sikuli.org, sikulix.com, oculix-org - MIT license
 */
package org.sikuli.ide.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for platform-specific font selection with emoji and Unicode support.
 * Solves the issue where Inter font doesn't contain emoji glyphs and Java's
 * font fallback mechanism doesn't work reliably in Swing components.
 *
 * @author Claude (Anthropic)
 * @since 3.0.3
 */
public final class FontUtils {

  private FontUtils() {}

  /**
   * Returns a font that supports emoji and Unicode symbols on the current platform.
   *
   * Platform-specific behavior:
   * - macOS: .SF Pro Text (falls back to Apple Color Emoji)
   * - Windows: Segoe UI (has emoji support)
   * - Linux: Inter (bundled) with JVM fallback
   *
   * @param size font size in points
   * @return platform-appropriate Font
   */
  public static Font getFallbackFont(int size) {
    return getFallbackFont(Font.PLAIN, size);
  }

  /**
   * Returns a font that supports emoji and Unicode symbols with specified style.
   *
   * @param style font style (Font.PLAIN, Font.BOLD, etc.)
   * @param size font size in points
   * @return platform-appropriate Font
   */
  public static Font getFallbackFont(int style, int size) {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("mac")) {
      return new Font(".SF Pro Text", style, size);
    } else if (os.contains("win")) {
      return new Font("Segoe UI", style, size);
    } else {
      Font defaultFont = UIManager.getFont("defaultFont");
      if (defaultFont != null) {
        return defaultFont.deriveFont(style, (float)size);
      }
      return new Font(Font.SANS_SERIF, style, size);
    }
  }

  /**
   * Returns the UI default font with emoji support, deriving size from defaultFont.
   *
   * @return platform-appropriate Font with default UI size
   */
  public static Font getFallbackFont() {
    Font defaultFont = UIManager.getFont("defaultFont");
    int size = defaultFont != null ? defaultFont.getSize() : 14;
    return getFallbackFont(size);
  }
}
