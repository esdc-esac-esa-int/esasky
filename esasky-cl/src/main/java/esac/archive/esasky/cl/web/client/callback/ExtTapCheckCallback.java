package esac.archive.esasky.cl.web.client.callback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExtTapCheckCallback extends JsonRequestCallback {
	private final long timecall;
	private final CommonTapDescriptor descriptor;
	private final CountStatus countStatus;
	private final String zeroCountMessage;

	private List<WavelengthCorrection> wavelengthCorrections;
	
	public ExtTapCheckCallback(String adql, CommonTapDescriptor descriptor, CountStatus countStatus,
			String progressIndicatorMessage, String zeroCountMessage) {
		super(progressIndicatorMessage, adql);
		this.timecall = System.currentTimeMillis();
		this.countStatus = countStatus;
		this.descriptor = descriptor;
		this.zeroCountMessage = zeroCountMessage;
		createWavelengthCorrections();
	}

	private void logReceived(int totalCount) {
	 	double timeForReceiving = (System.currentTimeMillis() - timecall)/1000.0;
	 	Log.debug("[ExtTapCheckCallback]" + descriptor.getLongName() + " Time for response (s): "
	 			+ timeForReceiving);
	 	
	 	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_EXTERNALTAPS, GoogleAnalytics.ACT_EXTTAP_COUNT,
	 			descriptor.getLongName() + " Time for response (s): " + timeForReceiving);

        Log.debug("[ExtTapCheckCallback]" + this.getClass().getSimpleName() + " " + descriptor.getLongName()
    	+ ": [" + totalCount + "] results found");
    
	}

	private boolean isValidUpdate() {
		boolean isValid = true;
		if(countStatus.getUpdateTime(descriptor) != null && countStatus.getUpdateTime(descriptor) > timecall) {
			Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
					+ timecall + " , dif:" + (countStatus.getUpdateTime(descriptor) - timecall));
			isValid = false;
		} else if(CoordinateUtils.getCenterCoordinateInJ2000().getFov() > descriptor.getFovLimit()){
			Log.warn(this.getClass().getSimpleName() + " discarded server answer to too large fov: "
					+ CoordinateUtils.getCenterCoordinateInJ2000().getFov());
			isValid = false;
		} else if (countStatus.isMarkedForRemoval(descriptor)) {
			Log.warn(this.getClass().getSimpleName() + " discarded server answer marked for removal");
			isValid = false;
		}
		return isValid;
	}

	@Override
	protected void onSuccess(final Response response) {
		Scheduler.get().scheduleFinally(() -> {

			if (!isValidUpdate()) {
				return;
			}

			GeneralJavaScriptObject responseObj = GeneralJavaScriptObject.createJsonObject(response.getText());

			GeneralJavaScriptObject data = responseObj.getProperty("data");
			GeneralJavaScriptObject metadata = responseObj.getProperty("metadata");
			GeneralJavaScriptObject[] formattedDataArr = GeneralJavaScriptObject.convertToArray(ExtTapUtils.formatExternalTapData(data, metadata));
			logReceived(formattedDataArr.length);
			countStatus.setCountDetails(descriptor, formattedDataArr.length, System.currentTimeMillis(), CoordinateUtils.getCenterCoordinateInJ2000());
			DescriptorCountAdapter dca = createDescriptors(formattedDataArr);
			CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(Arrays.asList(dca)));


			if (zeroCountMessage != null && !zeroCountMessage.isEmpty() && dca.isZeroCount()) {
				CommonEventBus.getEventBus().fireEvent(
						new ProgressIndicatorPushEvent(descriptor.getId() + "zeroCount", zeroCountMessage, true));

				new Timer() {
					@Override
					public void run() {
						CommonEventBus.getEventBus().fireEvent(
								new ProgressIndicatorPopEvent(descriptor.getId() + "zeroCount"));
					}
				}.schedule(20000);
			}
		});
	}
	
	private DescriptorCountAdapter createDescriptors(GeneralJavaScriptObject[] dataArr) {
		List<String> updatedIds = new LinkedList<>();

		for(GeneralJavaScriptObject data : dataArr) {
			int count = Integer.parseInt(data.getStringProperty("c"));
			final String column1Value = data.getStringProperty(descriptor.getGroupColumn1());
			final String column2Value = data.getStringProperty(descriptor.getGroupColumn2());

			Double emMin = data.getDoubleOrNullProperty("em_min");
			Double emMax = data.getDoubleOrNullProperty("em_max");

			emMin = scaleWavelength(emMin);
			emMax = scaleWavelength(emMax);


			// MAST workaround for incorrect table data..
			WavelengthCorrection wlc = wavelengthCorrections.stream().filter(c -> c.name.equalsIgnoreCase(column2Value)).findFirst().orElse(null);
			if ("MAST".equalsIgnoreCase(descriptor.getMission()) && wlc != null) {
				emMin = wlc.getWavelengthMin();
				emMax = wlc.getWavelengthMax();
			}


			String whereADQL = ExtTapUtils.createLevelDescriptorWhereADQL(descriptor.getGroupColumn1(), column1Value, descriptor.getGroupColumn2(), column2Value);
			String level2Name = column2Value != null && !column2Value.trim().isEmpty() ? column2Value : "Other";

			// Create or update level1 descriptor
			CommonTapDescriptor level1Descriptor;
			if (EsaSkyWebConstants.HEASARC_MISSION.equalsIgnoreCase(descriptor.getMission())) {
				Double regimeValue = WavelengthUtils.getWavelengthValueFromName(data.getStringProperty("regime"));
				if (regimeValue != null) {
					regimeValue = Math.min(regimeValue, 12);
					emMin = regimeValue;
					emMax = regimeValue;
				}

				// No level2 for HEASARC
				level1Descriptor = createOrUpdateLevelDescriptor(column1Value, descriptor, column2Value, whereADQL, emMin, emMax);
				updatedIds.add(level1Descriptor.getId());
				ExtTapUtils.setCount(descriptor, level1Descriptor, count);
			} else {
				// Create or update level1 descriptor
				level1Descriptor = createOrUpdateLevelDescriptor(column1Value, descriptor, descriptor.getTableName(), whereADQL, emMin, emMax);
				updatedIds.add(level1Descriptor.getId());
				ExtTapUtils.setCount(descriptor, level1Descriptor, count);
				// Create or update level2 descriptor
				CommonTapDescriptor level2Descriptor = createOrUpdateLevelDescriptor(level2Name, level1Descriptor, level1Descriptor.getTableName(), whereADQL, emMin, emMax);
				updatedIds.add(level2Descriptor.getId());
				ExtTapUtils.setCount(level1Descriptor, level2Descriptor, count);
			}
		}

		List<CommonTapDescriptor> descriptorList = new LinkedList<>();
		List<Integer> descriptorCountList = new LinkedList<>();

		// Add root descriptor
		descriptorList.add(descriptor);
		descriptorCountList.add(dataArr.length);

		// Remove children that is not part of the update
		descriptor.removeChildren(updatedIds);

		// Add all children
		List<CommonTapDescriptor> allChildren =  descriptor.getAllChildren();
		descriptorList.addAll(allChildren);
		descriptorCountList.addAll(allChildren.stream().map(TapDescriptorBase::getCount).collect(Collectors.toList()));

		CommonTapDescriptorList commonTapDescriptorList = new CommonTapDescriptorList();
		commonTapDescriptorList.setDescriptors(descriptorList);
		DescriptorCountAdapter countAdapter = new DescriptorCountAdapter(commonTapDescriptorList, EsaSkyWebConstants.CATEGORY_EXTERNAL, null);

		updateCount(descriptorList, descriptorCountList, countAdapter);

		return countAdapter;
	}

	private double scaleWavelength(Double em) {
		return em != null ? Math.min(Math.max(Math.abs(Math.log10(em)), 1), 12): 1;
	}

	private CommonTapDescriptor createOrUpdateLevelDescriptor(String name, CommonTapDescriptor parent, String tableName, String whereADQL, double emMin, double emMax) {
		CommonTapDescriptor descriptor1 = parent.getAllChildren().stream().filter(x -> Objects.equals(x.getLongName(), name)).findFirst().orElse(null);
		if (descriptor1 == null) {
			descriptor1 = ExtTapUtils.createLevelDescriptor(parent, name, tableName, whereADQL, emMin, emMax);
		} else {
			ExtTapUtils.updateLevelDescriptor(descriptor1, whereADQL, emMin, emMax);
		}

		return descriptor1;
	}


	private void updateCount(List<CommonTapDescriptor> descriptorList, List<Integer> descriptorCountList, DescriptorCountAdapter countAdapter) {
		for (int i = 0; i < descriptorList.size(); i++) {
			countAdapter.getCountStatus().setCount(descriptorList.get(i), descriptorCountList.get(i));
		}
	}

	private void createWavelengthCorrections() {
		WavelengthCorrection[] corrections = {
				new WavelengthCorrection("Kepler", 6.0, 6.4),
				new WavelengthCorrection("KeplerFFI", 6.0, 6.4),
				new WavelengthCorrection("K2", 6.0, 6.4),
				new WavelengthCorrection("K2FFI", 6.0, 6.4),
				new WavelengthCorrection("SPITZER_SHA", 3.7, 5.5),
				new WavelengthCorrection("GALEX", 6.6, 6.9),
				new WavelengthCorrection("TESS", 6.0, 6.2),
				new WavelengthCorrection("HST", 6.2, 6.8),
				new WavelengthCorrection("SWIFT", 6.2, 6.8),
				new WavelengthCorrection("HUT", 7.0, 7.4),
				new WavelengthCorrection("EUVE", 7.1, 8.1),
				new WavelengthCorrection("FUSE", 6.9, 7.1),
				new WavelengthCorrection("BEFS", 6.9, 7.1),
				new WavelengthCorrection("WUPPE", 6.5, 6.9),
				new WavelengthCorrection("TUES", 6.8, 7.0),
				new WavelengthCorrection("PS1", 5.9, 6.4),
				new WavelengthCorrection("HLA", 5.7, 6.8),
				new WavelengthCorrection("IUE", 6.9, 7.1),
				new WavelengthCorrection("HLSP", 5.5, 7.1)
		};

		wavelengthCorrections = Arrays.asList(corrections);
	}

	private class WavelengthCorrection {
		private final String name;
		private final Double wavelengthMin;
		private final Double wavelengthMax;

		public WavelengthCorrection(String name, Double wavelengthMin, Double wavelengthMax) {
			this.name = name;
			this.wavelengthMin = wavelengthMin;
			this.wavelengthMax = wavelengthMax;
		}

		public String getName() {
			return name;
		}

		public Double getWavelengthMin() {
			return wavelengthMin;
		}

		public Double getWavelengthMax() {
			return wavelengthMax;
		}
	}
	
}
