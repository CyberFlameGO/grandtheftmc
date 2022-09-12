package prices;

import net.grandtheftmc.vice.pickers.drugdealer.data.PricingData;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 18/06/2017.
 */
public class TestPrices {

    public static void main(String[] args) {
        calculatePrices();
    }

    private static Map<String, Double> currentPrices;

    static {
        //Initialise current prices
        currentPrices = new HashMap<>();
        currentPrices.put("cocain", 200.0);
        currentPrices.put("weed", 850.0);
        currentPrices.put("lsd", 2500.50);
        currentPrices.put("mdma", 1367.50);
        currentPrices.put("heroin", 1100.0);
    }

    protected static Map<String, Double> calculatePrices() {

        Map<String, PricingData> data = new HashMap<>();
        double totalSales = 0;

        //Set current transaction data
        data.put("cocain", new PricingData(150, 250, 40000));
        totalSales += 40000;
        data.put("weed", new PricingData(700, 900, 45278));
        totalSales += 45278;
        data.put("lsd", new PricingData(2000, 3000, 65000));
        totalSales += 65000;
        data.put("mdma", new PricingData(1200, 1500, 54214));
        totalSales += 54214;
        data.put("heroin", new PricingData(850, 1350, 59004));
        totalSales += 59004;

        double totalRelativeMarketShare = 0;

        for (PricingData pd : data.values()) {
            //Compute the market share of each drug
            double share = pd.computeMarketShare(totalSales);

            double amntAbove = 0, amntBelow = 0;
            double pcentAbove = 0, pcentBelow = 0;
            //We want to compute the average percentage of market share above and below this market share

            for (PricingData pd2 : data.values()) {
                if (!pd2.equals(pd)) {
                    //we are comparing a different object

                    //Sum the market share percentages above and below this object
                    if (pd2.getMarketShare() > share) {
                        amntAbove++;
                        pcentAbove += pd2.getMarketShare();
                    } else if (pd2.getMarketShare() < share) {
                        amntBelow++;
                        pcentBelow += pd2.getMarketShare();
                    }
                }
            }

            //compute the averages
            double avgPcentAbove = 0, avgPcentBelow = 0;

            if (amntAbove != 0) {
                avgPcentAbove = pcentAbove / amntAbove;
            }

            if (amntBelow != 0) {
                avgPcentBelow = pcentBelow / amntBelow;
            }

            //Compute the average change in market share compared to all other drugs
            double diffShare = avgPcentBelow - avgPcentAbove;
            pd.setRelativeMarketShare(diffShare);
            totalRelativeMarketShare += diffShare;
        }

        double correctionFactor = 0;

        if (totalRelativeMarketShare != 0) {
            //we need to normalise the relative differences to all average 0.
            correctionFactor = totalRelativeMarketShare / ((double) data.size());

            if (totalRelativeMarketShare < 0 && correctionFactor < 0) {
                //Make positive
                correctionFactor *= -1;
            } else if (totalRelativeMarketShare > 0 && correctionFactor > 0) {
                correctionFactor *= -1;
            }

        }

        //Now scale all results so that no items exceeds +/- 10% of its price
        double maxVariant = Double.MIN_VALUE;

        for (PricingData pd : data.values()) {
            //if this is negative we add it, if positive adding works also
            pd.setRelativeMarketShare(pd.getRelativeMarketShare() + correctionFactor);


            double rms = Math.abs(pd.getRelativeMarketShare());
            if (rms > maxVariant) {
                //set the max variant to equal the largest relative percentage change in market share
                maxVariant = rms;
            }
        }


        //If there is some +/- variant of more than 10% we must scale all values down
        while (maxVariant > 0.1) {

            for (PricingData pd : data.values()) {
                //Half everything constantly
                pd.setRelativeMarketShare(pd.getRelativeMarketShare() / 2);
            }

            maxVariant = Double.MIN_VALUE;
            for (PricingData pd : data.values()) {
                if (pd.getRelativeMarketShare() > maxVariant) {
                    maxVariant = pd.getRelativeMarketShare();
                }
            }
        }

        Map<String, Double> priceData = new HashMap<>();

        DecimalFormat df = new DecimalFormat("##.##");

        //Now calculate and set the price data
        for (Map.Entry<String, PricingData> e : data.entrySet()) {
            PricingData pd = e.getValue();
            double curPrice = currentPrices.get(e.getKey());
            //Increasing or decreasing will still work
            double newPrice = curPrice + (curPrice * pd.getRelativeMarketShare());

            if (newPrice > pd.getMaxCost()) {
                newPrice = pd.getMaxCost();
            } else if (newPrice < pd.getMinCost()) {
                newPrice = pd.getMinCost();
            }

            priceData.put(e.getKey(), newPrice);

            System.out.println("Drug(" + e.getKey() + "). Market Share= " + df.format(pd.getMarketShare())
                    + ". Relative Share= " + df.format(pd.getRelativeMarketShare()) + ". Old->New Price   " +
                    df.format(curPrice)  +"-->" + df.format(newPrice));
        }

        //We are done.
        return priceData;
    }

}
