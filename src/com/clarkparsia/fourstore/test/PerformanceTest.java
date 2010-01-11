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

import com.clarkparsia.fourstore.api.QueryException;
import com.clarkparsia.fourstore.api.Store;

import com.clarkparsia.fourstore.api.results.ResultSet;

import com.clarkparsia.fourstore.impl.StoreFactory;
import com.clarkparsia.fourstore.impl.TabbedResultSetFormatter;
import com.clarkparsia.utils.web.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Performance Tests</p>
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

                    ResultSet aResult = theRepo.query(aQuery);

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



	public static void main(String[] args) throws Exception {
		int NUM = 100;
		for (int i = 0; i < NUM; i++) {
			new WorkThread().start();
		}

		while (count < NUM) {
			System.err.println("Current count: " + count);
			System.err.println("Sleeping...");
			Thread.sleep(1000);
		}
		System.err.println("Done parallel execution");
		
		Store aStore = StoreFactory.create(new URL("http://localhost:8000/"), Method.GET);
		aStore.connect();
		long serialTime = 0;
		for (String aQuery : queriesRun) {
			long s = System.currentTimeMillis();
			try {
				aStore.query(aQuery);
			}
			catch (QueryException e) {
				System.err.println("serial execution error");
			}
			long e = System.currentTimeMillis();
			serialTime += (e-s);
		}

		System.err.println("complete...num correct? " + correct);
		System.err.println("serial time: " + serialTime);
		System.err.println("avg serial time: " + (serialTime/NUM));
		System.err.println("Total query time: " + time);
		System.err.println("avg query time: " + (time / NUM));
	}

	private static int count = 0;
	private static int correct = 0;
	private static long time = 0;
	private static Lock lock = new ReentrantLock();
	private static Random RANDOM = new Random();
	private static List<String> queriesRun = new ArrayList<String>();

	private static class WorkThread extends Thread {
		public void run() {
			try {
				Store aStore = StoreFactory.create(new URL("http://localhost:8000/"), Method.GET);

				aStore.connect();

				String aQuery = Queries.getSPARQLQueries()[RANDOM.nextInt(Queries.getSPARQLQueries().length)];

				lock.lock();
				queriesRun.add(aQuery);
				lock.unlock();


				long s = System.currentTimeMillis();
				ResultSet aResult = aStore.query(aQuery);
				long e = System.currentTimeMillis();

				lock.lock();
				time += (e-s);
				lock.unlock();

				String aFormattedResult = new TabbedResultSetFormatter().format(aResult);

				if (aFormattedResult.equals(ANSWER)) {
					lock.lock();
					correct++;
					lock.unlock();
				}

				aStore.disconnect();
			}
			catch (Exception e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			finally {
				lock.lock();
				count++;
				lock.unlock();
			}
		}
	}

	private static final String ANSWER = "type\t\n" +
					"---\n" +
					"http://www.clarkparsia.com/baseball/hand/Hand\t\n" +
					"http://www.w3.org/2003/01/geo/wgs84_pos#Point\t\n" +
					"http://www.clarkparsia.com/baseball/BattingStats\t\n" +
					"http://www.clarkparsia.com/baseball/Player\t\n" +
					"http://xmlns.com/foaf/0.1/Image\t\n" +
					"http://www.clarkparsia.com/baseball/FieldingStats\t\n" +
					"http://www.clarkparsia.com/baseball/league/League\t\n" +
					"http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing\t\n" +
					"http://www.clarkparsia.com/baseball/team/Team\t\n" +
					"http://www.clarkparsia.com/baseball/position/Position\t\n" +
					"http://www.clarkparsia.com/baseball/PitchingStats\t\n";
}
