package esac.archive.esasky.cl.web.client.callback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Response;
import esac.archive.esasky.cl.web.client.CommonEventBus;
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
	

	
	public ExtTapCheckCallback(String adql, CommonTapDescriptor descriptor, CountStatus countStatus,
			String progressIndicatorMessage) {
		super(progressIndicatorMessage, adql);
		this.timecall = System.currentTimeMillis();
		this.countStatus = countStatus;
		this.descriptor = descriptor;
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

			if ("HEASARC".equalsIgnoreCase(descriptor.getMission())) {
				Double regimeValue = WavelengthUtils.getWavelengthValueFromName(data.getStringProperty("regime"));
				if (regimeValue != null) {
					emMin = regimeValue;
					emMax = regimeValue;
				}
			}

			String whereADQL = ExtTapUtils.createLevelDescriptorWhereADQL(descriptor.getGroupColumn1(), column1Value, descriptor.getGroupColumn2(), column2Value);

			// Create or update level1 descriptor
			CommonTapDescriptor level1Descriptor = createOrUpdateLevelDescriptor(column1Value, descriptor, whereADQL, emMin, emMax);
			updatedIds.add(level1Descriptor.getId());
			ExtTapUtils.setCount(descriptor, level1Descriptor, count);

			// Create or update level1 descriptor
			String name = column2Value != null && !column2Value.trim().isEmpty() ? column2Value : "Other";
			CommonTapDescriptor level2Descriptor = createOrUpdateLevelDescriptor(name, level1Descriptor, whereADQL, emMin, emMax);

			updatedIds.add(level2Descriptor.getId());
			ExtTapUtils.setCount(level1Descriptor, level2Descriptor, count);
		}

		List<CommonTapDescriptor> descriptorList = new LinkedList<>();
		List<Integer> descriptorCountList = new LinkedList<>();


		if (dataArr.length > 0) {
			// Add root descriptor
			descriptorList.add(descriptor);
			descriptorCountList.add(dataArr.length);

			// Remove children that is not part of the update
			descriptor.removeChildren(updatedIds);

			// Add all children
			List<CommonTapDescriptor> allChildren =  descriptor.getAllChildren();
			descriptorList.addAll(allChildren);
			descriptorCountList.addAll(allChildren.stream().map(TapDescriptorBase::getCount).collect(Collectors.toList()));
		}


		CommonTapDescriptorList commonTapDescriptorList = new CommonTapDescriptorList();
		commonTapDescriptorList.setDescriptors(descriptorList);
		DescriptorCountAdapter countAdapter = new DescriptorCountAdapter(commonTapDescriptorList, EsaSkyWebConstants.CATEGORY_EXTERNAL, null);

		updateCount(descriptorList, descriptorCountList, countAdapter);

		return countAdapter;
	}

	private double scaleWavelength(Double em) {
		return em != null ? Math.abs(Math.log10(em)) : 0;
	}

	private CommonTapDescriptor createOrUpdateLevelDescriptor(String name, CommonTapDescriptor parent, String whereADQL, double emMin, double emMax) {
		CommonTapDescriptor descriptor1 = parent.getAllChildren().stream().filter(x -> Objects.equals(x.getLongName(), name)).findFirst().orElse(null);
		if (descriptor1 == null) {
			descriptor1 = ExtTapUtils.createLevelDescriptor(parent, name, whereADQL, emMin, emMax);
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
	
}
