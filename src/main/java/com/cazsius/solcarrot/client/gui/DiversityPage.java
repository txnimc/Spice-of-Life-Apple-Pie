package com.cazsius.solcarrot.client.gui;

import com.cazsius.solcarrot.client.gui.elements.UIBox;
import com.cazsius.solcarrot.client.gui.elements.UIElement;
import com.cazsius.solcarrot.client.gui.elements.UILabel;
import com.cazsius.solcarrot.tracking.FoodInstance;
import com.cazsius.solcarrot.tracking.FoodList;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import static com.cazsius.solcarrot.lib.Localization.localized;

public class DiversityPage extends Page {
    DiversityPage(double foodDiversity, Rectangle frame) {
        super(frame, localized("gui", "food_book.stats"));

//        mainStack.addChild(statWithIcon(
//                icon(FoodBookScreen.carrotImage),
//                "" + foodDiversity,
//                localized("gui", "food_book.stats.current_diversity")
//        ));

        // Dummy box to center the diversity display
        mainStack.addChild(new UIBox(new Rectangle(0, 0, 1, 35), new Color(0, 0, 0, 0)));

        UIElement diversityDisplay = statWithIcon(
                icon(FoodBookScreen.carrotImage),
                String.format("%.2f", foodDiversity),
                localized("gui", "food_book.stats.current_diversity")
        );
        mainStack.addChild(diversityDisplay);

        //mainStack.addChild(makeSeparatorLine());

        updateMainStack();
    }

}
