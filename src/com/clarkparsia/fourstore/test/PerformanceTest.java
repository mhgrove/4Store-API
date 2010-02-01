/*
 * Copyright (c) 2005-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarkparsia.fourstore.test;

import com.clarkparsia.fourstore.api.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.query.TupleQueryResult;

/**
 * <p>Simple Performance Tests</p>
 *
 * @author Michael Grove
 * @since 0.1
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

                    TupleQueryResult aResult = theRepo.query(aQuery);
					while (aResult.hasNext()) {
						aResult.next();
					}

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
}
