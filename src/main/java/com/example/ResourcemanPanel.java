package com.example;

import com.example.data.ResourceTracker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

public class ResourcemanPanel extends PluginPanel
{
    private final ResourcemanPlugin plugin;

    private JPanel resourceListPanel;
    private JLabel sessionResourceLabel;
    private JLabel allTimeResourceLabel;

    public ResourcemanPanel(ResourcemanPlugin plugin)
    {
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        build();
    }

    private void build()
    {
        JLabel title = new JLabel("Resourceman Mode");
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        add(buildResourcesPanel(), BorderLayout.CENTER);

        JButton resetButton = new JButton("Reset All Data");
        resetButton.setBackground(new Color(150, 30, 30));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        resetButton.addActionListener(e ->
        {
            int first = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset ALL Resourceman Mode data?\nThis cannot be undone.",
                    "Reset Resourceman Data",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (first != JOptionPane.YES_OPTION)
            {
                return;
            }

            int second = JOptionPane.showConfirmDialog(
                    this,
                    "This will permanently delete all tracked resources.\nAre you ABSOLUTELY sure?",
                    "Final Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (second != JOptionPane.YES_OPTION)
            {
                return;
            }

            plugin.resetAllData();
        });

        add(resetButton, BorderLayout.SOUTH);
    }

    private JPanel buildResourcesPanel()
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        allTimeResourceLabel = new JLabel("All-time: 0 items");
        allTimeResourceLabel.setForeground(Color.WHITE);
        allTimeResourceLabel.setFont(FontManager.getRunescapeSmallFont());

        sessionResourceLabel = new JLabel("Session: 0 items");
        sessionResourceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        sessionResourceLabel.setFont(FontManager.getRunescapeSmallFont());

        header.add(allTimeResourceLabel);
        header.add(sessionResourceLabel);
        wrapper.add(header, BorderLayout.NORTH);

        resourceListPanel = new JPanel();
        resourceListPanel.setLayout(new BoxLayout(resourceListPanel, BoxLayout.Y_AXIS));
        resourceListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JScrollPane scroll = new JScrollPane(resourceListPanel);
        scroll.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    public void update()
    {
        SwingUtilities.invokeLater(() ->
        {
            ResourceTracker tracker = plugin.getResourceTracker();
            if (tracker == null)
            {
                return;
            }

            sessionResourceLabel.setText("Session: " +
                    String.format("%,d", tracker.getSessionResourceCount()) + " items");
            allTimeResourceLabel.setText("All-time: " +
                    String.format("%,d", tracker.getAllTimeResourceCount()) + " items");

            resourceListPanel.removeAll();

            ItemManager itemManager = plugin.getItemManager();

            for (Map.Entry<String, Integer> entry : tracker.getAllTimeResources().entrySet())
            {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(ColorScheme.DARK_GRAY_COLOR);
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARKER_GRAY_COLOR),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

                JLabel iconLabel = new JLabel();
                iconLabel.setPreferredSize(new Dimension(32, 32));
                try
                {
                    BufferedImage img = itemManager.getImage(
                            itemManager.search(entry.getKey())
                                    .stream()
                                    .findFirst()
                                    .map(r -> r.getId())
                                    .orElse(-1)
                    );
                    if (img != null)
                    {
                        iconLabel.setIcon(new ImageIcon(img));
                    }
                }
                catch (Exception e)
                {
                    // No icon
                }

                JLabel nameLabel = new JLabel(capitalize(entry.getKey()));
                nameLabel.setForeground(Color.WHITE);
                nameLabel.setFont(FontManager.getRunescapeSmallFont());

                JLabel quantLabel = new JLabel(String.format("%,d", entry.getValue()));
                quantLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                quantLabel.setFont(FontManager.getRunescapeSmallFont());

                JPanel namePanel = new JPanel(new BorderLayout());
                namePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
                namePanel.add(nameLabel, BorderLayout.WEST);
                namePanel.add(quantLabel, BorderLayout.EAST);

                row.add(iconLabel, BorderLayout.WEST);
                row.add(namePanel, BorderLayout.CENTER);
                resourceListPanel.add(row);
            }

            resourceListPanel.revalidate();
            resourceListPanel.repaint();
        });
    }

    private String capitalize(String str)
    {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words)
        {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1));
        }
        return sb.toString();
    }
}