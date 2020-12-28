package com.kevun1.solpotato.client.gui;

import com.kevun1.solpotato.client.gui.elements.UIBox;
import com.kevun1.solpotato.client.gui.elements.UIElement;

import java.awt.*;

import static com.kevun1.solpotato.lib.Localization.localized;

public class DiversityPage extends Page {
    DiversityPage(double foodDiversity, Rectangle frame) {
        super(frame, localized("gui", "food_book.stats"));

        // Dummy box to center the diversity display
        mainStack.addChild(new UIBox(new Rectangle(0, 0, 1, 35), new Color(0, 0, 0, 0)));

        UIElement diversityDisplay = statWithIcon(
                icon(FoodBookScreen.carrotImage),
                String.format("%.2f", foodDiversity),
                localized("gui", "food_book.stats.current_diversity")
        );
        mainStack.addChild(diversityDisplay);

        updateMainStack();
    }

}
