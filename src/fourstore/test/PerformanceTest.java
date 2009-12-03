package fourstore.test;

import fourstore.api.Store;

import fourstore.api.results.Binding;
import fourstore.api.results.ResultSet;

import fourstore.impl.StoreImpl;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 10:05:30 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class PerformanceTest {
	private static int NUM_TRIES = 25;

	private static void test(Store theRepo, String[] theQueries) throws Exception {
		int count = 1;
        for (String aQuery : theQueries) {
            long aTime = 0;
			List<Long> aTimeList = new ArrayList<Long>();

			long aMin = Long.MAX_VALUE;
			long aMax = Long.MIN_VALUE;

            for (int i = 0; i < NUM_TRIES; i++) {
                long aStart = 0, aEnd = 0;
                if (aQuery.startsWith("construct")) {
                    aStart = System.currentTimeMillis();
                    theRepo.constructQuery(aQuery);
                    aEnd = System.currentTimeMillis();
                }
                else {
                    aStart = System.currentTimeMillis();
                    ResultSet aResult = theRepo.query(aQuery);

//					for (Binding aBinding : aResult){}

					aEnd = System.currentTimeMillis();
                }

				long aRunTime = (aEnd - aStart);

                aTime += aRunTime;

				if (aRunTime > aMax) {
					aMax = aRunTime;
				}

				if (aRunTime < aMin) {
					aMin = aRunTime;
				}

				aTimeList.add(aRunTime);
            }

//			System.err.println();
//            System.err.println("Test Run for Query #" + count++);
//			System.err.println("Average time per query: " + (aTime / Main.NUM_TRIES));
//			System.err.println("Min query time: " + aMin);
//			System.err.println("Max query time: " + aMax);
//			System.err.println("Median query time: " + median(aTimeList));
//			System.err.println("StdDev of query times: " + stddev(aTimeList));
//			System.err.println("Total time: " + aTime);
//			System.err.println("Total Number of excutions: " + Main.NUM_TRIES);

			System.err.println(new StringBuffer().append(count++).append(", ").append((int) mean(aTimeList)).append(", ").
					append(aMin).append(", ").append(aMax).append(", ").append(median(aTimeList)).append(", ").
					append(stddev(aTimeList)).append(", ").append(aTime).append(", ").append(NUM_TRIES));
        }
	}

	private static double stddev(List<Long> theList) {
		double aMean = mean(theList);

		double aSquares = 0;

		for (Long aInt : theList) {
			aSquares += Math.pow(aInt - aMean, 2);
		}

		return Math.sqrt(aSquares / theList.size());
	}

	private static double mean(List<Long> theValues) {
        if (theValues.size() == 0) {
            return 0;
        }

        double aTotal = 0;

        for (Long aInt : theValues) {
            aTotal += aInt;
        }

        return aTotal / (double) theValues.size();
    }

    private static double median(List<Long> theList) {
        if (theList.size() == 0) {
            return 0;
        }

        Collections.sort(theList);

        return theList.get(theList.size() / 2);
    }

	public static void main(String[] args) throws Exception {
		Store aStore = new StoreImpl(new URL("http://hume.int.clarkparsia.com:8000/"));

		test(aStore, Queries.getSPARQLQueries());
	}
}
