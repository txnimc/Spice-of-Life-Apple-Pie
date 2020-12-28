package com.kevun1.solpotato.utils;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.tracking.benefits.AttributeBenefit;
import com.kevun1.solpotato.tracking.benefits.Benefit;
import com.kevun1.solpotato.tracking.benefits.EffectBenefit;

import java.util.ArrayList;
import java.util.List;

public class BenefitsParser {
    public static List<List<Benefit>> parse(List<String> unparsed) {
        List<List<Benefit>> allBenefits = new ArrayList<>();

        for (String s : unparsed) {
            String[] thresholdBenefitsString = s.split(";", 0);
            List<Benefit> thresholdBenefits = new ArrayList<>();

            for (String benefitString : thresholdBenefitsString) {
                String[] benefitArgs = benefitString.split(",", 0);
                int len = benefitArgs.length;

                if (len < 2) {
                    SOLPotato.LOGGER.warn("Invalid benefit specification: " + benefitString);
                    continue;
                }

                String benefitType;
                if (benefitArgs[0].equals("attribute")) {
                    if (len < 3) {
                        SOLPotato.LOGGER.warn("Need to specify a value when defining an attribute benefit: " + benefitString);
                    }
                    benefitType = "attribute";
                } else if (benefitArgs[0].equals("effect")) {
                    benefitType = "effect";
                } else {
                    SOLPotato.LOGGER.warn("Invalid benefit type: " + benefitArgs[0] + " in string " + benefitString);
                    continue;
                }

                String benefitName = benefitArgs[1];
                double benefitValue = 0;
                if (len > 2) {
                    benefitValue = Double.parseDouble(benefitArgs[2]);
                }

                if (benefitType.equals("attribute")) {
                    thresholdBenefits.add(new AttributeBenefit(benefitName, benefitValue));
                }
                else if (benefitType.equals("effect")) {
                    thresholdBenefits.add(new EffectBenefit(benefitName, benefitValue));
                }
            }
            allBenefits.add(thresholdBenefits);
        }

        return allBenefits;
    }
}
