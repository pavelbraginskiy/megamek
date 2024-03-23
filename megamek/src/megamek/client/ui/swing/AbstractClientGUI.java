package megamek.client.ui.swing;

import megamek.client.ui.Messages;
import megamek.client.ui.swing.util.UIUtil;
import megamek.common.Configuration;
import megamek.common.preference.ClientPreferences;
import megamek.common.preference.PreferenceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClientGUI implements IClientGUI {

    protected static final GUIPreferences GUIP = GUIPreferences.getInstance();
    protected static final ClientPreferences CP = PreferenceManager.getClientPreferences();

    private static final String FILENAME_ICON_16X16 = "megamek-icon-16x16.png";
    private static final String FILENAME_ICON_32X32 = "megamek-icon-32x32.png";
    private static final String FILENAME_ICON_48X48 = "megamek-icon-48x48.png";
    private static final String FILENAME_ICON_256X256 = "megamek-icon-256x256.png";

    protected final JFrame frame = new JFrame(Messages.getString("ClientGUI.title"));

    public AbstractClientGUI() {
        initializeFrame();
    }

    @Override
    public JFrame getFrame() {
        return frame;

    }

    @Override
    public boolean shouldIgnoreHotKeys() {
        return UIUtil.isModalDialogDisplayed();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void die() {

    }

    private void initializeFrame() {
//        frame.setJMenuBar(menuBar);
        if (GUIP.getWindowSizeHeight() != 0) {
            frame.setLocation(GUIP.getWindowPosX(), GUIP.getWindowPosY());
            frame.setSize(GUIP.getWindowSizeWidth(), GUIP.getWindowSizeHeight());
        } else {
            frame.setSize(800, 600);
        }
        frame.setMinimumSize(new Dimension(640, 480));
        UIUtil.updateWindowBounds(frame);

        List<Image> iconList = new ArrayList<>();
        iconList.add(frame.getToolkit().getImage(new File(Configuration.miscImagesDir(), FILENAME_ICON_16X16).toString()));
        iconList.add(frame.getToolkit().getImage(new File(Configuration.miscImagesDir(), FILENAME_ICON_32X32).toString()));
        iconList.add(frame.getToolkit().getImage(new File(Configuration.miscImagesDir(), FILENAME_ICON_48X48).toString()));
        iconList.add(frame.getToolkit().getImage(new File(Configuration.miscImagesDir(), FILENAME_ICON_256X256).toString()));
        frame.setIconImages(iconList);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!GUIP.getNoSaveNag()) {
                    int savePrompt = JOptionPane.showConfirmDialog(null,
                            Messages.getString("ClientGUI.gameSaveDialogMessage"),
                            Messages.getString("ClientGUI.gameSaveFirst"),
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if ((savePrompt == JOptionPane.CANCEL_OPTION)
                            || ((savePrompt == JOptionPane.YES_OPTION) && !saveGame())) {
                        // When the user clicked YES but did not actually save the game, don't close the game
                        return;
                    }
                }
                frame.setVisible(false);
                saveSettings();
                die();

            }
        });
    }

    /**
     * Saves window and other settings to the GUIPreferences. Typically called when this ClientGUI shuts down.
     * By default, the size and position of the window are saved. When overriding this to save additional
     * settings, super should be called.
     */
    void saveSettings() {
        GUIP.setWindowPosX(frame.getLocation().x);
        GUIP.setWindowPosY(frame.getLocation().y);
        GUIP.setWindowSizeWidth(frame.getSize().width);
        GUIP.setWindowSizeHeight(frame.getSize().height);
    }

    protected abstract boolean saveGame();
}
