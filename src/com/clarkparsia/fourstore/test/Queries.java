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

/**
 * <p>Queries used for testing</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class Queries {

	public static String[] getQueries() {
		return new String[] {
				// query 1
			 "construct *\n" +
			 "from {s} p {o}, [{p} rdfs:label {p_label}], [{o} po {oo}, [{po} rdfs:label {po_label}]] \n" +
			 "where s = <http://www.clarkparsia.com/baseball/position/FirstBase>",

				// query 2
			 "construct *\n" +
			 "from {s} p {o}, [{p} rdfs:label {p_label}], [{o} po {oo}, [{po} rdfs:label {po_label}]] \n" +
			 "where s = <http://www.clarkparsia.com/baseball/ARI>",

				// query 3
			 "construct *\n" +
			 "from {s} p {o}, [{p} rdfs:label {p_label}], [{o} po {oo}, [{po} rdfs:label {po_label}]] \n" +
			 "where s = <http://www.clarkparsia.com/baseball/donnech01>",

				// query 4
			 "select  distinct uri, aLabel from\n" +
			 "{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
			 "{phantom_ltkx} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
			 "{phantom_ltkx} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
			 "{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
			 "{phantom_ltkx} <http://www.clarkparsia.com/baseball/team> {uri},\n" +
			 "{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
			 "[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
			 "where (var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) limit 10000",

				// query 5
			 "select distinct aInst, aLabel from {aInst} rdfs:label {aLabel}",

				// query 6
			 "select  distinct uri, aLabel from\n" +
			 "{phantom_qeyu} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
			 "{phantom_qeyu} <http://www.clarkparsia.com/baseball/position> {uri},\n" +
			 "{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
			 "{var1} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
			 "{phantom_qeyu} <http://www.clarkparsia.com/baseball/team> {var1},\n" +
			 "{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
			 "[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}] limit 10000",

				// query 7
				"select  distinct uri, aLabel from\n" +
				"{phantom_unrd} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{phantom_unrd} <http://www.clarkparsia.com/baseball/player> {uri},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{var1} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom_unrd} <http://www.clarkparsia.com/baseball/team> {var1},\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where ((var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) and (var1 = <http://www.clarkparsia.com/baseball/ARI>))",

				// query 8
				"select  distinct uri, aLabel from\n" +
				"{phantom_eoej} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{phantom_eoej} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom_klll} <http://www.clarkparsia.com/baseball/battingAverage> {uri},\n" +
				"{goal_base} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_klll},\n" +
				"{var2} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom_eoej} <http://www.clarkparsia.com/baseball/team> {var2},\n" +
				"{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where (var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) limit 10000",

				// query 9
				"select  distinct uri, aLabel from\n" +
				"{phantom_uuci} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
				"{phantom_uuci} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{goal_base} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_ahox},\n" +
				"{phantom_ahox} <http://www.clarkparsia.com/baseball/battingAverage> {var1},\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom_uuci} <http://www.clarkparsia.com/baseball/team> {uri},\n" +
				"{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where ((var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) and (var1 = \"0.30754352030947774\"^^<http://www.w3.org/2001/XMLSchema#float>)) limit 10000",

				// query 10
				"select  distinct uri, aLabel from \n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom_pmgm} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{phantom_pmgm} <http://www.clarkparsia.com/baseball/player> {uri},\n" +
				"{phantom_qxhi} <http://www.clarkparsia.com/baseball/battingAverage> {var1},\n" +
				"{uri} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_qxhi},\n" +
				"{phantom_pmgm} <http://www.clarkparsia.com/baseball/team> {var2},\n" +
				"{var2} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where ((var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) and ((var1 > \"0.30754352030947774\"^^<http://www.w3.org/2001/XMLSchema#float>) and (var2 = <http://www.clarkparsia.com/baseball/BRO>))) limit 10000",

				// query 11
				"select  distinct var0, var1, var2, var3 from\n" +
				"{phantom_dexj} <http://www.clarkparsia.com/baseball/player> {var3},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom_dexj} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{var3} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_auvr},\n" +
				"{phantom_auvr} <http://www.clarkparsia.com/baseball/battingAverage> {var1},\n" +
				"{var2} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom_dexj} <http://www.clarkparsia.com/baseball/team> {var2},\n" +
				"{var3} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>}\n" +
				"where ((var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) and (var1 > \"0.30754352030947774\"^^<http://www.w3.org/2001/XMLSchema#float>))",

				// query 12
				"select  distinct var0, var1, var2, var3 from\n" +
				"{phantom_nzqs} <http://www.clarkparsia.com/baseball/player> {var3},\n" +
				"{phantom_nzqs} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{var3} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_lnow},\n" +
				"{phantom_lnow} <http://www.clarkparsia.com/baseball/battingAverage> {var1},\n" +
				"{phantom_nzqs} <http://www.clarkparsia.com/baseball/team> {var2},\n" +
				"{var2} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{var3} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>}\n" +
				"where ((var1 > \"0.30754352030947774\"^^<http://www.w3.org/2001/XMLSchema#float>) and (var2 = <http://www.clarkparsia.com/baseball/BRO>))",

				// query 13
				"select  distinct var0, var1, var2, var3 from\n" +
				"{phantom_osrr} <http://www.clarkparsia.com/baseball/player> {var3},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom_osrr} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{phantom_kjvy} <http://www.clarkparsia.com/baseball/battingAverage> {var1},\n" +
				"{var3} <http://www.clarkparsia.com/baseball/careerBatting> {phantom_kjvy},\n" +
				"{phantom_osrr} <http://www.clarkparsia.com/baseball/team> {var2},\n" +
				"{var2} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{var3} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>}\n" +
				"where ((var0 = <http://www.clarkparsia.com/baseball/position/FirstBase>) and (var2 = <http://www.clarkparsia.com/baseball/BRO>))",

				// new queries:
				// query 14
				"select  distinct uri, aLabel\n" +
				"from\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/position> {uri},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/team> {var1},\n" +
				"{var1} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{goal_base} <http://www.clarkparsia.com/baseball/careerBatting> {phantom1},\n" +
				"{phantom1} <http://www.clarkparsia.com/baseball/homeruns> {var2},\n" +
				"{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				" limit 10000",

				// query 15
				"select  distinct uri, aLabel\n" +
				"from\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/team> {uri},\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{goal_base} <http://www.clarkparsia.com/baseball/careerBatting> {phantom1},\n" +
				"{phantom1} <http://www.clarkparsia.com/baseball/homeruns> {var2},\n" +
				"{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where\n" +
				"(var0 = <http://www.clarkparsia.com/baseball/position/LeftField>)\n" +
				" limit 10000",

				// query 16
				"select  distinct uri, aLabel\n" +
				"from\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/player> {goal_base},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{var1} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/team> {var1},\n" +
				"{phantom1} <http://www.clarkparsia.com/baseball/homeruns> {uri},\n" +
				"{goal_base} <http://www.clarkparsia.com/baseball/careerBatting> {phantom1},\n" +
				"{goal_base} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where\n" +
				"((var0 = <http://www.clarkparsia.com/baseball/position/LeftField>) and (var1 = <http://www.clarkparsia.com/baseball/BAL>))\n" +
				" limit 10000",

				// query 17
				"select  distinct uri, aLabel\n" +
				"from\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/position> {var0},\n" +
				"{var0} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/position/Position>},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/player> {uri},\n" +
				"{var1} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/team/Team>},\n" +
				"{phantom0} <http://www.clarkparsia.com/baseball/team> {var1},\n" +
				"{uri} <http://www.clarkparsia.com/baseball/careerBatting> {phantom1},\n" +
				"{phantom1} <http://www.clarkparsia.com/baseball/homeruns> {var2},\n" +
				"{uri} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> {<http://www.clarkparsia.com/baseball/Player>},\n" +
				"[{uri} <http://www.w3.org/2000/01/rdf-schema#label> {aLabel}]\n" +
				"where\n" +
				"((var0 = <http://www.clarkparsia.com/baseball/position/LeftField>) and ((var1 = <http://www.clarkparsia.com/baseball/BAL>) and (var2 >= \"105\"^^<http://www.w3.org/2001/XMLSchema#integer>)))\n" +
				" limit 10000",
		};
	}

	public static String[] getSPARQLQueries() {
		return new String[] {
				"construct  { \n" +
				"\t?s ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t?s ?p ?o .\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo .\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label } .\n" +
				"\t}.\n" +
				"\tFILTER (?s =  <http://www.clarkparsia.com/baseball/position/FirstBase>)\n" +
				"}",

				// #2
				"construct  { \n" +
				"\t?s ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t?s ?p ?o.\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label. } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo.\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label.} .\n" +
				"\t}.\n" +
				"\tFILTER (?s =  <http://www.clarkparsia.com/baseball/ARI>).\n" +
				"}",

				// #3
				"construct  { \n" +
				"\t?s ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t?s ?p ?o.\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label. } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo.\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label.} .\n" +
				"\t}.\n" +
				"\tFILTER (?s =  <http://www.clarkparsia.com/baseball/donnech01>).\n" +
				"}",

				// #4
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"}\n" +
				"LIMIT 10000",

				// #5
				"SELECT DISTINCT ?aInst ?aLabel \n" +
				"WHERE {\n" +
				"\t?aInst <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}",

				// #6
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/position> ?uri.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t\t}.\n" +
				"}\n" +
				"LIMIT 10000",

				// #7
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"\tFILTER (?var1 =  <http://www.clarkparsia.com/baseball/ARI>).\n" +
				"}",

				// #8
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?phantom_klll <http://www.clarkparsia.com/baseball/battingAverage> ?uri.\n" +
				"\t?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_klll.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"}\n" +
				"LIMIT 10000",

				// #9
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_ahox.\n" +
				"\t?phantom_ahox <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"\tFILTER (?var1 =  \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"}\n" +
				"LIMIT 10000",

				// #10
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"\t?phantom_qxhi <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?uri <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_qxhi.\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"\tFILTER (?var2 =  <http://www.clarkparsia.com/baseball/BRO>).\n" +
				"}\n" +
				"LIMIT 10000",

				// #11
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_auvr.\n" +
				"\t?phantom_auvr <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"}",

				// #12
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_lnow.\n" +
				"\t?phantom_lnow <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"\tFILTER (?var2 =  <http://www.clarkparsia.com/baseball/BRO>).\n" +
				"}",

				// #13
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?phantom_kjvy <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_kjvy.\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tFILTER (?var0 =  <http://www.clarkparsia.com/baseball/position/FirstBase>).\n" +
				"\tFILTER (?var2 =  <http://www.clarkparsia.com/baseball/BRO>).\n" +
				"}",

				// #14
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?uri.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"} LIMIT 10000",

				// 15
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}\n" +
				" .\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"} LIMIT 10000",

				// 16
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?uri.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"FILTER(?var1 =  <http://www.clarkparsia.com/baseball/BAL>).\n" +
				"} LIMIT 10000",

				// 17
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?uri <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"FILTER(?var1 =  <http://www.clarkparsia.com/baseball/BAL>).\n" +
				"FILTER(?var2 >= \"105\"^^<http://www.w3.org/2001/XMLSchema#integer>).\n" +
				"} LIMIT 10000",
		};
	}

	public static String[] getOptimizedSPARQLQueries() {
		return new String[] {
				"construct  { \n" +
				"\t<http://www.clarkparsia.com/baseball/position/FirstBase> ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t<http://www.clarkparsia.com/baseball/position/FirstBase> ?p ?o.\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo.\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label } \n" +
				"\t}\n}",

				// #2
				"construct  { \n" +
				"\t<http://www.clarkparsia.com/baseball/ARI> ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t<http://www.clarkparsia.com/baseball/ARI> ?p ?o.\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label. } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo.\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label } .\n" +
				"\t}\n}",

				// #3
				"construct  { \n" +
				"\t<http://www.clarkparsia.com/baseball/donnech01> ?p ?o . \n" +
				"\t?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label . \n" +
				"\t?o ?po ?oo . \n" +
				"\t?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label  \n" +
				"} \n" +
				"WHERE {\n" +
				"\t<http://www.clarkparsia.com/baseball/donnech01> ?p ?o.\n" +
				"\tOPTIONAL { ?p <http://www.w3.org/2000/01/rdf-schema#label> ?p_label. } .\n" +
				"\tOPTIONAL { \n" +
				"\t\t?o ?po ?oo.\n" +
				"\t\tOPTIONAL { ?po <http://www.w3.org/2000/01/rdf-schema#label> ?po_label.} .\n" +
				"\t}\n}",

				// #4
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_ltkx <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}\n}\n" +
				"LIMIT 10000",

				// #5
				"SELECT DISTINCT ?aInst ?aLabel \n" +
				"WHERE {\n" +
				"\t?aInst <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}",

				// #6
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/position> ?uri.\n" +
				"\t?phantom_qeyu <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t\t}.\n" +
				"}\n" +
				"LIMIT 10000",

				// #7
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/team> <http://www.clarkparsia.com/baseball/ARI>.\n" +
				"\t?phantom_unrd <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}\n" +
				"}",

				// #8
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?phantom_klll <http://www.clarkparsia.com/baseball/battingAverage> ?uri.\n" +
				"\t?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_klll.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_eoej <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}\n" +
				"}\n" +
				"LIMIT 10000",

				// #9
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_ahox.\n" +
				"\t?phantom_ahox <http://www.clarkparsia.com/baseball/battingAverage> \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_uuci <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"\t?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"}\n" +
				"LIMIT 10000",

				// #10
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE {\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"\t?phantom_qxhi <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?uri <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_qxhi.\n" +
				"\t?phantom_pmgm <http://www.clarkparsia.com/baseball/team> <http://www.clarkparsia.com/baseball/BRO>.\n" +
				"\t?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tOPTIONAL {\n" +
				"\t\t?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"\t}.\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"}\n" +
				"LIMIT 10000",

				// #11
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_auvr.\n" +
				"\t?phantom_auvr <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?var2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"\t?phantom_dexj <http://www.clarkparsia.com/baseball/team> ?var2.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"}",

				// #12
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"\t?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_lnow.\n" +
				"\t?phantom_lnow <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?phantom_nzqs <http://www.clarkparsia.com/baseball/team> <http://www.clarkparsia.com/baseball/BRO>.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"\tFILTER (?var1 > \"3.0754352030947774E-1\"^^<http://www.w3.org/2001/XMLSchema#float>).\n" +
				"}",

				// #13
				"SELECT DISTINCT ?var0 ?var1 ?var2 ?var3 \n" +
				"WHERE {\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/player> ?var3.\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/position> <http://www.clarkparsia.com/baseball/position/FirstBase>.\n" +
				"\t?phantom_kjvy <http://www.clarkparsia.com/baseball/battingAverage> ?var1.\n" +
				"\t?var3 <http://www.clarkparsia.com/baseball/careerBatting> ?phantom_kjvy.\n" +
				"\t?phantom_osrr <http://www.clarkparsia.com/baseball/team> <http://www.clarkparsia.com/baseball/BRO>.\n" +
				"\t?var3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"}",

				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?uri.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"} LIMIT 10000",

				// 15
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?uri.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}\n" +
				" .\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"} LIMIT 10000",

				// 16
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?goal_base.\n" +
				"?goal_base <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?uri.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?goal_base <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"FILTER(?var1 =  <http://www.clarkparsia.com/baseball/BAL>).\n" +
				"} LIMIT 10000",

				// 17
				"SELECT DISTINCT ?uri ?aLabel \n" +
				"WHERE \n" +
				"{\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/position> ?var0.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/team> ?var1.\n" +
				"?phantom0 <http://www.clarkparsia.com/baseball/player> ?uri.\n" +
				"?uri <http://www.clarkparsia.com/baseball/careerBatting> ?phantom1.\n" +
				"?phantom1 <http://www.clarkparsia.com/baseball/homeruns> ?var2.\n" +
				"?var0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/position/Position>.\n" +
				"?var1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/team/Team>.\n" +
				"?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.clarkparsia.com/baseball/Player>.\n" +
				"OPTIONAL {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?aLabel.\n" +
				"}.\n" +
				"FILTER(?var0 =  <http://www.clarkparsia.com/baseball/position/LeftField>).\n" +
				"FILTER(?var1 =  <http://www.clarkparsia.com/baseball/BAL>).\n" +
				"FILTER(?var2 >= \"105\"^^<http://www.w3.org/2001/XMLSchema#integer>).\n" +
				"} LIMIT 10000",
		};
	}

}