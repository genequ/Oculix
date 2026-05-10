/*
 * Copyright (c) 2010-2026, sikuli.org, sikulix.com, oculix-org - MIT license
 */
package org.sikuli.ide.ui;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A sidebar button with icon and optional label text.
 * Supports collapsed mode (icon only) and expanded mode (icon + label).
 * @author Julien Mer (julienmerconsulting)
 * @author Claude (Anthropic)
 * @since 3.0.3
 */
public class SidebarItem extends JButton {

  private final String labelText;
  private boolean collapsed = false;

  public SidebarItem(String text, Icon icon, ActionListener action) {
    this.labelText = text;
    setIcon(icon);
    setText(text);
    setToolTipText(text);
    setHorizontalAlignment(SwingConstants.LEFT);
    setIconTextGap(10);
    setFocusPainted(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setFont(createFallbackFont());
    putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

    if (action != null) {
      addActionListener(action);
    }

    addMouseAdapter();
  }

  public SidebarItem(String text, Icon icon) {
    this(text, icon, null);
  }

  private Font createFallbackFont() {
    // On macOS, use system font family which automatically falls back to Apple Color Emoji
    // On other platforms, use defaultFont with fallback support
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("mac")) {
      // macOS: Use SF Pro family which includes emoji support
      return new Font(".SF Pro Text", Font.PLAIN, 14);
    } else if (os.contains("win")) {
      // Windows: Use Segoe UI which supports emoji
      return new Font("Segoe UI", Font.PLAIN, 14);
    } else {
      // Linux: Use Inter (bundled) and hope for fallback, or use system default
      Font defaultFont = UIManager.getFont("defaultFont");
      if (defaultFont != null) {
        return defaultFont.deriveFont(14.0f);
      }
      return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    }
  }

  private void addMouseAdapter() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        if (isEnabled()) {
          setContentAreaFilled(true);
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setContentAreaFilled(false);
      }
    });
  }

  public void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
    if (collapsed) {
      setText(null);
      setHorizontalAlignment(SwingConstants.CENTER);
    } else {
      setText(labelText);
      setHorizontalAlignment(SwingConstants.LEFT);
    }
    revalidate();
  }

  public boolean isCollapsed() {
    return collapsed;
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    if (collapsed) {
      d.width = 50;
    }
    d.height = Math.max(d.height, 36);
    return d;
  }

  @Override
  public Dimension getMaximumSize() {
    Dimension d = super.getMaximumSize();
    d.height = getPreferredSize().height;
    return d;
  }
}
