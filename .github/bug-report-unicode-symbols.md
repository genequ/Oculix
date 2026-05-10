# Bug Report: Garbled Unicode Symbols in UI

## Summary
Unicode symbols (emoji, arrows, box-drawing characters) were displayed as garbled/tofu characters on macOS due to the Inter font not containing the required glyphs.

## Environment
- **OS**: macOS 26.4.1 (aarch64/ARM64)
- **Java**: 25.0.1 (Oracle)
- **OculiX Version**: 3.0.3
- **Affected Components**: Sidebar, Status Bar

## Affected Areas

### 1. Sidebar (Fixed in commit eaa9630)
- **Symbols**: 📁 ✏️ 🔧 ❓ ▸
- **Issue**: Sidebar button icons and arrows showed as garbled characters
- **Files**: `OculixSidebar.java`, `SidebarItem.java`

### 2. Status Bar (Fixed in current session)
- **Symbols**: │ (U+2502 box drawing), ⬤ (U+2B24 filled circle)
- **Issue**: Version separator and PaddleOCR indicator showed as garbled characters
- **Files**: `SikuliIDEStatusBar.java`

## Root Cause
UI components were using:
```java
UIManager.getFont("Label.font").deriveFont(11.0f)
```

This returned the Inter font, which doesn't contain glyphs for:
- Emoji characters
- Box-drawing symbols (│ ╭ ╮ ╯ ╰)
- Geometric shapes (▸ ⬤)

Java's font fallback mechanism didn't work reliably in Swing components.

## Solution
Created centralized `FontUtils` class for platform-specific font selection:

### FontUtils.java
```java
public final class FontUtils {
    public static Font getFallbackFont(int size) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return new Font(".SF Pro Text", style, size);
        } else if (os.contains("win")) {
            return new Font("Segoe UI", style, size);
        } else {
            // Linux: use default with JVM fallback
        }
    }
}
```

### Platform-Specific Fonts
| Platform | Font | Unicode Support |
|----------|------|-----------------|
| macOS | `.SF Pro Text` | Falls back to `Apple Color Emoji` |
| Windows | `Segoe UI` | Built-in emoji support |
| Linux | Default font | JVM fallback mechanism |

## Files Changed
1. **IDE/src/main/java/org/sikuli/ide/ui/FontUtils.java** (NEW)
   - Centralized utility for platform-specific font selection

2. **IDE/src/main/java/org/sikuli/ide/ui/SidebarItem.java**
   - Applied: `setFont(FontUtils.getFallbackFont())`

3. **IDE/src/main/java/org/sikuli/ide/SikuliIDEStatusBar.java**
   - Applied: `setFont(FontUtils.getFallbackFont(11))` to:
     - `_lblMsg` (OculiX version display)
     - `_lblOcrStatus` (PaddleOCR indicator)
     - `_lblCaretPos` (Caret position display)

## Verification

### Sidebar
After fix, sidebar should display:
```
📁 Scripts    ▸
✏️ Editor     ▸
🔧 Settings   ▸
❓ Help       ▸
```

### Status Bar
After fix, status bar should display:
```
OculiX 3.0.3  │  Java 25     [Python]  R: 1  C: 1
              ↑
          proper │ symbol

⬤ PaddleOCR  (green/red/gray depending on status)
  ↑
  proper ⬤ symbol
```

## Testing
```bash
# Build
mvn clean package -Pcomplete-mac-jar -DskipTests

# Run
java -jar IDE/target/oculixide-3.0.3-complete-mac.jar
```

Verify:
1. ✅ Sidebar icons display correctly
2. ✅ Sidebar arrows (▸) display correctly
3. ✅ Status bar separator (│) displays correctly
4. ✅ PaddleOCR indicator (⬤) displays correctly with proper colors
5. ✅ No garbled/tofu characters anywhere in UI

## Commits
- `eaa9630` - Initial sidebar fix (created `createFallbackFont()` in `SidebarItem`)
- `fix/sidebar-garbled-arrow-symbol` - Refactored to centralized `FontUtils` + status bar fix

## Reporter
@gequ
