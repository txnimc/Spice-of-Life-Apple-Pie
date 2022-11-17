package com.tarinoita.solapplepie.utils;

import com.tarinoita.solapplepie.SOLApplePie;
import com.tarinoita.solapplepie.tracking.benefits.AttributeBenefit;
import com.tarinoita.solapplepie.tracking.benefits.Benefit;
import com.tarinoita.solapplepie.tracking.benefits.EffectBenefit;

import java.util.ArrayList;
import java.util.List;

public class BenefitsParser {
    public static List<List<Benefit>> parse(List<String> unparsed) {
        List<List<Benefit>> allBenefits = new ArrayList<>();

        int thresh = 0;
        for (String s : unparsed) {
            String[] thresholdBenefitsString = s.split(";", 0);
            List<Benefit> thresholdBenefits = new ArrayList<>();

            for (String benefitString : thresholdBenefitsString) {
                String[] benefitArgs = benefitString.split(",", 0);
                int len = benefitArgs.length;

                if (len < 2) {
                    SOLApplePie.LOGGER.warn("Invalid benefit specification: " + benefitString);
                    continue;
                }

                String benefitType;
                if (benefitArgs[0].equals("attribute")) {
                    if (len < 3) {
                        SOLApplePie.LOGGER.warn("Need to specify a value when defining an attribute benefit: " + benefitString);
                    }
                    benefitType = "attribute";
                } else if (benefitArgs[0].equals("effect")) {
                    benefitType = "effect";
                } else {
                    SOLApplePie.LOGGER.warn("Invalid benefit type: " + benefitArgs[0] + " in string " + benefitString);
                    continue;
                }

                String benefitName = benefitArgs[1];
                double benefitValue = 0;
                if (len > 2) {
                    benefitValue = Double.parseDouble(benefitArgs[2]);
                }

                if (benefitType.equals("attribute")) {
                    thresholdBenefits.add(new AttributeBenefit(benefitName, benefitValue, thresh));
                }
                else if (benefitType.equals("effect")) {
                    thresholdBenefits.add(new EffectBenefit(benefitName, benefitValue, thresh));
                }
            }
            allBenefits.add(thresholdBenefits);
            thresh++;
        }

        return allBenefits;
    }
}
