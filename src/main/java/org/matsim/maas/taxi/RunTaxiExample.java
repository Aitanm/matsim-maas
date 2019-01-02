/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.maas.taxi;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.optimizer.DefaultTaxiOptimizerProvider;
import org.matsim.contrib.taxi.optimizer.TaxiOptimizer;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiModule;
import org.matsim.contrib.taxi.run.TaxiQSimModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import com.google.inject.Provider;

/**
 * An example class to Run a taxi scenario based on a config file.
 * Note that several different optimizers may be set directly within the config file.
 * Two examples are provided here: A rulebased and an assignment based dispatch algorithm.
 */

public class RunTaxiExample {
	public static final String CONFIG_FILE_RULEBASED = "scenarios/mielec_2014_02/mielec_taxi_config_rulebased.xml";
	public static final String CONFIG_FILE_ASSIGNMENT = "scenarios/mielec_2014_02/mielec_taxi_config_assignment.xml";

	public static void run(String configFile, boolean otfvis, int lastIteration) {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new TaxiConfigGroup(), new DvrpConfigGroup(),
				new OTFVisConfigGroup());
		config.controler().setLastIteration(lastIteration);
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);

		String mode = TaxiConfigGroup.get(config).getMode();
		final boolean ownTaxiOptimizer = false;
		if (!ownTaxiOptimizer) {
			controler.addQSimModule(new TaxiQSimModule());
			// (default taxi optimizer)
		} else {
			controler.addQSimModule(new TaxiQSimModule(MyTaxiOptimizerProvider.class));
			// (implement your own taxi optimizer)
		}

		controler.addOverridingModule(DvrpModule.createModuleWithDefaultDvrpModeQSimModule(mode));
		controler.addOverridingModule(new TaxiModule());

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}

	public static void main(String[] args) {
		//RunTaxiExample.run(CONFIG_FILE_RULEBASED,false, 0); // switch to 'true' to turn on visualisation
		RunTaxiExample.run(CONFIG_FILE_ASSIGNMENT, false, 0); // switch to 'true' to turn on visualisation
	}

	/**
	 * See {@link DefaultTaxiOptimizerProvider} for examples.
	 */
	private static class MyTaxiOptimizerProvider implements Provider<TaxiOptimizer> {
		@Override
		public TaxiOptimizer get() {
			return new TaxiOptimizer() {
				@Override
				public void vehicleEnteredNextLink(Vehicle vehicle, Link nextLink) {
					// TODO Auto-generated method stub
				}

				@Override
				public void requestSubmitted(Request request) {
					// TODO Auto-generated method stub
				}

				@Override
				public void nextTask(Vehicle vehicle) {
					// TODO Auto-generated method stub
				}

				@Override
				public void notifyMobsimBeforeSimStep(@SuppressWarnings("rawtypes") MobsimBeforeSimStepEvent e) {
					// TODO Auto-generated method stub
				}
			};
		}
	}
}
